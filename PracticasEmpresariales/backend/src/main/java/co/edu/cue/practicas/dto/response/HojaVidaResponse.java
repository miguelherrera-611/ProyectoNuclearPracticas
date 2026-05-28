package co.edu.cue.practicas.dto.response;

import co.edu.cue.practicas.model.entity.HojaVidaVersion;
import co.edu.cue.practicas.model.enums.EstadoHojaVida;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HojaVidaResponse {

    private Long id;
    private int version;
    private LocalDateTime fechaCarga;
    private String urlArchivo;
    private EstadoHojaVida estado;
    private String validadoPor;
    private LocalDateTime fechaValidacion;

    public static HojaVidaResponse desde(HojaVidaVersion hv) {
        return HojaVidaResponse.builder()
                .id(hv.getId())
                .version(hv.getVersion())
                .fechaCarga(hv.getFechaCarga())
                .urlArchivo(hv.getUrlArchivo())
                .estado(hv.getEstado())
                .validadoPor(hv.getValidadoPor())
                .fechaValidacion(hv.getFechaValidacion())
                .build();
    }
}

