package co.edu.cue.practicas.service.expediente;

import co.edu.cue.practicas.dto.request.CrearHojaVidaRequest;
import co.edu.cue.practicas.dto.response.HojaVidaResponse;
import co.edu.cue.practicas.exception.OperacionNoPermitidaException;
import co.edu.cue.practicas.model.enums.EstadoPractica;
import co.edu.cue.practicas.repository.estudiante.EstudianteRepository;
import co.edu.cue.practicas.security.CustomUserDetails;
import co.edu.cue.practicas.security.annotation.RequiereRol;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * PATRON PROXY (PROTECCION) — bloquea actualizacion de HV si hay practica en curso.
 */
@Component
@RequiredArgsConstructor
public class HojaVidaProteccionProxy implements HojaVidaWriter {

    private final HojaVidaService hojaVidaService;
    private final EstudianteRepository estudianteRepository;

    @Override
    @RequiereRol(roles = {co.edu.cue.practicas.model.enums.Rol.ESTUDIANTE, co.edu.cue.practicas.model.enums.Rol.COORDINACION_ACADEMICA})
    public HojaVidaResponse registrarVersion(Long estudianteId, CrearHojaVidaRequest request, CustomUserDetails actor) {
        var estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new OperacionNoPermitidaException("Estudiante no encontrado."));

        if (actor.getRol() == co.edu.cue.practicas.model.enums.Rol.ESTUDIANTE
                && !actor.getId().equals(estudiante.getUsuario().getId())) {
            throw new OperacionNoPermitidaException("No tiene acceso a esta hoja de vida.");
        }
        if (actor.getRol() == co.edu.cue.practicas.model.enums.Rol.COORDINACION_ACADEMICA
                && !actor.getFacultadId().equals(estudiante.getUsuario().getPrograma().getFacultad().getId())) {
            throw new OperacionNoPermitidaException("No tiene acceso a este estudiante.");
        }

        boolean enCurso = estudiante.getExpediente().getPracticas().stream()
                .anyMatch(p -> EstadoPractica.EN_CURSO.equals(p.getEstado()));
        if (enCurso) {
            throw new OperacionNoPermitidaException("No se puede reemplazar la hoja de vida con una practica EN_CURSO.");
        }

        return hojaVidaService.registrarVersion(estudianteId, request, actor);
    }
}

