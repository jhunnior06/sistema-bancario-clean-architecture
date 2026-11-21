package pe.edu.unsch.banco.infraestructura;

import pe.edu.unsch.banco.aplicacion.MovimientoRepository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Adaptador de infraestructura: implementa el contrato escribiendo en movimientos.txt.
 * Depende de la interfaz (aplicacion), nunca al reves.
 * IMPORTANTE: es solo un HISTORIAL; no permite reconstruir la cuenta al reiniciar.
 */
public class ArchivoMovimientoRepository implements MovimientoRepository {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Path archivo;

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