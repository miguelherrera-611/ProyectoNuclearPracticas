package co.edu.cue.practicas.service.estudiante.filtro;

import co.edu.cue.practicas.dto.request.FiltroEstudiantesRequest;
import co.edu.cue.practicas.dto.response.EstudianteResponse;
import co.edu.cue.practicas.model.enums.Rol;
import co.edu.cue.practicas.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListadoEstudiantesService {

    private final ListadoEstudiantesStrategyResolver resolver;

    public Page<EstudianteResponse> listar(FiltroEstudiantesRequest filtro, Pageable pageable, CustomUserDetails actor) {
        FiltroEstudiantesStrategy strategy = resolver.resolver(actor.getRol());
        return strategy.filtrar(filtro, pageable, actor);
    }
}

