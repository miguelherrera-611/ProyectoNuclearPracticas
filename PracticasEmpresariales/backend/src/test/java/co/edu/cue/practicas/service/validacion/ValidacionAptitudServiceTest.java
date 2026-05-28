package co.edu.cue.practicas.service.validacion;

import co.edu.cue.practicas.dto.request.ValidarAptitudRequest;
import co.edu.cue.practicas.event.AptitudMarcadaEvent;
import co.edu.cue.practicas.exception.OperacionNoPermitidaException;
import co.edu.cue.practicas.model.entity.*;
import co.edu.cue.practicas.model.enums.EstadoEstudiante;
import co.edu.cue.practicas.model.enums.Rol;
import co.edu.cue.practicas.repository.catalogo.CatalogoPracticaRepository;
import co.edu.cue.practicas.repository.estudiante.EstudianteRepository;
import co.edu.cue.practicas.repository.estudiante.ExpedientePracticasRepository;
import co.edu.cue.practicas.security.CustomUserDetails;
import co.edu.cue.practicas.service.validacion.strategy.ValidacionStrategyResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidacionAptitudServiceTest {

    @Mock
    private EstudianteRepository estudianteRepository;
    @Mock
    private CatalogoPracticaRepository catalogoRepository;
    @Mock
    private ExpedientePracticasRepository expedienteRepository;
    @Mock
    private co.edu.cue.practicas.audit.singleton.AuditoriaLogger auditoriaLogger;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    void validar_rechaza_si_no_hay_catalogo() {
        ValidacionAptitudService service = new ValidacionAptitudService(
                estudianteRepository,
                catalogoRepository,
                expedienteRepository,
                new ValidacionStrategyResolver(),
                auditoriaLogger,
                objectMapper,
                eventPublisher
        );

        Facultad facultad = Facultad.builder().id(1L).build();
        Programa programa = Programa.builder().id(2L).facultad(facultad).build();
        Usuario usuario = Usuario.builder().rol(Rol.ESTUDIANTE).programa(programa).estadoEstudiante(EstadoEstudiante.NO_APTO).build();
        Estudiante estudiante = Estudiante.builder().id(9L).usuario(usuario).build();

        when(estudianteRepository.findById(9L)).thenReturn(Optional.of(estudiante));
        when(catalogoRepository.findByPrograma_IdAndNumeroPracticaAndActivoTrue(2L, 1)).thenReturn(Optional.empty());

        ValidarAptitudRequest request = new ValidarAptitudRequest();
        request.setNumeroPractica(1);
        request.setApto(true);

        CustomUserDetails actor = new CustomUserDetails(Usuario.builder().rol(Rol.COORDINACION_ACADEMICA).facultad(facultad).build());

        assertThrows(OperacionNoPermitidaException.class, () -> service.validarAptitud(9L, request, actor));
    }

    @Test
    void validar_apto_crea_instancia_y_publica_evento() {
        ValidacionAptitudService service = new ValidacionAptitudService(
                estudianteRepository,
                catalogoRepository,
                expedienteRepository,
                new ValidacionStrategyResolver(),
                auditoriaLogger,
                objectMapper,
                eventPublisher
        );

        Facultad facultad = Facultad.builder().id(1L).build();
        RequisitosPractica requisitos = RequisitosPractica.builder()
                .numeroPractica(1)
                .creditosMinimos(10)
                .promedioMinimo(3.0)
                .requierePracticaAnteriorAprobada(false)
                .build();
        Programa programa = Programa.builder().id(2L).facultad(facultad).requisitos(List.of(requisitos)).build();
        Usuario usuario = Usuario.builder().rol(Rol.ESTUDIANTE).programa(programa).estadoEstudiante(EstadoEstudiante.NO_APTO).build();
        ExpedientePracticas expediente = ExpedientePracticas.builder().practicas(new java.util.ArrayList<>()).build();
        Estudiante estudiante = Estudiante.builder()
                .id(9L)
                .usuario(usuario)
                .creditosAprobados(20)
                .promedioAcademico(4.0)
                .documentosBaseCompletos(true)
                .hojaVidaValida(true)
                .expediente(expediente)
                .build();
        expediente.setEstudiante(estudiante);

        CatalogoPractica catalogo = CatalogoPractica.builder()
                .id(7L)
                .programa(programa)
                .numeroPractica(1)
                .nombre("Practica I")
                .materiaNucleoNombre("Nucleo")
                .materiaNucleoCodigo("NUC-101")
                .numeroCortesSeguimiento(3)
                .duracionSemanas(16)
                .documentosRequeridos("Doc")
                .build();

        when(estudianteRepository.findById(9L)).thenReturn(Optional.of(estudiante));
        when(catalogoRepository.findByPrograma_IdAndNumeroPracticaAndActivoTrue(2L, 1)).thenReturn(Optional.of(catalogo));
        when(expedienteRepository.save(any())).thenReturn(expediente);

        ValidarAptitudRequest request = new ValidarAptitudRequest();
        request.setNumeroPractica(1);
        request.setApto(true);

        CustomUserDetails actor = new CustomUserDetails(Usuario.builder().rol(Rol.COORDINACION_ACADEMICA).facultad(facultad).build());

        service.validarAptitud(9L, request, actor);

        assertEquals(EstadoEstudiante.APTO, estudiante.getUsuario().getEstadoEstudiante());
        assertEquals(1, estudiante.getExpediente().getPracticas().size());

        ArgumentCaptor<AptitudMarcadaEvent> captor = ArgumentCaptor.forClass(AptitudMarcadaEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
    }
}

