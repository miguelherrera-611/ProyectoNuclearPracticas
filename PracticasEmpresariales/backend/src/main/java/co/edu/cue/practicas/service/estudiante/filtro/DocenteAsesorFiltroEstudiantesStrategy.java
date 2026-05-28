package co.edu.cue.practicas.service.estudiante.filtro;

import co.edu.cue.practicas.dto.request.FiltroEstudiantesRequest;
import co.edu.cue.practicas.dto.response.EstudianteResponse;
import co.edu.cue.practicas.repository.estudiante.EstudianteRepository;
import co.edu.cue.practicas.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocenteAsesorFiltroEstudiantesStrategy implements FiltroEstudiantesStrategy {

    private final EstudianteRepository estudianteRepository;

    @Override
    public Page<EstudianteResponse> filtrar(FiltroEstudiantesRequest filtro, Pageable pageable, CustomUserDetails actor) {
        var spec = EstudianteSpecifications.construir(filtro)
                .and(EstudianteSpecifications.porDocenteAsesor(actor.getId()));
        return estudianteRepository.findAll(spec, pageable).map(EstudianteResponse::desde);
    }
}

