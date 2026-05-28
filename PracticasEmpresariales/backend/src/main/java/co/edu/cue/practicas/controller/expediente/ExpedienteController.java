package co.edu.cue.practicas.controller.expediente;

import co.edu.cue.practicas.dto.request.CrearHojaVidaRequest;
import co.edu.cue.practicas.dto.response.ApiResponse;
import co.edu.cue.practicas.dto.response.ExpedienteResponse;
import co.edu.cue.practicas.dto.response.HojaVidaResponse;
import co.edu.cue.practicas.security.CustomUserDetails;
import co.edu.cue.practicas.service.expediente.ExpedienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/expedientes")
@RequiredArgsConstructor
public class ExpedienteController {

    private final ExpedienteService expedienteService;

    @GetMapping("/{estudianteId}")
    public ResponseEntity<ApiResponse<ExpedienteResponse>> obtener(
            @PathVariable Long estudianteId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(ApiResponse.ok(expedienteService.obtener(estudianteId, userDetails)));
    }

    @PostMapping("/{estudianteId}/hoja-vida")
    public ResponseEntity<ApiResponse<HojaVidaResponse>> registrarHojaVida(
            @PathVariable Long estudianteId,
            @Valid @RequestBody CrearHojaVidaRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        HojaVidaResponse response = expedienteService.registrarHojaVida(estudianteId, request, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Hoja de vida registrada", response));
    }
}

