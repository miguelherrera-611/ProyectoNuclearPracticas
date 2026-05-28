package co.edu.cue.practicas.dto.response;

import co.edu.cue.practicas.model.entity.CatalogoPractica;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CatalogoPracticaResponse {

    private Long id;
    private Long programaId;
    private String programaNombre;
    private int numeroPractica;
    private String nombre;
    private String materiaNucleoNombre;
    private String materiaNucleoCodigo;
    private int numeroCortesSeguimiento;
    private int duracionSemanas;
    private String documentosRequeridos;
    private boolean activo;
    private LocalDateTime creadoEn;

    public static CatalogoPracticaResponse desde(CatalogoPractica c) {
        return CatalogoPracticaResponse.builder()
                .id(c.getId())
                .programaId(c.getPrograma().getId())
                .programaNombre(c.getPrograma().getNombre())
                .numeroPractica(c.getNumeroPractica())
                .nombre(c.getNombre())
                .materiaNucleoNombre(c.getMateriaNucleoNombre())
                .materiaNucleoCodigo(c.getMateriaNucleoCodigo())
                .numeroCortesSeguimiento(c.getNumeroCortesSeguimiento())
                .duracionSemanas(c.getDuracionSemanas())
                .documentosRequeridos(c.getDocumentosRequeridos())
                .activo(c.isActivo())
                .creadoEn(c.getCreadoEn())
                .build();
    }
}

