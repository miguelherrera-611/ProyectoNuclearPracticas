package co.edu.cue.practicas.service.validacion.chain;

import co.edu.cue.practicas.model.entity.Estudiante;
import co.edu.cue.practicas.model.entity.RequisitosPractica;

public abstract class ReglaBase implements ReglaValidacion {

    private ReglaValidacion siguiente;

    @Override
    public ReglaValidacion encadenar(ReglaValidacion siguiente) {
        this.siguiente = siguiente;
        return siguiente;
    }

    @Override
    public void validar(Estudiante estudiante, RequisitosPractica requisitos, ContextoValidacion contexto) {
        ejecutar(estudiante, requisitos, contexto);
        if (siguiente != null) {
            siguiente.validar(estudiante, requisitos, contexto);
        }
    }

    protected abstract void ejecutar(Estudiante estudiante, RequisitosPractica requisitos, ContextoValidacion contexto);
}

