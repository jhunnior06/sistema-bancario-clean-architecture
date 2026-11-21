package pe.edu.unsch.banco.infraestructura;

import pe.edu.unsch.banco.aplicacion.CuentaService;
import pe.edu.unsch.banco.aplicacion.MovimientoRepository;
import pe.edu.unsch.banco.dominio.CuentaBancaria;

import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Entrada/presentacion: muestra el menu, lee datos, maneja errores
 * y ensambla las dependencias (inyecta el repositorio en el servicio).
 */
public class Main {

    private static final String AVISO_PERSISTENCIA =
            "AVISO: la cuenta se mantiene solo en memoria. Al cerrar el programa NO se restaura "
            + "automaticamente; movimientos.txt es solo un historial.";

    private final Scanner scanner;
    private final CuentaService servicio;

    public Main(Scanner scanner, CuentaService servicio) {
        this.scanner = scanner;
        this.servicio = servicio;
    }

    public static void main(String[] args) {
        MovimientoRepository repositorio = new ArchivoMovimientoRepository("movimientos.txt");
        CuentaService servicio = new CuentaService(repositorio);
        new Main(new Scanner(System.in), servicio).iniciar();
    }

    private void iniciar() {
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
                    case 5 -> System.out.println("Saliendo del sistema...\n" + AVISO_PERSISTENCIA);
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
        if (servicio.existeCuentaActiva()) {
            // Aviso temprano; la regla real la hace cumplir CuentaService.registrar()
            System.out.println("Operacion rechazada: ya existe una cuenta activa a nombre de "
                    + servicio.consultar().getTitular() + ". No se puede reemplazar.");
            return;
        }
        System.out.print("Ingrese titular: ");
        String titular = leerLinea();
        BigDecimal saldoInicial = leerImporte("Ingrese saldo inicial: ");
        CuentaBancaria cuenta = servicio.registrar(titular, saldoInicial);
        System.out.println("Cuenta registrada | Titular: " + cuenta.getTitular()
                + " | Saldo: S/ " + cuenta.getSaldo());
    }

    private void depositar() {
        servicio.consultar(); // falla temprano si no hay cuenta, antes de pedir el monto
        BigDecimal saldo = servicio.depositar(leerImporte("Monto a depositar: "));
        System.out.println("Deposito exitoso | Saldo actual: S/ " + saldo);
    }

    private void retirar() {
        servicio.consultar();
        BigDecimal saldo = servicio.retirar(leerImporte("Monto a retirar: "));
        System.out.println("Retiro exitoso | Saldo actual: S/ " + saldo);
    }

    private void consultar() {
        CuentaBancaria cuenta = servicio.consultar();
        System.out.println("Titular: " + cuenta.getTitular());
        System.out.println("Saldo: S/ " + cuenta.getSaldo());
    }

    private int leerOpcion() {
        String texto = leerLinea().trim();
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return -1; // cae en "Opcion invalida" sin romper el programa
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