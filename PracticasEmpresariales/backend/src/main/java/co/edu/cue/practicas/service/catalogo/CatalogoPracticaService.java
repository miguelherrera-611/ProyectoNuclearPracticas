package co.edu.cue.practicas.service.catalogo;

import co.edu.cue.practicas.audit.ModuloAuditoria;
import co.edu.cue.practicas.audit.singleton.AuditoriaLogger;
import co.edu.cue.practicas.dto.request.CrearCatalogoPracticaRequest;
import co.edu.cue.practicas.dto.request.EditarCatalogoPracticaRequest;
import co.edu.cue.practicas.dto.response.CatalogoPracticaResponse;
import co.edu.cue.practicas.exception.OperacionNoPermitidaException;
import co.edu.cue.practicas.exception.RecursoNoEncontradoException;
import co.edu.cue.practicas.model.entity.BitacoraAuditoria;
import co.edu.cue.practicas.model.entity.CatalogoPractica;
import co.edu.cue.practicas.model.entity.Programa;
import co.edu.cue.practicas.model.enums.EstadoPractica;
import co.edu.cue.practicas.model.enums.Rol;
import co.edu.cue.practicas.model.enums.TipoAccion;
import co.edu.cue.practicas.repository.catalogo.CatalogoPracticaRepository;
import co.edu.cue.practicas.repository.practica.PracticaInstanciaRepository;
import co.edu.cue.practicas.repository.programa.ProgramaRepository;
import co.edu.cue.practicas.security.CustomUserDetails;
import co.edu.cue.practicas.security.annotation.RequiereRol;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogoPracticaService {

    private final CatalogoPracticaRepository catalogoRepository;
    private final ProgramaRepository programaRepository;
    private final PracticaInstanciaRepository practicaRepository;
    private final AuditoriaLogger auditoriaLogger;
    private final ObjectMapper objectMapper;

    @RequiereRol(roles = {Rol.COORDINACION_ACADEMICA})
    @Transactional
    public CatalogoPracticaResponse crear(CrearCatalogoPracticaRequest request, CustomUserDetails actor) {
        Programa programa = obtenerPrograma(request.getProgramaId());
        validarScopeFacultad(programa, actor);

        if (catalogoRepository.existsByPrograma_IdAndNumeroPractica(programa.getId(), request.getNumeroPractica())) {
            throw new OperacionNoPermitidaException("El numero de practica ya existe para este programa.");
        }

        CatalogoPractica catalogo = CatalogoPracticaBuilder.nuevo()
                .enPrograma(programa)
                .conNumeroPractica(request.getNumeroPractica())
                .conNombre(request.getNombre())
                .conMateriaNucleo(request.getMateriaNucleoNombre(), request.getMateriaNucleoCodigo())
                .conCortesSeguimiento(request.getNumeroCortesSeguimiento())
                .conDuracionSemanas(request.getDuracionSemanas())
                .conDocumentosRequeridos(request.getDocumentosRequeridos())
                .construir();

        catalogo = catalogoRepository.save(catalogo);

        auditoriaLogger.registrar(iniciarAuditoria(actor)
                .modulo(ModuloAuditoria.CATALOGO_PRACTICAS)
                .tipoAccion(TipoAccion.CREAR)
                .registroAfectadoId(catalogo.getId())
                .registroAfectadoTipo("CatalogoPractica")
                .valoresNuevos(toJson(catalogo))
                .exitoso(true));

        return CatalogoPracticaResponse.desde(catalogo);
    }

    @RequiereRol(roles = {Rol.COORDINACION_ACADEMICA})
    @Transactional
    public CatalogoPracticaResponse editar(Long id, EditarCatalogoPracticaRequest request, CustomUserDetails actor) {
        CatalogoPractica catalogo = buscarPorId(id);
        validarScopeFacultad(catalogo.getPrograma(), actor);

        if (catalogo.getNumeroPractica() != request.getNumeroPractica()
                && catalogoRepository.existsByPrograma_IdAndNumeroPractica(catalogo.getPrograma().getId(), request.getNumeroPractica())) {
            throw new OperacionNoPermitidaException("El numero de practica ya existe para este programa.");
        }

        String antes = toJson(catalogo);

        catalogo.setNumeroPractica(request.getNumeroPractica());
        catalogo.setNombre(request.getNombre());
        catalogo.setMateriaNucleoNombre(request.getMateriaNucleoNombre());
        catalogo.setMateriaNucleoCodigo(request.getMateriaNucleoCodigo());
        catalogo.setNumeroCortesSeguimiento(request.getNumeroCortesSeguimiento());
        catalogo.setDuracionSemanas(request.getDuracionSemanas());
        catalogo.setDocumentosRequeridos(request.getDocumentosRequeridos());
        catalogoRepository.save(catalogo);

        auditoriaLogger.registrar(iniciarAuditoria(actor)
                .modulo(ModuloAuditoria.CATALOGO_PRACTICAS)
                .tipoAccion(TipoAccion.EDITAR)
                .registroAfectadoId(catalogo.getId())
                .registroAfectadoTipo("CatalogoPractica")
                .valoresAnteriores(antes)
                .valoresNuevos(toJson(catalogo))
                .exitoso(true));

        return CatalogoPracticaResponse.desde(catalogo);
    }

    @RequiereRol(roles = {Rol.COORDINACION_ACADEMICA})
    @Transactional
    public void desactivar(Long id, CustomUserDetails actor) {
        CatalogoPractica catalogo = buscarPorId(id);
        validarScopeFacultad(catalogo.getPrograma(), actor);

        long activos = practicaRepository.countByCatalogoPractica_IdAndEstadoIn(
                catalogo.getId(),
                List.of(EstadoPractica.ASIGNADA_PENDIENTE_INICIO, EstadoPractica.EN_CURSO)
        );
        if (activos > 0) {
            throw new OperacionNoPermitidaException("No se puede desactivar una practica con estudiantes activos.");
        }

        catalogo.setActivo(false);
        catalogoRepository.save(catalogo);

        auditoriaLogger.registrar(iniciarAuditoria(actor)
                .modulo(ModuloAuditoria.CATALOGO_PRACTICAS)
                .tipoAccion(TipoAccion.DESACTIVAR)
                .registroAfectadoId(catalogo.getId())
                .registroAfectadoTipo("CatalogoPractica")
                .exitoso(true));
    }

    @RequiereRol(roles = {Rol.COORDINACION_ACADEMICA})
    @Transactional(readOnly = true)
    public Page<CatalogoPracticaResponse> listarPorPrograma(Long programaId, Pageable pageable, CustomUserDetails actor) {
        Programa programa = obtenerPrograma(programaId);
        validarScopeFacultad(programa, actor);

        return catalogoRepository.findByPrograma_IdAndActivoTrue(programaId, pageable)
                .map(CatalogoPracticaResponse::desde);
    }

    @RequiereRol(roles = {Rol.COORDINACION_ACADEMICA})
    @Transactional(readOnly = true)
    public CatalogoPracticaResponse obtener(Long id, CustomUserDetails actor) {
        CatalogoPractica catalogo = buscarPorId(id);
        validarScopeFacultad(catalogo.getPrograma(), actor);
        return CatalogoPracticaResponse.desde(catalogo);
    }

    private CatalogoPractica buscarPorId(Long id) {
        return catalogoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Catalogo no encontrado: " + id));
    }

    private Programa obtenerPrograma(Long programaId) {
        return programaRepository.findById(programaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Programa no encontrado: " + programaId));
    }

    private void validarScopeFacultad(Programa programa, CustomUserDetails actor) {
        if (actor.getFacultadId() == null || !actor.getFacultadId().equals(programa.getFacultad().getId())) {
            throw new OperacionNoPermitidaException("No tiene acceso al catalogo de esta facultad.");
        }
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

