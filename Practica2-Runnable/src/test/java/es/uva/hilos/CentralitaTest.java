package es.uva.hilos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;

import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CentralitaTest {

    private TestLogger testLogger;
    private Centralita centralita;

    @BeforeEach
    public void setUp() {
        testLogger = TestLoggerFactory.getTestLogger(Empleado.class);
        centralita = new Centralita();
    }

    @Test
    public void testUnEmpleadoAsincrono() throws InterruptedException {
        Empleado e1 = new Empleado(0, "Alberto");
        centralita.conEmpleado(e1);

        centralita.atenderLlamada(new Llamada(1, 101));

        TimeUnit.SECONDS.sleep(2); // Damos tiempo para que la llamada se procese

        List<LoggingEvent> events = testLogger.getAllLoggingEvents();

        // Verificamos que los logs existen, pero no en un orden específico
        assertTrue(events.stream().anyMatch(event -> event.getMessage().equals("Alberto atendió la llamada 101")));
    }

    @Test
    public void testPrioridadEmpleados() throws InterruptedException {
        Empleado e1 = new Empleado(1, "Carlos");
        Empleado e2 = new Empleado(0, "Alberto");
        centralita.conEmpleado(e1);
        centralita.conEmpleado(e2);

        centralita.atenderLlamada(new Llamada(1, 102)); // Debería elegir a Alberto

        TimeUnit.SECONDS.sleep(2); // Damos tiempo para que la llamada se procese

        List<LoggingEvent> events = testLogger.getAllLoggingEvents();

        // Verificamos que los logs existen sin exigir un orden específico
        assertTrue(events.stream().anyMatch(event -> event.getMessage().equals("Alberto atendió la llamada 102")));
    }

    @Test
    public void testLlamadasMultiplesSimultaneas() throws InterruptedException {
        Empleado e1 = new Empleado(0, "Alberto");
        Empleado e2 = new Empleado(1, "Carlos");
        centralita.conEmpleado(e1);
        centralita.conEmpleado(e2);

        centralita.atenderLlamada(new Llamada(2, 103)); // Debería asignarse a Alberto
        centralita.atenderLlamada(new Llamada(2, 104)); // Carlos debería atender esta

        TimeUnit.SECONDS.sleep(3); // Esperamos que ambas llamadas se completen

        List<LoggingEvent> events = testLogger.getAllLoggingEvents();

        // Verificamos que los logs existen, no importa el orden
        assertTrue(events.stream().anyMatch(event -> event.getMessage().equals("Alberto atendió la llamada 103")));
        assertTrue(events.stream().anyMatch(event -> event.getMessage().equals("Carlos atendió la llamada 104")));
    }

    @Test
    public void testTodosEmpleadosOcupados() throws InterruptedException {
        Empleado e1 = new Empleado(0, "Alberto");
        Empleado e2 = new Empleado(1, "Carlos");
        centralita.conEmpleado(e1);
        centralita.conEmpleado(e2);

        centralita.atenderLlamada(new Llamada(4, 105)); // Alberto atiende
        centralita.atenderLlamada(new Llamada(5, 106)); // Carlos atiende

        TimeUnit.SECONDS.sleep(1); // Esperamos para que las llamadas estén en curso

        centralita.atenderLlamada(new Llamada(1, 107)); // La tercera llamada espera a que Alberto quede libre

        TimeUnit.SECONDS.sleep(6); // Esperamos lo suficiente para que la tercera llamada se procese

        List<LoggingEvent> events = testLogger.getAllLoggingEvents();

        // Verificamos que los logs existen sin orden estricto
        assertTrue(events.stream().anyMatch(event -> event.getMessage().equals("Alberto atendió la llamada 105")));
        assertTrue(events.stream().anyMatch(event -> event.getMessage().equals("Carlos atendió la llamada 106")));
        assertTrue(events.stream().anyMatch(event -> event.getMessage().equals("Alberto atendió la llamada 107"))); // La tercera llamada
    }

    @Test
    public void testLlamadaDuracionCero() throws InterruptedException {
        Empleado e1 = new Empleado(0, "Alberto");
        centralita.conEmpleado(e1);

        centralita.atenderLlamada(new Llamada(0, 108)); // La llamada no debería bloquearse

        TimeUnit.SECONDS.sleep(1); // Esperamos lo suficiente para que la tercera llamada se procese

        List<LoggingEvent> events = testLogger.getAllLoggingEvents();
        assertTrue(events.stream().anyMatch(event -> event.getMessage().equals("Alberto atendió la llamada 108")));
    }

    @Test
    public void testAltaCargaDeLlamadas() throws InterruptedException {
        Empleado e1 = new Empleado(0, "Alberto");
        Empleado e2 = new Empleado(1, "Carlos");
        centralita.conEmpleado(e1);
        centralita.conEmpleado(e2);

        // Send multiple calls
        List<Integer> callIds = List.of(200, 201, 202, 203, 204); // Crear IDs separados para la compatibilidad de la función lambda
        for (int callId : callIds) {
            centralita.atenderLlamada(new Llamada(2, callId));
        }

        TimeUnit.SECONDS.sleep(10); // Esperar para permitir a todas las llamadas procesarse

        List<LoggingEvent> events = testLogger.getAllLoggingEvents();

        // Verificar que todas las llamadas han sido atendidas
        for (int callId : callIds) {
            assertTrue(events.stream().anyMatch(event -> event.getMessage().contains("atendió la llamada " + callId)));
        }
    }

}
