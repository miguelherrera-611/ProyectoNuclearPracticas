package co.edu.cue.practicas.service.estudiante;

import co.edu.cue.practicas.dto.request.CrearEstudianteRequest;
import co.edu.cue.practicas.dto.request.EnviarAProcesoRequest;
import co.edu.cue.practicas.event.EstudiantesEnviadosEvent;
import co.edu.cue.practicas.exception.OperacionNoPermitidaException;
import co.edu.cue.practicas.model.entity.Estudiante;
import co.edu.cue.practicas.model.entity.Facultad;
import co.edu.cue.practicas.model.entity.Programa;
import co.edu.cue.practicas.model.entity.Usuario;
import co.edu.cue.practicas.model.enums.EstadoEstudiante;
import co.edu.cue.practicas.model.enums.Rol;
import co.edu.cue.practicas.repository.estudiante.EstudianteRepository;
import co.edu.cue.practicas.repository.facultad.FacultadRepository;
import co.edu.cue.practicas.repository.programa.ProgramaRepository;
import co.edu.cue.practicas.repository.usuario.UsuarioRepository;
import co.edu.cue.practicas.security.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstudianteServiceTest {

    @Mock
    private EstudianteRepository estudianteRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ProgramaRepository programaRepository;
    @Mock
    private FacultadRepository facultadRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private co.edu.cue.practicas.audit.singleton.AuditoriaLogger auditoriaLogger;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EstudianteService service;

    @Test
    void crear_rechaza_correo_duplicado() {
        when(usuarioRepository.existsByCorreo("a@cue.edu.co")).thenReturn(true);

        CrearEstudianteRequest request = new CrearEstudianteRequest();
        request.setCorreo("a@cue.edu.co");

        CustomUserDetails actor = new CustomUserDetails(Usuario.builder().rol(Rol.ADMIN_DTI).build());

        assertThrows(OperacionNoPermitidaException.class, () -> service.crear(request, actor));
    }

    @Test
    void enviarAProceso_rechaza_no_apto() {
        Facultad facultad = Facultad.builder().id(1L).build();
        Programa programa = Programa.builder().id(2L).facultad(facultad).build();
        Usuario usuario = Usuario.builder().rol(Rol.ESTUDIANTE).programa(programa).estadoEstudiante(EstadoEstudiante.NO_APTO).build();
        Estudiante estudiante = Estudiante.builder().id(5L).usuario(usuario).build();

        when(estudianteRepository.findById(5L)).thenReturn(Optional.of(estudiante));

        EnviarAProcesoRequest request = new EnviarAProcesoRequest();
        request.setEstudianteIds(List.of(5L));

        CustomUserDetails actor = new CustomUserDetails(Usuario.builder().rol(Rol.COORDINACION_ACADEMICA).facultad(facultad).build());

        assertThrows(OperacionNoPermitidaException.class, () -> service.enviarAProceso(request, actor));
    }

    @Test
    void enviarAProceso_marca_enviado_y_publica_evento() {
        Facultad facultad = Facultad.builder().id(1L).build();
        Programa programa = Programa.builder().id(2L).facultad(facultad).build();
        Usuario usuario = Usuario.builder().rol(Rol.ESTUDIANTE).programa(programa).estadoEstudiante(EstadoEstudiante.APTO).build();
        Estudiante estudiante = Estudiante.builder().id(5L).usuario(usuario).build();

        when(estudianteRepository.findById(5L)).thenReturn(Optional.of(estudiante));
        when(estudianteRepository.saveAll(any())).thenReturn(List.of(estudiante));

        EnviarAProcesoRequest request = new EnviarAProcesoRequest();
        request.setEstudianteIds(List.of(5L));

        CustomUserDetails actor = new CustomUserDetails(Usuario.builder().rol(Rol.COORDINACION_ACADEMICA).facultad(facultad).build());

        service.enviarAProceso(request, actor);

        verify(estudianteRepository).saveAll(any());
        ArgumentCaptor<EstudiantesEnviadosEvent> captor = ArgumentCaptor.forClass(EstudiantesEnviadosEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
    }
}

