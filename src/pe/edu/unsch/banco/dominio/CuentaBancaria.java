package pe.edu.unsch.banco.dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Entidad de dominio: representa una cuenta bancaria y sus reglas de negocio.
 * No conoce Scanner, System.out ni FileWriter (no depende de ninguna otra capa).
 */
public class CuentaBancaria {

    private static final int DECIMALES = 2;

    private final String titular;
    private BigDecimal saldo;

    public CuentaBancaria(String titular, BigDecimal saldoInicial) {
        if (titular == null || titular.isBlank()) {
            throw new IllegalArgumentException("El titular no puede estar vacio");
        }
        if (saldoInicial == null || saldoInicial.signum() < 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser negativo");
        }
        validarDecimales(saldoInicial);
        this.titular = titular.trim();
        this.saldo = saldoInicial.setScale(DECIMALES, RoundingMode.UNNECESSARY);
    }

    public void depositar(BigDecimal monto) {
        validarMonto(monto);
        saldo = saldo.add(monto);
    }

    public void retirar(BigDecimal monto) {
        validarMonto(monto);
        if (monto.compareTo(saldo) > 0) {
            throw new IllegalStateException("Fondos insuficientes. Saldo disponible: S/ " + saldo);
        }
        saldo = saldo.subtract(monto);
    }

    private void validarMonto(BigDecimal monto) {
        if (monto == null || monto.signum() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
        validarDecimales(monto);
    }

    private void validarDecimales(BigDecimal valor) {
        if (valor.stripTrailingZeros().scale() > DECIMALES) {
            throw new IllegalArgumentException("El importe admite como maximo " + DECIMALES + " decimales");
        }
    }

    public String getTitular() {
        return titular;
    }

    public BigDecimal getSaldo() {
        return saldo.setScale(DECIMALES, RoundingMode.UNNECESSARY);
    }
}