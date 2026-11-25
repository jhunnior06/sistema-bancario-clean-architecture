package pe.edu.unsch.banco.aplicacion.puertos;

/**
 * Puerto de salida del sistema bancario.
 * Define lo único que el núcleo necesita para guardar el historial de movimientos.
 * Los adaptadores de archivo y de memoria implementan esta interfaz.
 */
public interface MovimientoRepository {

    void guardar(String movimiento);
}