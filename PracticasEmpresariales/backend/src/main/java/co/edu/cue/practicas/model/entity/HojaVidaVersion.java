package co.edu.cue.practicas.model.entity;

import co.edu.cue.practicas.model.enums.EstadoHojaVida;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "hojas_vida")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HojaVidaVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @Column(nullable = false)
    private int version;

    @Column(name = "fecha_carga", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime fechaCarga = LocalDateTime.now();

    @Column(name = "url_archivo", nullable = false, length = 500)
    private String urlArchivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoHojaVida estado;

    @Column(name = "validado_por", length = 200)
    private String validadoPor;

    @Column(name = "fecha_validacion")
    private LocalDateTime fechaValidacion;
}

