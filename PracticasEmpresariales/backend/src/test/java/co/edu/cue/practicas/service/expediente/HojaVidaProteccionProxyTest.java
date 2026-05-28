package co.edu.cue.practicas.service.expediente;

import co.edu.cue.practicas.dto.request.CrearHojaVidaRequest;
import co.edu.cue.practicas.exception.OperacionNoPermitidaException;
import co.edu.cue.practicas.model.entity.Estudiante;
import co.edu.cue.practicas.model.entity.ExpedientePracticas;
import co.edu.cue.practicas.model.entity.PracticaInstancia;
import co.edu.cue.practicas.model.entity.Usuario;
import co.edu.cue.practicas.model.enums.EstadoPractica;
import co.edu.cue.practicas.model.enums.Rol;
import co.edu.cue.practicas.repository.estudiante.EstudianteRepository;
import co.edu.cue.practicas.security.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HojaVidaProteccionProxyTest {

    @Mock
    private HojaVidaService hojaVidaService;
    @Mock
    private EstudianteRepository estudianteRepository;

    @InjectMocks
    private HojaVidaProteccionProxy proxy;

    @Test
    void rechaza_actualizar_si_practica_en_curso() {
        PracticaInstancia practica = PracticaInstancia.builder().estado(EstadoPractica.EN_CURSO).build();
        ExpedientePracticas expediente = ExpedientePracticas.builder().practicas(List.of(practica)).build();
        Usuario usuario = Usuario.builder().id(1L).rol(Rol.ESTUDIANTE).build();
        Estudiante estudiante = Estudiante.builder().id(5L).usuario(usuario).expediente(expediente).build();
        expediente.setEstudiante(estudiante);

        when(estudianteRepository.findById(5L)).thenReturn(Optional.of(estudiante));

        CrearHojaVidaRequest request = new CrearHojaVidaRequest();
        request.setUrlArchivo("hv.pdf");

        CustomUserDetails actor = new CustomUserDetails(usuario);

        assertThrows(OperacionNoPermitidaException.class, () -> proxy.registrarVersion(5L, request, actor));
        verifyNoInteractions(hojaVidaService);
    }
}

