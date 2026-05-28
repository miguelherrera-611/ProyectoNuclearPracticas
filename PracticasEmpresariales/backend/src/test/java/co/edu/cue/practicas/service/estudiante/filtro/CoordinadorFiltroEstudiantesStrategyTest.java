package co.edu.cue.practicas.service.estudiante.filtro;

import co.edu.cue.practicas.dto.request.FiltroEstudiantesRequest;
import co.edu.cue.practicas.exception.OperacionNoPermitidaException;
import co.edu.cue.practicas.model.enums.EstadoEstudiante;
import co.edu.cue.practicas.model.enums.Rol;
import co.edu.cue.practicas.repository.estudiante.EstudianteRepository;
import co.edu.cue.practicas.security.CustomUserDetails;
import co.edu.cue.practicas.model.entity.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CoordinadorFiltroEstudiantesStrategyTest {

    @Mock
    private EstudianteRepository estudianteRepository;

    @InjectMocks
    private CoordinadorFiltroEstudiantesStrategy strategy;

    @Test
    void rechaza_estado_no_apto() {
        FiltroEstudiantesRequest filtro = new FiltroEstudiantesRequest();
        filtro.setEstadoEstudiante(EstadoEstudiante.NO_APTO);

        CustomUserDetails actor = new CustomUserDetails(Usuario.builder().rol(Rol.COORDINADOR_PRACTICAS).programaId(10L).build());

        assertThrows(OperacionNoPermitidaException.class, () -> strategy.filtrar(filtro, org.springframework.data.domain.PageRequest.of(0, 10), actor));
    }
}

