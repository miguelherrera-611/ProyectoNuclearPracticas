package co.edu.cue.practicas.service.validacion;

import co.edu.cue.practicas.audit.ModuloAuditoria;
import co.edu.cue.practicas.audit.singleton.AuditoriaLogger;
import co.edu.cue.practicas.dto.request.ValidarAptitudRequest;
import co.edu.cue.practicas.event.AptitudMarcadaEvent;
import co.edu.cue.practicas.exception.OperacionNoPermitidaException;
import co.edu.cue.practicas.exception.RecursoNoEncontradoException;
import co.edu.cue.practicas.model.entity.*;
import co.edu.cue.practicas.model.enums.EstadoEstudiante;
import co.edu.cue.practicas.model.enums.Rol;
import co.edu.cue.practicas.model.enums.TipoAccion;
import co.edu.cue.practicas.repository.catalogo.CatalogoPracticaRepository;
import co.edu.cue.practicas.repository.estudiante.EstudianteRepository;
import co.edu.cue.practicas.repository.estudiante.ExpedientePracticasRepository;
import co.edu.cue.practicas.security.CustomUserDetails;
import co.edu.cue.practicas.security.annotation.RequiereRol;
import co.edu.cue.practicas.service.validacion.chain.ContextoValidacion;
import co.edu.cue.practicas.service.validacion.strategy.ValidacionStrategyResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ValidacionAptitudService extends FlujoValidacionAptitud {

    private final EstudianteRepository estudianteRepository;
    private final CatalogoPracticaRepository catalogoRepository;
    private final ExpedientePracticasRepository expedienteRepository;
    private final ValidacionStrategyResolver strategyResolver;
    private final AuditoriaLogger auditoriaLogger;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    @RequiereRol(roles = {Rol.COORDINACION_ACADEMICA})
    @Transactional
    public void validarAptitud(Long estudianteId, ValidarAptitudRequest request, CustomUserDetails actor) {
        if (request.getApto() == null) {
            throw new OperacionNoPermitidaException("Debe indicar si el estudiante es apto o no.");
        }
        validar(estudianteId, request, actor);
    }

    @Override
    protected Estudiante cargarEstudiante(Long estudianteId) {
        return estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado: " + estudianteId));
    }

    @Override
    protected void validarScope(Estudiante estudiante, CustomUserDetails actor) {
        Long facultadId = actor.getFacultadId();
        Long facultadEstudiante = estudiante.getUsuario().getPrograma().getFacultad().getId();
        if (facultadId == null || !facultadId.equals(facultadEstudiante)) {
            throw new OperacionNoPermitidaException("No tiene acceso a este estudiante.");
        }
    }

    @Override
    protected CatalogoPractica obtenerCatalogo(Estudiante estudiante, ValidarAptitudRequest request) {
        Long programaId = estudiante.getUsuario().getPrograma().getId();
        return catalogoRepository.findByPrograma_IdAndNumeroPracticaAndActivoTrue(programaId, request.getNumeroPractica())
                .orElseThrow(() -> new OperacionNoPermitidaException("El catalogo de practicas no esta configurado."));
    }

    @Override
    protected RequisitosPractica obtenerRequisitos(Estudiante estudiante, ValidarAptitudRequest request) {
        Programa programa = estudiante.getUsuario().getPrograma();
        return programa.getRequisitos().stream()
                .filter(r -> r.getNumeroPractica() == request.getNumeroPractica())
                .findFirst()
                .orElseThrow(() -> new OperacionNoPermitidaException("No existen requisitos configurados para esta practica."));
    }

    @Override
    protected void ejecutarValidaciones(Estudiante estudiante, RequisitosPractica requisitos, ValidarAptitudRequest request) {
        if (EstadoEstudiante.APTO.equals(estudiante.getUsuario().getEstadoEstudiante())) {
            throw new OperacionNoPermitidaException("El estudiante ya esta en estado APTO.");
        }

        ContextoValidacion contexto = new ContextoValidacion(request.getNumeroPractica(), estudiante.getExpediente());
        var estrategia = strategyResolver.resolver(estudiante.getUsuario().getPrograma());
        estrategia.validar(estudiante, requisitos, contexto);
    }

    @Override
    protected void marcarApto(Estudiante estudiante, CatalogoPractica catalogo, CustomUserDetails actor) {
        String antes = toJson(estudiante.getUsuario());

        estudiante.getUsuario().setEstadoEstudiante(EstadoEstudiante.APTO);
        estudiante.getUsuario().setMotivoNoApto(null);

        ExpedientePracticas expediente = estudiante.getExpediente();
        boolean yaExiste = expediente.getPracticas().stream()
                .anyMatch(p -> p.getNumeroPractica() == catalogo.getNumeroPractica());
        if (yaExiste) {
            throw new OperacionNoPermitidaException("La practica ya existe en el expediente.");
        }
        var instancia = catalogo.clonarPara(expediente);
        expediente.getPracticas().add(instancia);
        expedienteRepository.save(expediente);

        auditoriaLogger.registrar(iniciarAuditoria(actor)
                .modulo(ModuloAuditoria.VALIDACION_APTITUD)
                .tipoAccion(TipoAccion.EDITAR)
                .registroAfectadoId(estudiante.getId())
                .registroAfectadoTipo("Estudiante")
                .valoresAnteriores(antes)
                .valoresNuevos(toJson(estudiante.getUsuario()))
                .exitoso(true));

        eventPublisher.publishEvent(new AptitudMarcadaEvent(this, estudiante, true));
    }

    @Override
    protected void marcarNoApto(Estudiante estudiante, ValidarAptitudRequest request, CustomUserDetails actor) {
        if (request.getMotivoNoApto() == null || request.getMotivoNoApto().isBlank()) {
            throw new OperacionNoPermitidaException("El motivo de NO_APTO es obligatorio.");
        }
        if (estudiante.isEnviadoProceso()) {
            throw new OperacionNoPermitidaException("Un estudiante enviado al proceso no puede volver a NO_APTO.");
        }

        String antes = toJson(estudiante.getUsuario());

        estudiante.getUsuario().setEstadoEstudiante(EstadoEstudiante.NO_APTO);
        estudiante.getUsuario().setMotivoNoApto(request.getMotivoNoApto());

        auditoriaLogger.registrar(iniciarAuditoria(actor)
                .modulo(ModuloAuditoria.VALIDACION_APTITUD)
                .tipoAccion(TipoAccion.EDITAR)
                .registroAfectadoId(estudiante.getId())
                .registroAfectadoTipo("Estudiante")
                .valoresAnteriores(antes)
                .valoresNuevos(toJson(estudiante.getUsuario()))
                .exitoso(true));

        eventPublisher.publishEvent(new AptitudMarcadaEvent(this, estudiante, false));
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

