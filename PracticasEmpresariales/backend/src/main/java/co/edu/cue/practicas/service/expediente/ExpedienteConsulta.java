package co.edu.cue.practicas.service.expediente;

import co.edu.cue.practicas.dto.response.ExpedienteResponse;
import co.edu.cue.practicas.security.CustomUserDetails;

public interface ExpedienteConsulta {

    ExpedienteResponse obtener(Long estudianteId, CustomUserDetails actor);
}

