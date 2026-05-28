package co.edu.cue.practicas.repository.estudiante;

import co.edu.cue.practicas.model.entity.ExpedientePracticas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpedientePracticasRepository extends JpaRepository<ExpedientePracticas, Long> {
}

