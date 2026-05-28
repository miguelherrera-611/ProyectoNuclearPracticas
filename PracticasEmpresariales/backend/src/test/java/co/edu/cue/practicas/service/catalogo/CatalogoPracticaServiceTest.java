package co.edu.cue.practicas.service.catalogo;

import co.edu.cue.practicas.dto.request.CrearCatalogoPracticaRequest;
import co.edu.cue.practicas.exception.OperacionNoPermitidaException;
import co.edu.cue.practicas.model.entity.CatalogoPractica;
import co.edu.cue.practicas.model.entity.Facultad;
import co.edu.cue.practicas.model.entity.Programa;
import co.edu.cue.practicas.repository.catalogo.CatalogoPracticaRepository;
import co.edu.cue.practicas.repository.practica.PracticaInstanciaRepository;
import co.edu.cue.practicas.repository.programa.ProgramaRepository;
import co.edu.cue.practicas.security.CustomUserDetails;
import co.edu.cue.practicas.model.entity.Usuario;
import co.edu.cue.practicas.model.enums.Rol;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogoPracticaServiceTest {

    @Mock
    private CatalogoPracticaRepository catalogoRepository;
    @Mock
    private ProgramaRepository programaRepository;
    @Mock
    private PracticaInstanciaRepository practicaRepository;
    @Mock
    private co.edu.cue.practicas.audit.singleton.AuditoriaLogger auditoriaLogger;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CatalogoPracticaService service;

    @Test
    void crear_rechaza_numero_duplicado() {
        Programa programa = Programa.builder().id(10L).facultad(Facultad.builder().id(1L).build()).build();
        when(programaRepository.findById(10L)).thenReturn(Optional.of(programa));
        when(catalogoRepository.existsByPrograma_IdAndNumeroPractica(10L, 1)).thenReturn(true);

        CrearCatalogoPracticaRequest request = new CrearCatalogoPracticaRequest();
        request.setProgramaId(10L);
        request.setNumeroPractica(1);
        request.setNombre("Practica I");
        request.setMateriaNucleoNombre("Nucleo");
        request.setMateriaNucleoCodigo("NUC-101");
        request.setNumeroCortesSeguimiento(3);
        request.setDuracionSemanas(16);
        request.setDocumentosRequeridos("Documento");

        CustomUserDetails actor = new CustomUserDetails(Usuario.builder().id(5L).rol(Rol.COORDINACION_ACADEMICA).facultad(programa.getFacultad()).build());

        assertThrows(OperacionNoPermitidaException.class, () -> service.crear(request, actor));
    }

    @Test
    void desactivar_rechaza_practicas_activas() {
        Programa programa = Programa.builder().id(10L).facultad(Facultad.builder().id(1L).build()).build();
        CatalogoPractica catalogo = CatalogoPractica.builder().id(9L).programa(programa).build();
        when(catalogoRepository.findById(9L)).thenReturn(Optional.of(catalogo));
        when(practicaRepository.countByCatalogoPractica_IdAndEstadoIn(any(), any())).thenReturn(1L);

        CustomUserDetails actor = new CustomUserDetails(Usuario.builder().id(5L).rol(Rol.COORDINACION_ACADEMICA).facultad(programa.getFacultad()).build());

        assertThrows(OperacionNoPermitidaException.class, () -> service.desactivar(9L, actor));
    }
}

