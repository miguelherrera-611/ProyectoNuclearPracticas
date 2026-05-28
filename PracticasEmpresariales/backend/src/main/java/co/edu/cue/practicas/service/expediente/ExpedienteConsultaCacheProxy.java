package co.edu.cue.practicas.service.expediente;

import co.edu.cue.practicas.dto.response.ExpedienteResponse;
import co.edu.cue.practicas.exception.OperacionNoPermitidaException;
import co.edu.cue.practicas.exception.RecursoNoEncontradoException;
import co.edu.cue.practicas.model.enums.Rol;
import co.edu.cue.practicas.repository.estudiante.EstudianteRepository;
import co.edu.cue.practicas.security.CustomUserDetails;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PATRON PROXY (CACHE) — cachea expedientes consultados con frecuencia.
 */
@Component
public class ExpedienteConsultaCacheProxy implements ExpedienteConsulta {

    private final ExpedienteConsultaBase base;
    private final EstudianteRepository estudianteRepository;
    private final Map<Long, ExpedienteResponse> cache = new ConcurrentHashMap<>();

    public ExpedienteConsultaCacheProxy(ExpedienteConsultaBase base, EstudianteRepository estudianteRepository) {
        this.base = base;
        this.estudianteRepository = estudianteRepository;
    }

    @Override
    public ExpedienteResponse obtener(Long estudianteId, CustomUserDetails actor) {
        validarAcceso(estudianteId, actor);
        return cache.computeIfAbsent(estudianteId, id -> base.obtener(id, actor));
    }

    public void invalidar(Long estudianteId) {
        cache.remove(estudianteId);
    }

    private void validarAcceso(Long estudianteId, CustomUserDetails actor) {
        var estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado: " + estudianteId));
        Rol rol = actor.getRol();

        if (Rol.ESTUDIANTE.equals(rol) && !actor.getId().equals(estudiante.getUsuario().getId())) {
            throw new OperacionNoPermitidaException("No tiene acceso a este expediente.");
        }
        if (Rol.COORDINADOR_PRACTICAS.equals(rol)) {
            if (!estudiante.isEnviadoProceso() || !actor.getProgramaId().equals(estudiante.getUsuario().getPrograma().getId())) {
                throw new OperacionNoPermitidaException("No tiene acceso a este expediente.");
            }
        }
        if (Rol.COORDINACION_ACADEMICA.equals(rol)) {
            if (!actor.getFacultadId().equals(estudiante.getUsuario().getPrograma().getFacultad().getId())) {
                throw new OperacionNoPermitidaException("No tiene acceso a este expediente.");
            }
        }
        if (Rol.DOCENTE_ASESOR.equals(rol)) {
            boolean asignado = estudiante.getExpediente().getPracticas().stream()
                    .anyMatch(p -> p.getDocenteAsesor() != null && p.getDocenteAsesor().getId().equals(actor.getId()));
            if (!asignado) {
                throw new OperacionNoPermitidaException("No tiene acceso a este expediente.");
            }
        }
        if (Rol.TUTOR_EMPRESARIAL.equals(rol)) {
            boolean asignado = estudiante.getExpediente().getPracticas().stream()
                    .anyMatch(p -> p.getTutorEmpresarial() != null && p.getTutorEmpresarial().getId().equals(actor.getId()));
            if (!asignado) {
                throw new OperacionNoPermitidaException("No tiene acceso a este expediente.");
            }
        }
    }
}

