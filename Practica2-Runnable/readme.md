# Ejercicio de Gestión de Llamadas con Hilos en Java

## Descripción

Este ejercicio tiene como objetivo familiarizarte con la programación concurrente en Java utilizando **hilos** (threads). Simularás el funcionamiento de una centralita telefónica en la que varios **empleados** atienden llamadas de manera asíncrona. Las llamadas se asignarán a los empleados en función de su **prioridad**.

Además, aprenderás a utilizar un **logger** para rastrear el flujo de eventos en una aplicación concurrente y a realizar pruebas automatizadas con **JUnit** para verificar el correcto funcionamiento del sistema.

## Objetivos

Completar los bloques de código comentados con TODO para que pasen todos los tests.

### Objetivos extra

- Utilizar ExecutorService para reutilizar hilos
- Generar 2 casos de test más
- Generar al menos un log de nivel debug, warning e info en otros puntos del código
    [Con sentido]
- Gestionar las llamadas no asignadas en una cola

## Clases principales

### 1. **Empleado**
Esta clase representa a un empleado que atiende llamadas. Cada empleado tiene:
- Un nombre.
- Un nivel de prioridad (empleados con menor número de prioridad tienen preferencia para atender llamadas).
  
Un empleado atiende las llamadas mediante el método `atenderLlamada(Llamada llamada)`, el cual es simulado con un `sleep()` para representar el tiempo de duración de la llamada.

**Logger**: Durante la ejecución, el logger registrará eventos como:
- "Empleado está atendiendo la llamada ..."
- "Empleado atendió la llamada {id}"

### 2. **Llamada**
Esta clase representa una llamada telefónica que llega a la centralita. Contiene dos atributos:
- `duracion`: el tiempo que la llamada durará (en segundos).
- `id`: un identificador único para la llamada, usado para los logs.

### 3. **Centralita**
La centralita es la encargada de recibir las llamadas y asignarlas a los empleados disponibles según la prioridad. Debes implementar el método:
- `atenderLlamada(Llamada llamada)`: Este método seleccionará al empleado disponible de mayor prioridad y le asignará la llamada. El proceso debe ser **asíncrono** usando hilos.

### 4. **Logger**
El ejercicio utiliza la biblioteca SLF4J para el logging. Los logs son útiles para hacer un seguimiento de los eventos en un entorno multihilo. Los mensajes que se logean son:
- Cuando un empleado comienza a atender una llamada.
- Cuando un empleado termina de atender la llamada.

## Pruebas Unitarias

Se han proporcionado pruebas automatizadas utilizando **JUnit** para verificar el comportamiento de las clases. Las pruebas comprueban:
- Que los empleados atienden correctamente las llamadas.
- Que las llamadas se asignan a los empleados según su prioridad.
- Que el sistema maneja correctamente llamadas concurrentes.

En las pruebas, se verifica que los **logs** se generan correctamente. Dado que el sistema es concurrente, **el orden de los logs no es importante**, pero los mensajes correctos deben estar presentes.

### Ejemplo de prueba:
```java
@Test
public void testLlamadasMultiplesSimultaneas() throws InterruptedException {
    Empleado e1 = new Empleado(0, "Alberto");
    Empleado e2 = new Empleado(1, "Carlos");
    centralita.conEmpleado(e1);
    centralita.conEmpleado(e2);

    centralita.atenderLlamada(new Llamada(2, 103)); // Asignado a Alberto
    centralita.atenderLlamada(new Llamada(2, 104)); // Asignado a Carlos

    TimeUnit.SECONDS.sleep(3); // Esperamos a que ambas llamadas se completen

    List<LoggingEvent> events = testLogger.getLoggingEvents();
    assertEquals(4, events.size());

    assertTrue(events.stream().anyMatch(event -> event.getMessage().equals("Alberto está atendiendo la llamada ...")));
    assertTrue(events.stream().anyMatch(event -> event.getMessage().equals("Alberto atendió la llamada 103")));
    assertTrue(events.stream().anyMatch(event -> event.getMessage().equals("Carlos está atendiendo la llamada ...")));
    assertTrue(events.stream().anyMatch(event -> event.getMessage().equals("Carlos atendió la llamada 104")));
}
```

