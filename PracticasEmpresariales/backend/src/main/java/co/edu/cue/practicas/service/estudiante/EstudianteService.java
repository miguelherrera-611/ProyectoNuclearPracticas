package co.edu.cue.practicas.service.estudiante;

import co.edu.cue.practicas.audit.ModuloAuditoria;
import co.edu.cue.practicas.audit.singleton.AuditoriaLogger;
import co.edu.cue.practicas.dto.request.CrearEstudianteRequest;
import co.edu.cue.practicas.dto.request.EnviarAProcesoRequest;
import co.edu.cue.practicas.dto.response.EstudianteResponse;
import co.edu.cue.practicas.event.EstudiantesEnviadosEvent;
import co.edu.cue.practicas.event.UsuarioCreadoEvent;
import co.edu.cue.practicas.exception.OperacionNoPermitidaException;
import co.edu.cue.practicas.exception.RecursoNoEncontradoException;
import co.edu.cue.practicas.model.entity.BitacoraAuditoria;
import co.edu.cue.practicas.model.entity.Estudiante;
import co.edu.cue.practicas.model.entity.Facultad;
import co.edu.cue.practicas.model.entity.Programa;
import co.edu.cue.practicas.model.entity.Usuario;
import co.edu.cue.practicas.model.enums.EstadoCuenta;
import co.edu.cue.practicas.model.enums.EstadoEstudiante;
import co.edu.cue.practicas.model.enums.Rol;
import co.edu.cue.practicas.model.enums.TipoAccion;
import co.edu.cue.practicas.repository.estudiante.EstudianteRepository;
import co.edu.cue.practicas.repository.facultad.FacultadRepository;
import co.edu.cue.practicas.repository.programa.ProgramaRepository;
import co.edu.cue.practicas.repository.usuario.UsuarioRepository;
import co.edu.cue.practicas.security.CustomUserDetails;
import co.edu.cue.practicas.security.annotation.RequiereRol;
import co.edu.cue.practicas.service.estudiante.filtro.EstudianteSpecifications;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProgramaRepository programaRepository;
    private final FacultadRepository facultadRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaLogger auditoriaLogger;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @RequiereRol(roles = {Rol.ADMIN_DTI})
    @Transactional
    public EstudianteResponse crear(CrearEstudianteRequest request, CustomUserDetails actor) {
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new OperacionNoPermitidaException("El correo ya esta registrado en el sistema.");
        }
        if (estudianteRepository.existsByIdentificacion(request.getIdentificacion())) {
            throw new OperacionNoPermitidaException("La identificacion ya esta registrada en el sistema.");
        }

        Programa programa = programaRepository.findById(request.getProgramaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Programa no encontrado: " + request.getProgramaId()));
        Facultad facultad = facultadRepository.findById(request.getFacultadId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Facultad no encontrada: " + request.getFacultadId()));

        if (!programa.getFacultad().getId().equals(facultad.getId())) {
            throw new OperacionNoPermitidaException("El programa no pertenece a la facultad indicada.");
        }

        String passwordTemporal = generarPasswordTemporal();
        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .correo(request.getCorreo())
                .passwordHash(passwordEncoder.encode(passwordTemporal))
                .telefono(request.getTelefono())
                .rol(Rol.ESTUDIANTE)
                .facultad(facultad)
                .programa(programa)
                .activo(true)
                .primerIngreso(true)
                .estadoCuenta(EstadoCuenta.PENDIENTE)
                .estadoEstudiante(EstadoEstudiante.NO_APTO)
                .build();
        usuario = usuarioRepository.save(usuario);

        Estudiante estudiante = EstudianteBuilder.nuevo()
                .conUsuario(usuario)
                .conIdentificacion(request.getIdentificacion())
                .conContactoEmergencia(request.getContactoEmergencia())
                .conSemestre(request.getSemestre())
                .conCreditosAprobados(request.getCreditosAprobados())
                .conPromedioAcademico(request.getPromedioAcademico())
                .conDocumentosBaseCompletos(request.isDocumentosBaseCompletos())
                .conHojaVidaValida(request.isHojaVidaValida())
                .construir();
        estudiante = estudianteRepository.save(estudiante);

        eventPublisher.publishEvent(new UsuarioCreadoEvent(this, usuario, passwordTemporal));

        auditoriaLogger.registrar(iniciarAuditoria(actor)
                .modulo(ModuloAuditoria.ESTUDIANTES)
                .tipoAccion(TipoAccion.CREAR)
                .registroAfectadoId(estudiante.getId())
                .registroAfectadoTipo("Estudiante")
                .valoresNuevos(toJson(estudiante))
                .exitoso(true));

        return EstudianteResponse.desde(estudiante);
    }

    @RequiereRol(roles = {Rol.ADMIN_DTI, Rol.COORDINACION_ACADEMICA, Rol.COORDINADOR_PRACTICAS})
    @Transactional(readOnly = true)
    public Page<EstudianteResponse> listar(EstadoEstudiante estado, Pageable pageable, CustomUserDetails actor) {
        Rol rol = actor.getRol();

        if (Rol.COORDINADOR_PRACTICAS.equals(rol)) {
            if (estado == null) {
                throw new OperacionNoPermitidaException("Debe indicar el estado para listar estudiantes.");
            }
            if (estado != EstadoEstudiante.APTO) {
                throw new OperacionNoPermitidaException("El coordinador solo puede ver estudiantes APTO.");
            }
            var spec = EstudianteSpecifications.porPrograma(actor.getProgramaId())
                    .and(EstudianteSpecifications.enviadoProceso())
                    .and(EstudianteSpecifications.porEstado(EstadoEstudiante.APTO));
            return estudianteRepository.findAll(spec, pageable).map(EstudianteResponse::desde);
        }

        if (Rol.COORDINACION_ACADEMICA.equals(rol)) {
            if (estado == null) {
                throw new OperacionNoPermitidaException("Debe indicar el estado para listar estudiantes.");
            }
            return estudianteRepository.findPorEstadoYFacultad(
                    Rol.ESTUDIANTE, estado, actor.getFacultadId(), pageable
            ).map(EstudianteResponse::desde);
        }

        return estudianteRepository.findAll(pageable).map(EstudianteResponse::desde);
    }

    @RequiereRol(roles = {Rol.ADMIN_DTI, Rol.COORDINACION_ACADEMICA})
    @Transactional(readOnly = true)
    public EstudianteResponse obtener(Long id, CustomUserDetails actor) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado: " + id));

        if (Rol.COORDINACION_ACADEMICA.equals(actor.getRol())) {
            Long facultadId = actor.getFacultadId();
            Long facultadEstudiante = estudiante.getUsuario().getPrograma().getFacultad().getId();
            if (!facultadEstudiante.equals(facultadId)) {
                throw new OperacionNoPermitidaException("No tiene acceso a este estudiante.");
            }
        }

        return EstudianteResponse.desde(estudiante);
    }

    @RequiereRol(roles = {Rol.ADMIN_DTI})
    @Transactional
    public void desactivar(Long id, CustomUserDetails actor) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado: " + id));

        estudiante.getUsuario().setActivo(false);

        auditoriaLogger.registrar(iniciarAuditoria(actor)
                .modulo(ModuloAuditoria.ESTUDIANTES)
                .tipoAccion(TipoAccion.DESACTIVAR)
                .registroAfectadoId(estudiante.getId())
                .registroAfectadoTipo("Estudiante")
                .exitoso(true));
    }

    @RequiereRol(roles = {Rol.COORDINACION_ACADEMICA})
    @Transactional
    public void enviarAProceso(EnviarAProcesoRequest request, CustomUserDetails actor) {
        var estudiantes = request.getEstudianteIds().stream()
                .map(id -> estudianteRepository.findById(id)
                        .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado: " + id)))
                .toList();

        for (Estudiante estudiante : estudiantes) {
            if (!actor.getFacultadId().equals(estudiante.getUsuario().getPrograma().getFacultad().getId())) {
                throw new OperacionNoPermitidaException("No tiene acceso a este estudiante.");
            }
            if (!EstadoEstudiante.APTO.equals(estudiante.getUsuario().getEstadoEstudiante())) {
                throw new OperacionNoPermitidaException("Solo se pueden enviar estudiantes APTO al proceso.");
            }
            if (estudiante.isEnviadoProceso()) {
                continue;
            }
            estudiante.setEnviadoProceso(true);
        }

        estudianteRepository.saveAll(estudiantes);

        auditoriaLogger.registrar(iniciarAuditoria(actor)
                .modulo(ModuloAuditoria.ESTUDIANTES)
                .tipoAccion(TipoAccion.EDITAR)
                .registroAfectadoTipo("Estudiante")
                .valoresNuevos(toJson(estudiantes))
                .exitoso(true));

        eventPublisher.publishEvent(new EstudiantesEnviadosEvent(this, estudiantes));
    }

    private String generarPasswordTemporal() {
        byte[] bytes = new byte[9];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private BitacoraAuditoria.BitacoraAuditoriaBuilder iniciarAuditoria(CustomUserDetails actor) {
        return BitacoraAuditoria.builder()
                .usuario(actor.getUsuario())
                .nombreUsuario(actor.getNombre())
                .rolUsuario(actor.getRol())
                .etiquetaCargoUsuario(actor.getEtiquetaCargo());
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }
}

