package co.edu.cue.practicas.repository.estudiante;

import co.edu.cue.practicas.model.entity.HojaVidaVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HojaVidaRepository extends JpaRepository<HojaVidaVersion, Long> {

    Optional<HojaVidaVersion> findTopByEstudiante_IdOrderByVersionDesc(Long estudianteId);
}

