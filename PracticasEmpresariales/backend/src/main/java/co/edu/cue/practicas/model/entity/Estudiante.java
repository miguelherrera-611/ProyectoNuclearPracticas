package co.edu.cue.practicas.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "estudiantes", indexes = {
        @Index(name = "idx_estudiante_identificacion", columnList = "identificacion", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(nullable = false, length = 30)
    private String identificacion;

    @Column(name = "contacto_emergencia", nullable = false, length = 200)
    private String contactoEmergencia;

    @Column(nullable = false)
    private int semestre;

    @Column(name = "creditos_aprobados", nullable = false)
    private int creditosAprobados;

    @Column(name = "promedio_academico", nullable = false)
    private double promedioAcademico;

    @Column(name = "documentos_base_completos", nullable = false)
    private boolean documentosBaseCompletos;

    @Column(name = "hoja_vida_valida", nullable = false)
    private boolean hojaVidaValida;

    @Column(name = "enviado_proceso", nullable = false)
    @Builder.Default
    private boolean enviadoProceso = false;

    @OneToOne(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ExpedientePracticas expediente;

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HojaVidaVersion> hojasVida = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime creadoEn = LocalDateTime.now();
}

