package pe.edu.unsch.banco.infraestructura.salida;

import pe.edu.unsch.banco.aplicacion.puertos.MovimientoRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador de salida que guarda los movimientos en una lista en memoria.
 * Sirve como alternativa al archivo TXT; los movimientos se pierden al cerrar el programa.
 */
public class MemoriaMovimientoRepository implements MovimientoRepository {

    private final List<String> movimientos = new ArrayList<>();

    @Override
    public void guardar(String movimiento) {
        movimientos.add(movimiento);
    }

    public List<String> listarMovimientos() {
        return List.copyOf(movimientos);
    }
}