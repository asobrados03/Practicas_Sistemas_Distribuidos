package es.uva.sockets;

import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class ManagerCliente extends Thread {
    // Clase para que el encargado de cada cliente
    // Se ejecute en un hilo diferente

    private final Socket socket;
    private final ServidorJuego servidor;
    private final int idJugador;
    // Se pueden usar mas atributos ...
    private static final java.util.logging.Logger logger = Logger.getLogger(ManagerCliente.class.getName());

    public ManagerCliente(Socket socket, ServidorJuego servidor, int idJugador) {
        this.socket = socket;
        this.servidor = servidor;
        this.idJugador = idJugador;
        // Se pueden usar mas atributos ...
    }

    public void enviarMensaje(String message) {
        // TODO: enviar un mensaje. NOTA: a veces hace falta usar flush.
        try {
            // Supongamos que tienes un PrintWriter como atributo en ManagerCliente
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message); // Envía el mensaje seguido de un salto de línea
            out.flush();
        } catch (IOException e) {
            logger.severe("Error al enviar mensaje al cliente "+ idJugador+": " + e.getMessage());
        }
    }

    @Override
    public void run() {
        // Mantener todos los procesos necesarios hasta el final
        // de la partida (alguien encuentra el tesoro)
        while (!servidor.estado.estaTerminado() && !socket.isClosed()) {
            procesarMensajeCliente();
        }
    }

    public void procesarMensajeCliente() {
        // TODO: leer el mensaje del cliente
        // y procesarlo usando interpretarMensaje
        // Si detectamos el final del socket
        // gestionar desconexion ...

        try {
            // Crear un BufferedReader para leer los mensajes del cliente
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String mensaje = in.readLine();
            // Leer mensaje
            if(!mensaje.isEmpty()){
                interpretarMensaje(mensaje);
            } else {
                logger.info("Mensaje vacío o nulo recibido, no se procesa.");
            }
        } catch (IOException e) {
            logger.severe("Error en la conexión con el cliente "+ idJugador+": " + e.getMessage());
        }
    }

    public void interpretarMensaje(String mensaje) {
        // TODO: Esta función debe realizar distintas
        // Acciones según el mensaje recibido
        // Manipulando el estado del servidor
        // Si el mensaje recibido no tiene el formato correcto
        // No ocurre nada

        String[] partes = mensaje.trim().split(" ");

        if (mensaje.isEmpty()) {
            return;
        }

        // Identificar el comando principal
        String comando = partes[0];

        // Procesar el comando "MOVE"
        if (comando.equals("MOVE")) {
            if (partes.length == 2) {
                String direccion = partes[1];

                // Validar la dirección
                switch (direccion) {
                    case "UP" -> {
                        servidor.estado.mover(idJugador, Direccion.UP);
                        enviarMensaje("MOVE UP "+idJugador);
                        servidor.broadcast("MOVE UP "+idJugador);
                    }
                    case "DOWN" -> {
                        servidor.estado.mover(idJugador, Direccion.DOWN);
                        enviarMensaje("MOVE DOWN "+idJugador);
                        servidor.broadcast("MOVE DOWN "+idJugador);
                    }
                    case "LEFT" -> {
                        servidor.estado.mover(idJugador, Direccion.LEFT);
                        enviarMensaje("MOVE LEFT "+idJugador);
                        servidor.broadcast("MOVE LEFT "+idJugador);
                    }
                    case "RIGHT" -> {
                        servidor.estado.mover(idJugador, Direccion.RIGHT);
                        enviarMensaje("MOVE RIGHT "+idJugador);
                        servidor.broadcast("MOVE RIGHT "+idJugador);
                    }
                }
            }
        } else if (comando.equals("DIG")) {
            if (partes.length == 1) {
                servidor.estado.buscar(idJugador);
                enviarMensaje("DIG "+idJugador+" SUCCESS");
                servidor.broadcast("DIG "+idJugador+" SUCCESS");
            }
        }
        else {
            System.out.println("Comando no válido: " + mensaje);
        }
    }
}