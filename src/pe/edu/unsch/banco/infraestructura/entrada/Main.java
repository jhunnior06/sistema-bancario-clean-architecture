package pe.edu.unsch.banco.infraestructura.entrada;

import pe.edu.unsch.banco.aplicacion.CuentaService;
import pe.edu.unsch.banco.aplicacion.puertos.CuentaUseCase;
import pe.edu.unsch.banco.aplicacion.puertos.MovimientoRepository;
import pe.edu.unsch.banco.infraestructura.salida.ArchivoMovimientoRepository;
import pe.edu.unsch.banco.infraestructura.salida.MemoriaMovimientoRepository;

import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Adaptador de entrada por consola y punto donde se arman las dependencias.
 * Aquí se elige qué adaptador de salida usar; el dominio y el servicio no cambian.
 */
public class Main {

    private static final String AVISO_PERSISTENCIA =
            "AVISO: la cuenta se mantiene solo en memoria. Al cerrar el programa NO se restaura "
                    + "automaticamente; solo se guarda el historial de movimientos.";

    private final Scanner scanner;
    private final CuentaUseCase servicio;
    private final MovimientoRepository repository;

    public Main(Scanner scanner, CuentaUseCase servicio, MovimientoRepository repository) {
        this.scanner = scanner;
        this.servicio = servicio;
        this.repository = repository;
    }

    public static void main(String[] args) {
        // Prueba A: archivo
        MovimientoRepository repository = new ArchivoMovimientoRepository();

        // Prueba B: memoria (sustituye la linea anterior)
        //MovimientoRepository repository = new MemoriaMovimientoRepository();

        CuentaUseCase servicio = new CuentaService(repository);
        new Main(new Scanner(System.in), servicio, repository).iniciar();
    }

    private void iniciar() {
        System.out.println("Adaptador de salida: " + repository.getClass().getSimpleName());
        System.out.println(AVISO_PERSISTENCIA);
        int opcion;
        do {
            mostrarMenu();
            opcion = leerOpcion();
            try {
                switch (opcion) {
                    case 1 -> registrar();
                    case 2 -> depositar();
                    case 3 -> retirar();
                    case 4 -> consultar();
                    case 5 -> salir();
                    default -> System.out.println("Opcion invalida");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Operacion rechazada: " + e.getMessage());
            } catch (UncheckedIOException e) {
                System.out.println("La operacion se aplico, pero no se pudo guardar el movimiento: "
                        + e.getMessage());
            }
        } while (opcion != 5);
    }

    private void mostrarMenu() {
        System.out.println("\n=== SISTEMA BANCARIO ===");
        System.out.println("1. Registrar cuenta");
        System.out.println("2. Depositar dinero");
        System.out.println("3. Retirar dinero");
        System.out.println("4. Consultar saldo");
        System.out.println("5. Salir");
        System.out.print("Seleccione una opcion: ");
    }

    private void registrar() {
        if (servicio.existeCuenta()) {
            System.out.println("Operacion rechazada: ya existe una cuenta activa a nombre de "
                    + servicio.consultarTitular() + ". No se puede reemplazar.");
            return;
        }
        System.out.print("Ingrese titular: ");
        String titular = leerLinea();
        BigDecimal saldoInicial = leerImporte("Ingrese saldo inicial: ");
        servicio.registrarCuenta(titular, saldoInicial);
        System.out.println("Cuenta registrada | Titular: " + servicio.consultarTitular()
                + " | Saldo: S/ " + servicio.consultarSaldo());
    }

    private void depositar() {
        servicio.consultarSaldo(); // si no hay cuenta, avisa antes de pedir el monto
        servicio.depositar(leerImporte("Monto a depositar: "));
        System.out.println("Deposito exitoso | Saldo actual: S/ " + servicio.consultarSaldo());
    }

    private void retirar() {
        servicio.consultarSaldo();
        servicio.retirar(leerImporte("Monto a retirar: "));
        System.out.println("Retiro exitoso | Saldo actual: S/ " + servicio.consultarSaldo());
    }

    private void consultar() {
        System.out.println("Titular: " + servicio.consultarTitular());
        System.out.println("Saldo: S/ " + servicio.consultarSaldo());
    }

    private void salir() {
        if (repository instanceof MemoriaMovimientoRepository memoria) {
            System.out.println("Movimientos guardados en memoria (" + memoria.listarMovimientos().size() + "):");
            memoria.listarMovimientos().forEach(m -> System.out.println("  " + m));
        } else {
            System.out.println("Movimientos guardados en movimientos.txt");
        }
        System.out.println("Saliendo del sistema...\n" + AVISO_PERSISTENCIA);
    }

    private int leerOpcion() {
        String texto = leerLinea().trim();
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private BigDecimal leerImporte(String mensaje) {
        System.out.print(mensaje);
        String texto = leerLinea().trim().replace(',', '.');
        try {
            return new BigDecimal(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + texto + "' no es un importe valido");
        }
    }

    private String leerLinea() {
        if (!scanner.hasNextLine()) {
            System.out.println("\nEntrada finalizada. " + AVISO_PERSISTENCIA);
            System.exit(0);
        }
        return scanner.nextLine();
    }
}