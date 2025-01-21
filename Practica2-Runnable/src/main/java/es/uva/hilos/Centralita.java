package es.uva.hilos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Centralita {
	private final List<Empleado> empleados = new ArrayList<>();
	private final Queue<Llamada> colaLlamadas = new LinkedList<>();
	private final Map<Empleado, Boolean> estadoEmpleados = new HashMap<>();
	private final Lock lock = new ReentrantLock();
	private final ExecutorService executorService = Executors.newFixedThreadPool(10); // Cambia el número de hilos según sea necesario

	private final static Logger logger = LoggerFactory.getLogger(Centralita.class);

	public void conEmpleado(Empleado empleado) {
		empleados.add(empleado);
		estadoEmpleados.put(empleado, false); // Inicialmente libre
		logger.info("Empleado {} añadido a la centralita", empleado.getNombre());
	}

	private void atenderLlamadaConEmpleado(Empleado empleado, Llamada llamada) {
		try {
			empleado.atenderLlamada(llamada);
		} catch (InterruptedException e) {
			logger.error("Error al atender la llamada {} por el empleado {}", llamada.getId(), empleado.getNombre(), e);
			Thread.currentThread().interrupt(); // Mantiene el estado de interrupción
		} finally {
			lock.lock();
			try {
				estadoEmpleados.put(empleado, false); // Marca al empleado como libre
				// Revisa si hay llamadas en espera
				procesarLlamadasEnEspera();
				logger.debug("Empleado {} ha terminado de atender la llamada {}", empleado.getNombre(), llamada.getId());
				// Aquí podrías manejar la siguiente llamada de la cola si es necesario
			} finally {
				lock.unlock();
			}
		}
	}

	public void atenderLlamada(Llamada llamada) {
		lock.lock();
		try {
			Empleado empleadoDisponible = null;

			for (Empleado empleado : empleados) {
				if (!estadoEmpleados.get(empleado)) { // Está disponible
					if (empleadoDisponible == null || empleado.getPrioridad() < empleadoDisponible.getPrioridad()) {
						empleadoDisponible = empleado;
					}
				}
			}

			if (empleadoDisponible != null) {
				final Empleado empleadoFinal = empleadoDisponible;
				estadoEmpleados.put(empleadoFinal, true); // Marca el empleado como ocupado
				logger.info("Empleado {} está atendiendo la llamada {}", empleadoFinal.getNombre(), llamada.getId());
				executorService.submit(() -> atenderLlamadaConEmpleado(empleadoFinal, llamada));
			} else {
				logger.warn("No hay empleados disponibles para atender la llamada {}", llamada.getId());
				colaLlamadas.add(llamada); // Si no hay empleados disponibles, agrega a la cola
			}
		} finally {
			lock.unlock();
		}
	}

	private void procesarLlamadasEnEspera() {
		lock.lock();
		try {
			if (!colaLlamadas.isEmpty()) {
				Llamada llamadaPendiente = colaLlamadas.poll();
				if (llamadaPendiente != null) {
					atenderLlamada(llamadaPendiente);
				}
			}
		} finally {
			lock.unlock();
		}
	}
}
