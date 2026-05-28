package co.edu.cue.practicas.model.entity;

import co.edu.cue.practicas.model.enums.EstadoPractica;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "catalogo_practicas", uniqueConstraints = {
        @UniqueConstraint(name = "uk_catalogo_programa_numero", columnNames = {"programa_id", "numero_practica"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogoPractica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programa_id", nullable = false)
    private Programa programa;

    @Column(name = "numero_practica", nullable = false)
    private int numeroPractica;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(name = "materia_nucleo_nombre", nullable = false, length = 200)
    private String materiaNucleoNombre;

    @Column(name = "materia_nucleo_codigo", nullable = false, length = 50)
    private String materiaNucleoCodigo;

    @Column(name = "numero_cortes_seguimiento", nullable = false)
    private int numeroCortesSeguimiento;

    @Column(name = "duracion_semanas", nullable = false)
    private int duracionSemanas;

    /** Documentos requeridos separados por coma. */
    @Column(name = "documentos_requeridos", nullable = false, length = 1000)
    private String documentosRequeridos;

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime creadoEn = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime actualizadoEn = LocalDateTime.now();

    @PreUpdate
    void onUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }

    /**
     * PATRON PROTOTYPE — clona la plantilla para crear una instancia independiente.
     */
    public PracticaInstancia clonarPara(ExpedientePracticas expediente) {
        return PracticaInstancia.builder()
                .expediente(expediente)
                .catalogoPractica(this)
                .numeroPractica(this.numeroPractica)
                .nombre(this.nombre)
                .materiaNucleoNombre(this.materiaNucleoNombre)
                .materiaNucleoCodigo(this.materiaNucleoCodigo)
                .numeroCortesSeguimiento(this.numeroCortesSeguimiento)
                .duracionSemanas(this.duracionSemanas)
                .documentosRequeridos(this.documentosRequeridos)
                .estado(EstadoPractica.ASIGNADA_PENDIENTE_INICIO)
                .build();
    }
}

