package co.edu.cue.practicas.service.estudiante.filtro;

import co.edu.cue.practicas.dto.request.FiltroEstudiantesRequest;
import co.edu.cue.practicas.dto.response.EstudianteResponse;
import co.edu.cue.practicas.exception.OperacionNoPermitidaException;
import co.edu.cue.practicas.model.enums.EstadoEstudiante;
import co.edu.cue.practicas.repository.estudiante.EstudianteRepository;
import co.edu.cue.practicas.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoordinadorFiltroEstudiantesStrategy implements FiltroEstudiantesStrategy {

    private final EstudianteRepository estudianteRepository;

    @Override
    public Page<EstudianteResponse> filtrar(FiltroEstudiantesRequest filtro, Pageable pageable, CustomUserDetails actor) {
        if (filtro.getEstadoEstudiante() != null && filtro.getEstadoEstudiante() != EstadoEstudiante.APTO) {
            throw new OperacionNoPermitidaException("El coordinador solo puede ver estudiantes APTO.");
        }
        filtro.setEstadoEstudiante(EstadoEstudiante.APTO);

        var spec = EstudianteSpecifications.construir(filtro)
                .and(EstudianteSpecifications.porPrograma(actor.getProgramaId()))
                .and(EstudianteSpecifications.enviadoProceso());
        return estudianteRepository.findAll(spec, pageable).map(EstudianteResponse::desde);
    }
}

