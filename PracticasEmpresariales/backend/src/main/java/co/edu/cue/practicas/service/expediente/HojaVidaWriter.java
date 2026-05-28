package co.edu.cue.practicas.service.expediente;

import co.edu.cue.practicas.dto.request.CrearHojaVidaRequest;
import co.edu.cue.practicas.dto.response.HojaVidaResponse;
import co.edu.cue.practicas.security.CustomUserDetails;

public interface HojaVidaWriter {

    HojaVidaResponse registrarVersion(Long estudianteId, CrearHojaVidaRequest request, CustomUserDetails actor);
}

