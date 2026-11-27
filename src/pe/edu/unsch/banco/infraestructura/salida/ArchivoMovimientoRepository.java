package pe.edu.unsch.banco.infraestructura.salida;

import pe.edu.unsch.banco.aplicacion.puertos.MovimientoRepository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Adaptador de salida que guarda cada movimiento en el archivo movimientos.txt.
 * Implementa el puerto MovimientoRepository; el núcleo no sabe que existe un archivo.
 * Solo es un historial: al reiniciar el programa no se reconstruye la cuenta.
 */
public class ArchivoMovimientoRepository implements MovimientoRepository {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Path archivo;

    public ArchivoMovimientoRepository() {
        this("movimientos.txt");
    }

    public ArchivoMovimientoRepository(String rutaArchivo) {
        this.archivo = Path.of(rutaArchivo);
    }

    @Override
    public void guardar(String movimiento) {
        String linea = LocalDateTime.now().format(FORMATO) + " | " + movimiento + System.lineSeparator();
        try {
            Files.writeString(archivo, linea, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo escribir en " + archivo.toAbsolutePath(), e);
        }
    }
}