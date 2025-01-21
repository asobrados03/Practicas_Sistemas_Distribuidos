package es.uva.sockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServidorJuego {
    // El juego consiste en encontrar un tesoro
    // en un mapa cuadriculado, cuando un jugador
    // se conecta aparece en un cuadrado aleatorio
    // no ocupado.
    // El _PROTOCOLO_ del cliente, la manera en que
    // se comunica con el servidor es el siguiente
    // MOVE UP|DOWN|LEFT|RIGHT
    // DIG
    // EL Servidor Verifica la validez de los movimientos
    // Los aplica sobre su estado y envía la actualización
    // A todos los jugadores.
    // EL _PROTOCOLO_ con el que el servidor comunica las
    // actualizaciones a los clientes es el siguiente
    // PLAYER JOIN <PLAYER-ID> <X> <Y>
    // MOVE UP|DOWN|LEFT|RIGHT <PLAYER-ID>
    // DIG <PLAYER-ID> <SUCCESS>
    // El delimitador de lo que constituye un mensaje es
    // un caracter de salto de linea

    public final Estado estado;
    public final ServerSocket serverSocket;
    private final List<ManagerCliente> clientes;
    private int jugadorIdCounter = 0; // Contador global para IDs de jugadores

    public ServidorJuego(int size, int puerto) throws IOException {
        estado = new Estado(size);
        clientes = new ArrayList<>();
        // Crear un serverSocket que acepte
        // conexiones de VARIOS clientes

        serverSocket = new ServerSocket(puerto);
    }

    public void iniciar() throws IOException {
        while (!estado.estaTerminado()) {
            ManagerCliente nuevo = aceptarConexion();
            clientes.add(nuevo);
            nuevo.start();
        }

    }

    public ManagerCliente aceptarConexion() throws IOException {
        int nuevoJugadorId = jugadorIdCounter++;

        // Aceptar la conexión del cliente
        Socket socket = serverSocket.accept();

        // Determinar una posición inicial aleatoria para el nuevo jugador
        Coordenadas posicionInicial;
        do {
            posicionInicial = Coordenadas.generarAleatoria(estado.getSize());
        } while (estado.estaOcupada(posicionInicial));

        // Registrar al nuevo jugador en el estado del servidor
        Jugador nuevoJugador = new Jugador(nuevoJugadorId, posicionInicial);
        estado.nuevoJugador(nuevoJugador);

        // Crear un nuevo ManagerCliente para manejar la conexión
        ManagerCliente nuevoCliente = new ManagerCliente(socket, this, nuevoJugadorId);

        // Agregar el cliente a la lista si no está registrado aún
        if (!clientes.contains(nuevoCliente)) {
            clientes.add(nuevoCliente);
        }

        // Informar al nuevo cliente sobre su estado inicial
        nuevoCliente.enviarMensaje("PLAYER JOIN " + nuevoJugadorId + " " +
                posicionInicial.getX() + " " + posicionInicial.getY() + "\n");

        // Informar al cliente sobre los jugadores existentes
        for (Jugador jugador : estado.jugadores) {
            if (jugador.id != nuevoJugadorId) {
                nuevoCliente.enviarMensaje("PLAYER JOIN " + jugador.id + " " +
                        jugador.coordenadas.getX() + " " +
                        jugador.coordenadas.getY() + "\n");
            }
        }

        // Informar a los demás jugadores sobre el nuevo cliente
        broadcast("PLAYER JOIN " + nuevoJugadorId + " " +
                posicionInicial.getX() + " " + posicionInicial.getY());

        return nuevoCliente;
    }

    public synchronized void broadcast(String message) {
        // TODO: Enviar un mensaje a todos los clientes

        for(ManagerCliente cliente: clientes){
            cliente.enviarMensaje(message);
        }
    }

}
