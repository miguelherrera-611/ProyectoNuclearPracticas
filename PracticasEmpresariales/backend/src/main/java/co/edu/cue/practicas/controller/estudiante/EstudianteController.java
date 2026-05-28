package co.edu.cue.practicas.controller.estudiante;

import co.edu.cue.practicas.dto.request.CrearEstudianteRequest;
import co.edu.cue.practicas.dto.request.EnviarAProcesoRequest;
import co.edu.cue.practicas.dto.request.FiltroEstudiantesRequest;
import co.edu.cue.practicas.dto.request.ValidarAptitudRequest;
import co.edu.cue.practicas.dto.response.ApiResponse;
import co.edu.cue.practicas.dto.response.EstudianteResponse;
import co.edu.cue.practicas.model.enums.EstadoEstudiante;
import co.edu.cue.practicas.security.CustomUserDetails;
import co.edu.cue.practicas.service.estudiante.EstudianteService;
import co.edu.cue.practicas.service.estudiante.filtro.ListadoEstudiantesService;
import co.edu.cue.practicas.service.validacion.ValidacionAptitudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {

    private final EstudianteService estudianteService;
    private final ListadoEstudiantesService listadoEstudiantesService;
    private final ValidacionAptitudService validacionAptitudService;

    @PostMapping
    public ResponseEntity<ApiResponse<EstudianteResponse>> crear(
            @Valid @RequestBody CrearEstudianteRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Estudiante creado", estudianteService.crear(request, userDetails)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<EstudianteResponse>>> listar(
            @RequestParam(required = false) EstadoEstudiante estado,
            @PageableDefault(sort = "id") Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(ApiResponse.ok(estudianteService.listar(estado, pageable, userDetails)));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<Page<EstudianteResponse>>> buscar(
            @ModelAttribute FiltroEstudiantesRequest filtro,
            @PageableDefault(sort = "id") Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(ApiResponse.ok(listadoEstudiantesService.listar(filtro, pageable, userDetails)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EstudianteResponse>> obtener(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(ApiResponse.ok(estudianteService.obtener(id, userDetails)));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ApiResponse<Void>> desactivar(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        estudianteService.desactivar(id, userDetails);
        return ResponseEntity.ok(ApiResponse.ok("Estudiante desactivado", null));
    }

    @PostMapping("/{id}/aptitud")
    public ResponseEntity<ApiResponse<Void>> validarAptitud(
            @PathVariable Long id,
            @Valid @RequestBody ValidarAptitudRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        validacionAptitudService.validarAptitud(id, request, userDetails);
        return ResponseEntity.ok(ApiResponse.ok("Aptitud registrada", null));
    }

    @PostMapping("/enviar-proceso")
    public ResponseEntity<ApiResponse<Void>> enviarAProceso(
            @Valid @RequestBody EnviarAProcesoRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        estudianteService.enviarAProceso(request, userDetails);
        return ResponseEntity.ok(ApiResponse.ok("Estudiantes enviados al proceso", null));
    }
}

