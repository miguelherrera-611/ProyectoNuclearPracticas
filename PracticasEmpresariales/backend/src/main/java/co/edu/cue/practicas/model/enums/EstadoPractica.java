package co.edu.cue.practicas.model.enums;

/**
 * Estado de la práctica empresarial.
 * El estado solo avanza: ASIGNADA_PENDIENTE_INICIO → EN_CURSO → FINALIZADA → CANCELADA.
 * Nunca retrocede.
 */
public enum EstadoPractica {
    ASIGNADA_PENDIENTE_INICIO,
    EN_CURSO,
    FINALIZADA,
    CANCELADA
}
