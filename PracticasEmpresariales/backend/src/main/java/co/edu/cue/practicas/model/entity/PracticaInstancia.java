package co.edu.cue.practicas.model.entity;

import co.edu.cue.practicas.model.enums.EstadoPractica;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "practicas_instancia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PracticaInstancia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediente_id", nullable = false)
    private ExpedientePracticas expediente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalogo_practica_id")
    private CatalogoPractica catalogoPractica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_asesor_id")
    private Usuario docenteAsesor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_empresarial_id")
    private Usuario tutorEmpresarial;

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

    @Column(name = "documentos_requeridos", nullable = false, length = 1000)
    private String documentosRequeridos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private EstadoPractica estado;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime creadoEn = LocalDateTime.now();
}

