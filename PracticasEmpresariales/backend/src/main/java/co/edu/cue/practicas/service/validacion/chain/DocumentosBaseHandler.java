package co.edu.cue.practicas.service.validacion.chain;

import co.edu.cue.practicas.exception.OperacionNoPermitidaException;
import co.edu.cue.practicas.model.entity.Estudiante;
import co.edu.cue.practicas.model.entity.RequisitosPractica;

public class DocumentosBaseHandler extends ReglaBase {

    @Override
    protected void ejecutar(Estudiante estudiante, RequisitosPractica requisitos, ContextoValidacion contexto) {
        if (!estudiante.isDocumentosBaseCompletos()) {
            throw new OperacionNoPermitidaException("Los documentos base no estan completos.");
        }
    }
}

