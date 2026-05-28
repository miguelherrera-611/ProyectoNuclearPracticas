package co.edu.cue.practicas.service.estudiante.filtro;

import co.edu.cue.practicas.dto.request.FiltroEstudiantesRequest;
import co.edu.cue.practicas.dto.response.EstudianteResponse;
import co.edu.cue.practicas.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FiltroEstudiantesStrategy {

    Page<EstudianteResponse> filtrar(FiltroEstudiantesRequest filtro, Pageable pageable, CustomUserDetails actor);
}

