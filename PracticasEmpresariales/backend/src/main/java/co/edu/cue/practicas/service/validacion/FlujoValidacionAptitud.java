package co.edu.cue.practicas.service.validacion;

import co.edu.cue.practicas.dto.request.ValidarAptitudRequest;
import co.edu.cue.practicas.model.entity.Estudiante;
import co.edu.cue.practicas.model.entity.RequisitosPractica;
import co.edu.cue.practicas.model.entity.CatalogoPractica;
import co.edu.cue.practicas.security.CustomUserDetails;

/**
 * PATRON TEMPLATE METHOD — flujo general de validacion de aptitud.
 */
public abstract class FlujoValidacionAptitud {

    public final void validar(Long estudianteId, ValidarAptitudRequest request, CustomUserDetails actor) {
        Estudiante estudiante = cargarEstudiante(estudianteId);
        validarScope(estudiante, actor);

        if (Boolean.FALSE.equals(request.getApto())) {
            marcarNoApto(estudiante, request, actor);
            return;
        }

        CatalogoPractica catalogo = obtenerCatalogo(estudiante, request);
        RequisitosPractica requisitos = obtenerRequisitos(estudiante, request);
        ejecutarValidaciones(estudiante, requisitos, request);
        marcarApto(estudiante, catalogo, actor);
    }

    protected abstract Estudiante cargarEstudiante(Long estudianteId);

    protected abstract void validarScope(Estudiante estudiante, CustomUserDetails actor);

    protected abstract CatalogoPractica obtenerCatalogo(Estudiante estudiante, ValidarAptitudRequest request);

    protected abstract RequisitosPractica obtenerRequisitos(Estudiante estudiante, ValidarAptitudRequest request);

    protected abstract void ejecutarValidaciones(Estudiante estudiante, RequisitosPractica requisitos, ValidarAptitudRequest request);

    protected abstract void marcarApto(Estudiante estudiante, CatalogoPractica catalogo, CustomUserDetails actor);

    protected abstract void marcarNoApto(Estudiante estudiante, ValidarAptitudRequest request, CustomUserDetails actor);
}

