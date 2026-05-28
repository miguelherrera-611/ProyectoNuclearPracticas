package co.edu.cue.practicas.service.estudiante;

import co.edu.cue.practicas.exception.OperacionNoPermitidaException;
import co.edu.cue.practicas.model.entity.Estudiante;
import co.edu.cue.practicas.model.entity.ExpedientePracticas;
import co.edu.cue.practicas.model.entity.Usuario;

/**
 * PATRON BUILDER — GPE-143
 * Construye el expediente del estudiante paso a paso.
 */
public class EstudianteBuilder {

    private final Estudiante estudiante = new Estudiante();
    private final ExpedientePracticas expediente = new ExpedientePracticas();

    public static EstudianteBuilder nuevo() {
        return new EstudianteBuilder();
    }

    public EstudianteBuilder conUsuario(Usuario usuario) {
        estudiante.setUsuario(usuario);
        return this;
    }

    public EstudianteBuilder conIdentificacion(String identificacion) {
        estudiante.setIdentificacion(identificacion);
        return this;
    }

    public EstudianteBuilder conContactoEmergencia(String contacto) {
        estudiante.setContactoEmergencia(contacto);
        return this;
    }

    public EstudianteBuilder conSemestre(int semestre) {
        estudiante.setSemestre(semestre);
        return this;
    }

    public EstudianteBuilder conCreditosAprobados(int creditos) {
        estudiante.setCreditosAprobados(creditos);
        return this;
    }

    public EstudianteBuilder conPromedioAcademico(double promedio) {
        estudiante.setPromedioAcademico(promedio);
        return this;
    }

    public EstudianteBuilder conDocumentosBaseCompletos(boolean completos) {
        estudiante.setDocumentosBaseCompletos(completos);
        return this;
    }

    public EstudianteBuilder conHojaVidaValida(boolean valida) {
        estudiante.setHojaVidaValida(valida);
        return this;
    }

    public Estudiante construir() {
        if (estudiante.getUsuario() == null) {
            throw new OperacionNoPermitidaException("El usuario del estudiante es obligatorio.");
        }
        if (estudiante.getIdentificacion() == null || estudiante.getIdentificacion().isBlank()) {
            throw new OperacionNoPermitidaException("La identificacion es obligatoria.");
        }
        expediente.setEstudiante(estudiante);
        estudiante.setExpediente(expediente);
        return estudiante;
    }
}

