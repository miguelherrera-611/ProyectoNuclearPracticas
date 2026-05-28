package co.edu.cue.practicas.service.validacion.strategy;

import co.edu.cue.practicas.model.entity.Programa;
import org.springframework.stereotype.Component;

/**
 * Selecciona la estrategia de validacion segun el programa.
 */
@Component
public class ValidacionStrategyResolver {

    private final ValidacionAptitudStrategy estrategiaDefault = new ValidacionPorRequisitosStrategy();

    public ValidacionAptitudStrategy resolver(Programa programa) {
        return estrategiaDefault;
    }
}

