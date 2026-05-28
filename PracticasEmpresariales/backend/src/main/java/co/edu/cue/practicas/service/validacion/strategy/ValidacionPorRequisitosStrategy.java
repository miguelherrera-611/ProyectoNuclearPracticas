package co.edu.cue.practicas.service.validacion.strategy;

import co.edu.cue.practicas.model.entity.Estudiante;
import co.edu.cue.practicas.model.entity.RequisitosPractica;
import co.edu.cue.practicas.service.validacion.chain.*;

/**
 * PATRON STRATEGY — valida usando la cadena de reglas institucional.
 */
public class ValidacionPorRequisitosStrategy implements ValidacionAptitudStrategy {

    @Override
    public void validar(Estudiante estudiante, RequisitosPractica requisitos, ContextoValidacion contexto) {
        ReglaValidacion cadena = new CreditosMinimosHandler();
        cadena.encadenar(new PromedioMinimoHandler())
                .encadenar(new PracticaAnteriorHandler())
                .encadenar(new DocumentosBaseHandler())
                .encadenar(new HojaVidaHandler());

        cadena.validar(estudiante, requisitos, contexto);
    }
}

