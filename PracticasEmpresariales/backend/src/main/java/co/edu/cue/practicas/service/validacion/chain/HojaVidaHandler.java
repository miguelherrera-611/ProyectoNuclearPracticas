package co.edu.cue.practicas.service.validacion.chain;

import co.edu.cue.practicas.exception.OperacionNoPermitidaException;
import co.edu.cue.practicas.model.entity.Estudiante;
import co.edu.cue.practicas.model.entity.RequisitosPractica;

public class HojaVidaHandler extends ReglaBase {

    @Override
    protected void ejecutar(Estudiante estudiante, RequisitosPractica requisitos, ContextoValidacion contexto) {
        if (!estudiante.isHojaVidaValida()) {
            throw new OperacionNoPermitidaException("La hoja de vida no esta valida.");
        }
    }
}

