package co.edu.cue.practicas.service.validacion.strategy;

import co.edu.cue.practicas.model.entity.Estudiante;
import co.edu.cue.practicas.model.entity.RequisitosPractica;
import co.edu.cue.practicas.service.validacion.chain.ContextoValidacion;

public interface ValidacionAptitudStrategy {

    void validar(Estudiante estudiante, RequisitosPractica requisitos, ContextoValidacion contexto);
}

