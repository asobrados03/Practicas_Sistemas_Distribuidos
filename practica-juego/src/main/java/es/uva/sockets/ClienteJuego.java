package es.uva.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class ClienteJuego {
    // La clase cliente tiene las siguientes responsabilidades
    // Unirse al juego conectandose al servidor
    // Mantener un estado de juego actualizado interpretando los
    // mensajes del servidor (y mostrar el estado)
    // Convertir input del jugador en un mensaje que enviar al servidor
    // NOTA: para simplificar el manejo de input podemos considerar
    // que el usario manda cada comando en una linea distinta
    // (aunque sea muy incomodo)

    public final Estado estado;
    // TODO: Faltarán atributos ...
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private static final java.util.logging.Logger logger = Logger.getLogger(ClienteJuego.class.getName());

    public ClienteJuego(int size) {
        // [OPCIONAL] TODO: Extiende el protocolo de comunicacion para
        // que el servidor envie el tamaño del mapa tras la conexion
        // de manera que el estado no se instancie hasta entonces
        // y conocer este parametro a priori no sea necesario.
        estado = new Estado(size);
    }

    public void iniciar(String host, int puerto) throws InterruptedException {
        // Metodo que reune todo y mantiene lo necesario en un bucle
        conectar(host, puerto);
        Thread procesadorMensajesServidor = new Thread(() -> {
            while (!estado.estaTerminado()) {
                procesarMensajeServidor();
            }
        });
        Thread procesadorInput = new Thread(() -> {
            while (!estado.estaTerminado()) {
                procesarInput();
            }
        });
        procesadorMensajesServidor.start();
        procesadorInput.start();
        procesadorInput.join();
        procesadorMensajesServidor.join();
        // Si acaban los hilos es que el juego terminó
        cerrarConexion();
    }

    public void cerrarConexion() {
        // TODO: cierra todos los recursos asociados a la conexion con el servidor
        try {
            if(in != null) in.close();
            if(out != null) out.close();
            if(socket != null) socket.close();
        } catch (IOException e) {
            logger.severe("Error al cerrar la conexión del servidor: " + e.getMessage());
        }

    }

    public void conectar(String host, int puerto) {
        // TODO: iniciar la conexion con el servidor
        // (Debe guardar la conexion en un atributo)
        try {
            socket = new Socket(host, puerto);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            logger.severe("Error al conectar al servidor: " + e.getMessage());
        }
    }

    public void procesarInput() {
        // TODO: Comprueba la entrada estandar y
        // se procesa mediante intrepretar input,
        // Se genera un mensaje que se envia al servidor
        try{
            int tecla = System.in.read();
            String comando = interpretarInput((char) tecla);
            if(!comando.isEmpty()) {
                out.println(comando); // Enviar el comando al servidor
                out.flush(); // Asegurarse de que el comando se transmita
                logger.fine("Comando enviado al servidor: " + comando);
            }
        } catch (IOException e) {
            logger.severe("Error al leer la entrada de usuario: " + e.getMessage());
        }
    }

    public void procesarMensajeServidor() {
        // TODO: Comprueba la conexion y obtiene un mensaje
        // que se procesa con interpretarMensaje
        // Al recibir la actualizacion del servidor podeis
        // Usar el metodo mostrar del estado
        // Para enseñarlo

        final long TIMEOUT = 5000; // Tiempo máximo de inactividad en milisegundos
        long lastMessageTime = System.currentTimeMillis();

        try {
            String mensaje;
            while(true){
                if (in.ready()){
                    mensaje = in.readLine();
                    if (!mensaje.isEmpty()) {
                        logger.fine("Mensaje recibido en cliente: " + mensaje);
                        interpretarMensaje(mensaje); // Procesa el mensaje
                        estado.mostrar(); // Actualiza y muestra el estado
                        lastMessageTime = System.currentTimeMillis(); // Actualiza la marca de tiempo
                    } else {
                        logger.fine("Mensaje vacío o nulo recibido, no se procesa.");
                    }
                }

                // Salir si el tiempo de inactividad supera el límite
                if (System.currentTimeMillis() - lastMessageTime > TIMEOUT) {
                    logger.fine("Tiempo de inactividad excedido, cerrando conexión...");
                    break;
                }
            }
            estado.terminar();
        } catch (IOException e) {
            logger.severe("Error al recibir mensajes del servidor: " + e.getMessage());
            estado.terminar();
        }
    }

    public String interpretarInput(char tecla) {
        // TODO: WASD para moverse, Q para buscar
        // Este metodo debe devolver el comando necesario
        // Que enviar al servidor

        switch (tecla){
            case 'W' -> {
                return "MOVE UP";
            }
            case 'S' -> {
                return "MOVE DOWN";
            }
            case 'A' -> {
                return "MOVE LEFT";
            }
            case 'D' -> {
                return "MOVE RIGHT";
            }
            case 'Q' -> {
                return "DIG";
            }
            default -> {
                return "";
            }
        }
    }

    public void interpretarMensaje(String mensaje) {
        // TODO: interpretar los mensajes del servidor actualizando el estado
        try {
            String[] mensajeDividido = mensaje.split(" ");

            if (mensajeDividido.length < 2) {
                logger.severe("Mensaje inválido recibido: " + mensaje);
                return; // Si no tiene al menos 2 partes, no es válido
            }

            String tipoMensaje = mensajeDividido[0];

            switch (tipoMensaje) {
                case "PLAYER" -> {
                    if (mensajeDividido.length < 5) { // Validar formato de mensaje
                        logger.severe("Mensaje PLAYER JOIN inválido: " + mensaje);
                        return;
                    }

                    int playerID = Integer.parseInt(mensajeDividido[2]);
                    int x = Integer.parseInt(mensajeDividido[3]);
                    int y = Integer.parseInt(mensajeDividido[4]);

                    Coordenadas coordenadas = new Coordenadas(x, y);
                    Jugador jugador = new Jugador(playerID, coordenadas);

                    // Verificar si el jugador ya está en la lista
                    boolean jugadorExistente = false;
                    for (Jugador jugadorLocal : estado.jugadores) {
                        if (jugadorLocal.id == playerID) {
                            jugadorExistente = true;
                            break;
                        }
                    }

                    if (!jugadorExistente){
                        estado.nuevoJugador(jugador); //igual
                        logger.fine("Jugador añadido: " + jugador);
                        logger.info("Estado actual después de añadir: " + estado.jugadores);
                    } else{
                        logger.fine("Jugador ya existe: " + jugador);
                    }

                }
                case "MOVE" -> {
                    if (mensajeDividido.length < 3) {
                        logger.fine("Mensaje MOVE inválido: " + mensaje);
                        return;
                    }

                    String direction = mensajeDividido[1];
                    int playerID = Integer.parseInt(mensajeDividido[2]);

                    try {
                        Direccion dir = Direccion.valueOf(direction);
                        estado.mover(playerID, dir);
                    } catch (IllegalArgumentException e) {
                        logger.severe("Dirección inválida en MOVE: " + direction);
                    }
                }
                case "DIG" -> {
                    if (mensajeDividido.length < 3) {
                        logger.fine("Mensaje DIG inválido: " + mensaje);
                        return;
                    }

                    int playerID = Integer.parseInt(mensajeDividido[1]);
                    String success = mensajeDividido[2];

                    if (success.equals("SUCCESS")) {
                        estado.buscar(playerID);
                    } else {
                        logger.severe("Resultado de DIG no reconocido: " + success);
                    }
                }
                default -> logger.severe("Tipo de mensaje no reconocido: " + tipoMensaje);
            }
        } catch (NumberFormatException e) {
            logger.severe("Error al interpretar un número en el mensaje: " + mensaje + " - " + e.getMessage());
        } catch (Exception e) {
            logger.severe("Error inesperado al interpretar el mensaje: " + mensaje + "-" + e.getMessage());
        }
    }

}
