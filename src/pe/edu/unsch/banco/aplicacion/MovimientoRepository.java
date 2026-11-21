package pe.edu.unsch.banco.aplicacion;

/**
 * Contrato (puerto) de persistencia de movimientos.
 * La capa de aplicacion define QUE necesita, no COMO se guarda.
 */
public interface MovimientoRepository {

    void guardar(String movimiento);
}