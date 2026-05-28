package co.edu.cue.practicas.dto.response;

import co.edu.cue.practicas.model.entity.PracticaInstancia;
import co.edu.cue.practicas.model.enums.EstadoPractica;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PracticaResumenResponse {

    private Long id;
    private int numeroPractica;
    private String nombre;
    private String materiaNucleoNombre;
    private String materiaNucleoCodigo;
    private EstadoPractica estado;

    public static PracticaResumenResponse desde(PracticaInstancia p) {
        return PracticaResumenResponse.builder()
                .id(p.getId())
                .numeroPractica(p.getNumeroPractica())
                .nombre(p.getNombre())
                .materiaNucleoNombre(p.getMateriaNucleoNombre())
                .materiaNucleoCodigo(p.getMateriaNucleoCodigo())
                .estado(p.getEstado())
                .build();
    }
}

