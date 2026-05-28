package co.edu.cue.practicas.service.validacion.chain;

import co.edu.cue.practicas.exception.OperacionNoPermitidaException;
import co.edu.cue.practicas.model.entity.Estudiante;
import co.edu.cue.practicas.model.entity.RequisitosPractica;
import co.edu.cue.practicas.model.enums.EstadoPractica;

public class PracticaAnteriorHandler extends ReglaBase {

    @Override
    protected void ejecutar(Estudiante estudiante, RequisitosPractica requisitos, ContextoValidacion contexto) {
        if (!requisitos.isRequierePracticaAnteriorAprobada()) {
            return;
        }

        int numeroPractica = contexto.numeroPractica();
        if (numeroPractica <= 1) {
            return;
        }

        boolean aprobada = contexto.expediente().getPracticas().stream()
                .anyMatch(p -> p.getNumeroPractica() == numeroPractica - 1 && EstadoPractica.FINALIZADA.equals(p.getEstado()));

        if (!aprobada) {
            throw new OperacionNoPermitidaException("La practica anterior no esta finalizada.");
        }
    }
}

