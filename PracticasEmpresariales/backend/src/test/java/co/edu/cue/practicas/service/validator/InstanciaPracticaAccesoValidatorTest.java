package co.edu.cue.practicas.service.validator;

import co.edu.cue.practicas.exception.AccesoNoAutorizadoException;
import co.edu.cue.practicas.model.entity.ExpedienteEstudiante;
import co.edu.cue.practicas.model.entity.Facultad;
import co.edu.cue.practicas.model.entity.InstanciaPractica;
import co.edu.cue.practicas.model.entity.Programa;
import co.edu.cue.practicas.model.entity.Usuario;
import co.edu.cue.practicas.model.enums.Rol;
import co.edu.cue.practicas.security.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("InstanciaPracticaAccesoValidator — Pruebas unitarias")
class InstanciaPracticaAccesoValidatorTest {

    private static final Long FACULTAD_ID = 100L;

    private final InstanciaPracticaAccesoValidator validator = new InstanciaPracticaAccesoValidator();

    private CustomUserDetails actor(Rol rol) {
        CustomUserDetails actor = mock(CustomUserDetails.class);
        lenient().when(actor.getRol()).thenReturn(rol);
        lenient().when(actor.getFacultadId()).thenReturn(FACULTAD_ID);
        return actor;
    }

    private InstanciaPractica instanciaDeEstudiante(Long estudianteId) {
        Facultad facultad = Facultad.builder().id(FACULTAD_ID).nombre("Facultad de Ingenieria").build();
        Programa programa = Programa.builder().id(1L).nombre("Ingenieria").facultad(facultad).build();
        Usuario estudiante = Usuario.builder().id(estudianteId).nombre("Estudiante Test")
                .correo("est@cue.edu.co").programa(programa).build();
        ExpedienteEstudiante expediente = ExpedienteEstudiante.builder().id(1L).estudiante(estudiante).build();
        return InstanciaPractica.builder().id(1L).expediente(expediente).build();
    }

    @Test
    @DisplayName("ADMIN_DTI siempre tiene acceso")
    void validarAcceso_adminDti_noLanzaExcepcion() {
        InstanciaPractica instancia = instanciaDeEstudiante(1L);
        CustomUserDetails actor = actor(Rol.ADMIN_DTI);

        assertThatCode(() -> validator.validarAcceso(instancia, actor)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("DIRECCION siempre tiene acceso")
    void validarAcceso_direccion_noLanzaExcepcion() {
        InstanciaPractica instancia = instanciaDeEstudiante(1L);
        CustomUserDetails actor = actor(Rol.DIRECCION);

        assertThatCode(() -> validator.validarAcceso(instancia, actor)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("COORDINADOR_PRACTICAS con la misma facultad tiene acceso")
    void validarAcceso_coordinadorMismaFacultad_noLanzaExcepcion() {
        InstanciaPractica instancia = instanciaDeEstudiante(1L);
        CustomUserDetails actor = actor(Rol.COORDINADOR_PRACTICAS);

        assertThatCode(() -> validator.validarAcceso(instancia, actor)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("COORDINADOR_PRACTICAS de otra facultad no tiene acceso")
    void validarAcceso_coordinadorOtraFacultad_lanzaExcepcion() {
        InstanciaPractica instancia = instanciaDeEstudiante(1L);
        CustomUserDetails actor = actor(Rol.COORDINADOR_PRACTICAS);
        when(actor.getFacultadId()).thenReturn(999L);

        assertThatThrownBy(() -> validator.validarAcceso(instancia, actor))
                .isInstanceOf(AccesoNoAutorizadoException.class);
    }

    @Test
    @DisplayName("DOCENTE_ASESOR asignado a la instancia tiene acceso")
    void validarAcceso_docenteAsignado_noLanzaExcepcion() {
        InstanciaPractica instancia = instanciaDeEstudiante(1L);
        instancia.setDocenteAsesor(Usuario.builder().id(5L).build());
        CustomUserDetails actor = actor(Rol.DOCENTE_ASESOR);
        when(actor.getId()).thenReturn(5L);

        assertThatCode(() -> validator.validarAcceso(instancia, actor)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("DOCENTE_ASESOR no asignado no tiene acceso")
    void validarAcceso_docenteNoAsignado_lanzaExcepcion() {
        InstanciaPractica instancia = instanciaDeEstudiante(1L);
        instancia.setDocenteAsesor(Usuario.builder().id(5L).build());
        CustomUserDetails actor = actor(Rol.DOCENTE_ASESOR);
        when(actor.getId()).thenReturn(99L);

        assertThatThrownBy(() -> validator.validarAcceso(instancia, actor))
                .isInstanceOf(AccesoNoAutorizadoException.class);
    }

    @Test
    @DisplayName("TUTOR_EMPRESARIAL asignado a la instancia tiene acceso")
    void validarAcceso_tutorAsignado_noLanzaExcepcion() {
        InstanciaPractica instancia = instanciaDeEstudiante(1L);
        instancia.setTutorEmpresarial(Usuario.builder().id(7L).correo("tutor@empresa.com").build());
        CustomUserDetails actor = actor(Rol.TUTOR_EMPRESARIAL);
        when(actor.getUsername()).thenReturn("tutor@empresa.com");

        assertThatCode(() -> validator.validarAcceso(instancia, actor)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("TUTOR_EMPRESARIAL no asignado no tiene acceso")
    void validarAcceso_tutorNoAsignado_lanzaExcepcion() {
        InstanciaPractica instancia = instanciaDeEstudiante(1L);
        instancia.setTutorEmpresarial(Usuario.builder().id(7L).correo("tutor@empresa.com").build());
        CustomUserDetails actor = actor(Rol.TUTOR_EMPRESARIAL);
        when(actor.getUsername()).thenReturn("otro@empresa.com");

        assertThatThrownBy(() -> validator.validarAcceso(instancia, actor))
                .isInstanceOf(AccesoNoAutorizadoException.class);
    }

    @Test
    @DisplayName("ESTUDIANTE dueno de la instancia tiene acceso")
    void validarAcceso_estudianteDueno_noLanzaExcepcion() {
        InstanciaPractica instancia = instanciaDeEstudiante(1L);
        CustomUserDetails actor = actor(Rol.ESTUDIANTE);
        when(actor.getId()).thenReturn(1L);

        assertThatCode(() -> validator.validarAcceso(instancia, actor)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("ESTUDIANTE de otra instancia no tiene acceso")
    void validarAcceso_estudianteNoDueno_lanzaExcepcion() {
        InstanciaPractica instancia = instanciaDeEstudiante(1L);
        CustomUserDetails actor = actor(Rol.ESTUDIANTE);
        when(actor.getId()).thenReturn(2L);

        assertThatThrownBy(() -> validator.validarAcceso(instancia, actor))
                .isInstanceOf(AccesoNoAutorizadoException.class);
    }
}
