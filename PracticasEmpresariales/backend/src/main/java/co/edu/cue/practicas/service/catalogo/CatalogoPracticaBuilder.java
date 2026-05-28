package co.edu.cue.practicas.service.catalogo;

import co.edu.cue.practicas.exception.OperacionNoPermitidaException;
import co.edu.cue.practicas.model.entity.CatalogoPractica;
import co.edu.cue.practicas.model.entity.Programa;

/**
 * PATRON BUILDER — GPE-141
 * Construye una entrada del catalogo paso a paso.
 */
public class CatalogoPracticaBuilder {

    private final CatalogoPractica catalogo = new CatalogoPractica();

    public static CatalogoPracticaBuilder nuevo() {
        return new CatalogoPracticaBuilder();
    }

    public CatalogoPracticaBuilder enPrograma(Programa programa) {
        catalogo.setPrograma(programa);
        return this;
    }

    public CatalogoPracticaBuilder conNumeroPractica(int numeroPractica) {
        catalogo.setNumeroPractica(numeroPractica);
        return this;
    }

    public CatalogoPracticaBuilder conNombre(String nombre) {
        catalogo.setNombre(nombre);
        return this;
    }

    public CatalogoPracticaBuilder conMateriaNucleo(String nombre, String codigo) {
        catalogo.setMateriaNucleoNombre(nombre);
        catalogo.setMateriaNucleoCodigo(codigo);
        return this;
    }

    public CatalogoPracticaBuilder conCortesSeguimiento(int numeroCortes) {
        catalogo.setNumeroCortesSeguimiento(numeroCortes);
        return this;
    }

    public CatalogoPracticaBuilder conDuracionSemanas(int duracionSemanas) {
        catalogo.setDuracionSemanas(duracionSemanas);
        return this;
    }

    public CatalogoPracticaBuilder conDocumentosRequeridos(String documentosRequeridos) {
        catalogo.setDocumentosRequeridos(documentosRequeridos);
        return this;
    }

    public CatalogoPractica construir() {
        if (catalogo.getPrograma() == null) {
            throw new OperacionNoPermitidaException("El programa es obligatorio en el catalogo.");
        }
        if (catalogo.getNombre() == null || catalogo.getNombre().isBlank()) {
            throw new OperacionNoPermitidaException("El nombre de la practica es obligatorio.");
        }
        if (catalogo.getMateriaNucleoNombre() == null || catalogo.getMateriaNucleoNombre().isBlank()) {
            throw new OperacionNoPermitidaException("La materia nucleo es obligatoria.");
        }
        if (catalogo.getMateriaNucleoCodigo() == null || catalogo.getMateriaNucleoCodigo().isBlank()) {
            throw new OperacionNoPermitidaException("El codigo de la materia nucleo es obligatorio.");
        }
        return catalogo;
    }
}

