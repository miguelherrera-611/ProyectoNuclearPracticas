package co.edu.cue.practicas.repository.catalogo;

import co.edu.cue.practicas.model.entity.CatalogoPractica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CatalogoPracticaRepository extends JpaRepository<CatalogoPractica, Long> {

    boolean existsByPrograma_IdAndNumeroPractica(Long programaId, int numeroPractica);

    Optional<CatalogoPractica> findByPrograma_IdAndNumeroPracticaAndActivoTrue(Long programaId, int numeroPractica);

    Page<CatalogoPractica> findByPrograma_IdAndActivoTrue(Long programaId, Pageable pageable);
}

