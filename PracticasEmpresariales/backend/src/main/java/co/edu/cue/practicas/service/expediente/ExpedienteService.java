package co.edu.cue.practicas.service.expediente;

import co.edu.cue.practicas.dto.request.CrearHojaVidaRequest;
import co.edu.cue.practicas.dto.response.ExpedienteResponse;
import co.edu.cue.practicas.dto.response.HojaVidaResponse;
import co.edu.cue.practicas.security.CustomUserDetails;
import co.edu.cue.practicas.security.annotation.RequiereRol;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpedienteService {

    private final ExpedienteConsultaCacheProxy consultaProxy;
    private final HojaVidaProteccionProxy hojaVidaProxy;

    @RequiereRol(roles = {
            co.edu.cue.practicas.model.enums.Rol.ADMIN_DTI,
            co.edu.cue.practicas.model.enums.Rol.COORDINACION_ACADEMICA,
            co.edu.cue.practicas.model.enums.Rol.COORDINADOR_PRACTICAS,
            co.edu.cue.practicas.model.enums.Rol.DOCENTE_ASESOR,
            co.edu.cue.practicas.model.enums.Rol.TUTOR_EMPRESARIAL,
            co.edu.cue.practicas.model.enums.Rol.ESTUDIANTE,
            co.edu.cue.practicas.model.enums.Rol.DIRECCION
    })
    public ExpedienteResponse obtener(Long estudianteId, CustomUserDetails actor) {
        return consultaProxy.obtener(estudianteId, actor);
    }

    public HojaVidaResponse registrarHojaVida(Long estudianteId, CrearHojaVidaRequest request, CustomUserDetails actor) {
        return hojaVidaProxy.registrarVersion(estudianteId, request, actor);
    }
}

