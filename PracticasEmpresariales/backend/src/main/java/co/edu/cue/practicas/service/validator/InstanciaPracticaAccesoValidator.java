package co.edu.cue.practicas.service.validator;

import co.edu.cue.practicas.exception.AccesoNoAutorizadoException;
import co.edu.cue.practicas.model.entity.InstanciaPractica;
import co.edu.cue.practicas.model.enums.Rol;
import co.edu.cue.practicas.security.CustomUserDetails;
import org.springframework.stereotype.Component;

/**
 * SOLID — SRP: regla de acceso a una instancia de practica segun el rol del actor.
 * Separado de VinculacionService/CierreFormalFacade para que ambos reutilicen la misma
 * regla sin duplicar logica de autorizacion.
 */
@Component
public class InstanciaPracticaAccesoValidator {

    public void validarAcceso(InstanciaPractica instancia, CustomUserDetails actor) {
        Rol rol = actor.getRol();
        if (rol == Rol.ADMIN_DTI || rol == Rol.DIRECCION) return;
        if (rol == Rol.COORDINADOR_PRACTICAS) {
            validarInstanciaEnFacultadDelCoordinador(instancia, actor);
            return;
        }
        if (rol == Rol.DOCENTE_ASESOR) {
            if (instancia.getDocenteAsesor() == null || !instancia.getDocenteAsesor().getId().equals(actor.getId()))
                throw new AccesoNoAutorizadoException("No tiene acceso a esta instancia de practica.");
            return;
        }
        if (rol == Rol.TUTOR_EMPRESARIAL) {
            if (instancia.getTutorEmpresarial() == null
                    || !instancia.getTutorEmpresarial().getCorreo().equalsIgnoreCase(actor.getUsername()))
                throw new AccesoNoAutorizadoException("No tiene acceso a esta instancia de practica.");
            return;
        }
        if (rol == Rol.ESTUDIANTE) {
            if (instancia.getExpediente() == null || !instancia.getExpediente().getEstudiante().getId().equals(actor.getId()))
                throw new AccesoNoAutorizadoException("No tiene acceso a esta instancia de practica.");
            return;
        }
        throw new AccesoNoAutorizadoException("No tiene permiso para consultar esta practica.");
    }

    private void validarInstanciaEnFacultadDelCoordinador(InstanciaPractica instancia, CustomUserDetails actor) {
        var estudiante = instancia.getExpediente() != null ? instancia.getExpediente().getEstudiante() : null;
        Long facultadEstudiante = estudiante != null
                && estudiante.getPrograma() != null
                && estudiante.getPrograma().getFacultad() != null
                ? estudiante.getPrograma().getFacultad().getId()
                : null;

        if (actor.getFacultadId() == null || !actor.getFacultadId().equals(facultadEstudiante)) {
            throw new AccesoNoAutorizadoException("No tiene acceso a practicas de otra facultad.");
        }
    }
}
