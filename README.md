# Laboratorio 07-A – Arquitectura Limpia (Clean Architecture)

**Curso:** Construcción y Evolución de Software IS-388 – EPIS UNSCH
**Docente:** Ing. J. Alexander Ayala Farfán
**Estudiante:** Briam Jhunnior Chachayma Gutierrez
**Modalidad:** trabajo individual (el estudiante asume los 4 roles de la guía)

## 1. Descripción
Refactorización del programa `SistemaBancario` (una sola clase que mezclaba entrada de datos, reglas de negocio, presentación y persistencia) hacia una estructura en capas que respeta la regla de dependencias de Clean Architecture. Se conservan los cinco comandos: registrar, depositar, retirar, consultar saldo y salir.

## 2. Requisitos
- JDK 17 o superior
- IntelliJ IDEA
- Git

## 3. Ejecución
Abrir `src/pe/edu/unsch/banco/infraestructura/Main.java` en IntelliJ y pulsar ▶ junto a `main`. El archivo `movimientos.txt` se crea en la raíz del proyecto.

## 4. Estructura
```
src/pe/edu/unsch/banco/
├── dominio/
│   └── CuentaBancaria.java              Entidad + reglas (montos, saldo, retiro)
├── aplicacion/
│   ├── CuentaService.java               Casos de uso; recibe el repositorio por constructor
│   └── MovimientoRepository.java        Contrato: guardar(String movimiento)
└── infraestructura/
    ├── ArchivoMovimientoRepository.java Implementa el contrato escribiendo en movimientos.txt
    └── Main.java                        Menú, lectura de datos, manejo de errores, ensamblado
docs/codigo-original/SistemaBancario.java  Código inicial (referencia, no se compila)
```

## 5. Regla de dependencias
```
infraestructura  ──►  aplicacion  ──►  dominio
```
- `dominio` no importa nada de `aplicacion` ni de `infraestructura` (solo `java.math`).
- `aplicacion` importa `dominio`, pero no `infraestructura`.
- `ArchivoMovimientoRepository` **implementa** la interfaz `MovimientoRepository`: el detalle depende de la abstracción, no al revés (inversión de dependencias).
- `Main` es el único lugar donde se crea la implementación concreta y se inyecta al servicio.

## 6. Decisiones técnicas
- **BigDecimal en lugar de double:** con el código original 0.1 + 0.2 daba `0.30000000000000004`. `BigDecimal` es decimal exacto y se limita a 2 decimales (céntimos).
- **Registro único:** si ya hay una cuenta activa, `CuentaService.registrar()` la rechaza.
- **Errores como excepciones:** dominio y servicio lanzan excepciones; solo `Main` las muestra.
- **Un solo Scanner con `nextLine()`:** el original usaba dos `Scanner` y `nextInt()`, y se caía al escribir letras.

## 7. Limitación conocida
`movimientos.txt` es un **historial**, no una persistencia de cuentas: al reiniciar, la cuenta no se restaura. El programa lo advierte al iniciar y al salir.

## 8. Documentos
- [DIAGNOSTICO.md](DIAGNOSTICO.md) – responsabilidades y problemas del código original
- [PRUEBAS.md](PRUEBAS.md) – casos P01–P07
- [SUSTENTACION.md](SUSTENTACION.md) – preguntas de sustentación, dificultades y conclusiones

## 9. Versión
Etiqueta `lab-7a` sobre el commit final de esta sesión (antes del laboratorio 7-B).
