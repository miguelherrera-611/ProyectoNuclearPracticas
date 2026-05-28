package co.edu.cue.practicas.service.expediente;

import co.edu.cue.practicas.dto.response.ExpedienteResponse;
import co.edu.cue.practicas.dto.response.HojaVidaResponse;
import co.edu.cue.practicas.dto.response.PracticaResumenResponse;
import co.edu.cue.practicas.exception.OperacionNoPermitidaException;
import co.edu.cue.practicas.exception.RecursoNoEncontradoException;
import co.edu.cue.practicas.model.entity.Estudiante;
import co.edu.cue.practicas.model.enums.Rol;
import co.edu.cue.practicas.repository.estudiante.EstudianteRepository;
import co.edu.cue.practicas.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ExpedienteConsultaBase implements ExpedienteConsulta {

    private final EstudianteRepository estudianteRepository;

    @Override
    @Transactional(readOnly = true)
    public ExpedienteResponse obtener(Long estudianteId, CustomUserDetails actor) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado: " + estudianteId));

        validarAcceso(estudiante, actor);

        var usuario = estudiante.getUsuario();
        var builder = ExpedienteHistoricoBuilder.nuevo()
                .conDatosBasicos(
                        estudiante.getId(),
                        usuario.getNombre(),
                        estudiante.getIdentificacion(),
                        usuario.getPrograma().getNombre(),
                        usuario.getPrograma().getFacultad().getNombre(),
                        estudiante.getSemestre(),
                        usuario.getEstadoEstudiante(),
                        estudiante.isHojaVidaValida()
                );

        estudiante.getHojasVida().stream()
                .map(HojaVidaResponse::desde)
                .forEach(builder::agregarHojaVida);

        estudiante.getExpediente().getPracticas().stream()
                .map(PracticaResumenResponse::desde)
                .forEach(builder::agregarPractica);

        return builder.construir();
    }

    private void validarAcceso(Estudiante estudiante, CustomUserDetails actor) {
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

