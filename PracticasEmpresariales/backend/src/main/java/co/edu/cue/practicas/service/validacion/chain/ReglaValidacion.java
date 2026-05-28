package co.edu.cue.practicas.service.validacion.chain;

import co.edu.cue.practicas.model.entity.Estudiante;
import co.edu.cue.practicas.model.entity.RequisitosPractica;

public interface ReglaValidacion {

    void validar(Estudiante estudiante, RequisitosPractica requisitos, ContextoValidacion contexto);

    ReglaValidacion encadenar(ReglaValidacion siguiente);
}

