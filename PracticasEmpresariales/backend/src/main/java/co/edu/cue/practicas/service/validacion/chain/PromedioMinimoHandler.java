package co.edu.cue.practicas.service.validacion.chain;

import co.edu.cue.practicas.exception.OperacionNoPermitidaException;
import co.edu.cue.practicas.model.entity.Estudiante;
import co.edu.cue.practicas.model.entity.RequisitosPractica;

public class PromedioMinimoHandler extends ReglaBase {

    @Override
    protected void ejecutar(Estudiante estudiante, RequisitosPractica requisitos, ContextoValidacion contexto) {
        if (estudiante.getPromedioAcademico() < requisitos.getPromedioMinimo()) {
            throw new OperacionNoPermitidaException("El estudiante no cumple con el promedio minimo.");
        }
    }
}

