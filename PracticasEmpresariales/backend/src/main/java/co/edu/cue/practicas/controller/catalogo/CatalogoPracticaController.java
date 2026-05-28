package co.edu.cue.practicas.controller.catalogo;

import co.edu.cue.practicas.dto.request.CrearCatalogoPracticaRequest;
import co.edu.cue.practicas.dto.request.EditarCatalogoPracticaRequest;
import co.edu.cue.practicas.dto.response.ApiResponse;
import co.edu.cue.practicas.dto.response.CatalogoPracticaResponse;
import co.edu.cue.practicas.security.CustomUserDetails;
import co.edu.cue.practicas.service.catalogo.CatalogoPracticaService;
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
@RequestMapping("/catalogo-practicas")
@RequiredArgsConstructor
public class CatalogoPracticaController {

    private final CatalogoPracticaService catalogoPracticaService;

    @PostMapping
    public ResponseEntity<ApiResponse<CatalogoPracticaResponse>> crear(
            @Valid @RequestBody CrearCatalogoPracticaRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Catalogo creado", catalogoPracticaService.crear(request, userDetails)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CatalogoPracticaResponse>> editar(
            @PathVariable Long id,
            @Valid @RequestBody EditarCatalogoPracticaRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(ApiResponse.ok("Catalogo actualizado", catalogoPracticaService.editar(id, request, userDetails)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CatalogoPracticaResponse>>> listar(
            @RequestParam Long programaId,
            @PageableDefault(sort = "numeroPractica") Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(ApiResponse.ok(catalogoPracticaService.listarPorPrograma(programaId, pageable, userDetails)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CatalogoPracticaResponse>> obtener(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(ApiResponse.ok(catalogoPracticaService.obtener(id, userDetails)));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ApiResponse<Void>> desactivar(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        catalogoPracticaService.desactivar(id, userDetails);
        return ResponseEntity.ok(ApiResponse.ok("Catalogo desactivado", null));
    }
}

