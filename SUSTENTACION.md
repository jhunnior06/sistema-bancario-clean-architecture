# Preguntas de sustentación

**1. ¿Qué problema concreto resolvieron al separar CuentaBancaria de Main?**
Las reglas del negocio (monto mayor que cero, no retirar más que el saldo, máximo 2 decimales) quedaron en un solo lugar, independientes de la consola. Antes la validación estaba mezclada con `Scanner` y `System.out`, y se podía operar sin cuenta o con saldo negativo. Ahora `CuentaBancaria` se puede reutilizar con otra interfaz y probar sin teclado ni archivo.

**2. ¿Por qué CuentaService recibe MovimientoRepository en su constructor?**
Por inversión de dependencias: el servicio depende de una **interfaz**, no de una clase concreta. `Main` decide qué implementación inyectar (archivo hoy, H2 mañana, o una falsa para pruebas). El constructor deja explícita la dependencia y garantiza que el servicio nunca exista sin repositorio.

**3. ¿Qué archivos deberían cambiar si reemplazamos FileWriter por JDBC/H2?**
Crear `infraestructura/H2MovimientoRepository.java` que implemente `MovimientoRepository`, cambiar una línea en `Main.java` para crear esa clase y agregar el driver de H2. `CuentaBancaria`, `CuentaService` y `MovimientoRepository` **no cambian**.

**4. ¿En qué capa se valida que el retiro no exceda el saldo?**
En el **dominio**, en `CuentaBancaria.retirar()`. Es una regla del negocio bancario, no un detalle de la interfaz ni del almacenamiento.

**5. ¿Qué diferencia existe entre registrar movimientos y persistir cuentas?**
Registrar movimientos es escribir un historial de eventos. Persistir cuentas es guardar el **estado** (titular y saldo) para recuperarlo al reiniciar. `movimientos.txt` nunca se lee, por eso la cuenta se pierde al cerrar.

# Dificultades
- Decidir en qué capa va cada validación: "cuenta existente" en el servicio, reglas de montos en la entidad.
- Entender que la interfaz `MovimientoRepository` va en `aplicacion` y su implementación en `infraestructura`.
- Migrar de `double` a `BigDecimal` (`add()`, `subtract()`, `compareTo()`).
- Conflicto en `.gitignore` al subir el proyecto porque el repositorio de GitHub se creó con archivos.
- Un Pull Request se creó con base `main` en lugar de `develop` y hubo que cambiar la base.
- Recordar hacer `git pull` en `develop` después de cada merge.

# Conclusiones
- La clase original tenía al menos siete razones para cambiar; separarla en capas hace que cada cambio afecte a un solo archivo.
- La regla de dependencias protege las reglas del negocio de los cambios tecnológicos.
- La refactorización corrigió defectos reales: reemplazo de cuenta, operaciones sin cuenta, saldo negativo, redondeo de `double` y caídas por entrada inválida.
- El historial de movimientos no equivale a persistencia; queda como mejora para el laboratorio 7-B.
