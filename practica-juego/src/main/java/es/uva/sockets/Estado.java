package es.uva.sockets;

import java.util.ArrayList;
import java.util.logging.Logger;

public class Estado {
    public final ArrayList<Jugador> jugadores;
    public Coordenadas tesoro;
    public final ArrayList<Coordenadas> buscadas;
    private final int size;
    private boolean terminado;

    public Estado(int size) {
        this.size = size;
        terminado = false;
        this.jugadores = new ArrayList<>();
        this.buscadas = new ArrayList<>();
        // TODO: Coordenadas aleatorias para el tesoro
        this.tesoro = new Coordenadas(0, 0);
    }

    public int getSize() {
        return size;
    }

    public synchronized boolean estaOcupada(Coordenadas coordenadas) {
        for (Jugador jugador : jugadores) {
            if (jugador.coordenadas.equals(coordenadas)) {
                return true;
            }
        }
        return false;
    }


    // Los métodos que modifican el estado son synchronized,
    // Esto quiere decir que un hilo debe esperar a que otro
    // hilo acabe de utilizarlo
    // Lo necesitamos ya que vamos a gestionar cada conexión
    // con un cliente en un hilo deistinto
    public synchronized void terminar(){
        terminado = true;
    }

    public synchronized boolean estaTerminado(){
        return terminado;
    }


    public synchronized void nuevoJugador(Jugador jugador) {
        final Logger logger = Logger.getLogger(Estado.class.getName());

        jugadores.add(jugador);
        logger.info("Estado actual: " + jugadores);
    }

    public synchronized void buscar(int id) {
        // TODO busca el tesoro el jugador con este id
        // Si se encuentra finaliza el juego

        for(Jugador jugador: jugadores){
            if(jugador.id == id){
                if(tesoro.equals(jugador.coordenadas)){
                    terminar();
                } else {
                    if(!buscadas.contains(jugador.coordenadas)){
                        buscadas.add(jugador.coordenadas);
                    }
                }

                break;
            }
        }
    }

    public synchronized void mover(int id, Direccion dir) {
        // TODO mueve a el jugador id en la direccion dir

        for(Jugador jugador: jugadores){
            if(jugador.id == id){
                Coordenadas nuevasCoordenadas = jugador.coordenadas.mover(dir);
                if(nuevasCoordenadas.dentroDeLimites(size)) {
                    boolean hayColision = false;
                    for (Jugador otroJugador : jugadores) {
                        if (otroJugador.id != id && otroJugador.coordenadas.equals(nuevasCoordenadas)) {
                            hayColision = true;
                            break;
                        }
                    }

                    // Actualiza si es válido
                    if (!hayColision) {
                        jugador.coordenadas = nuevasCoordenadas;
                    }
                }
                break;
            }
        }
    }

    public void mostrar() {
        // Limpiar pantalla
        System.out.print("\033[H\033[2J");
        System.out.flush();
        // Linea horizontal
        for (int col = 0; col < size; col++) {
            System.out.print("---");
        }
        System.out.println("-");
        for (int row = 0; row < size; row++) {
            // Columnas
            for (int col = 0; col < size; col++) {
                System.out.print("| "); // Cada celda representada por "| |"
                // Si no hay nada imprimimos vacio
                char toPrint = ' ';
                // Si ya esta cavado imprimos una x
                for (Coordenadas coordenadas : buscadas) {
                    if (coordenadas.equals(new Coordenadas(col, row))) {
                        toPrint = 'x';
                    }
                }
                // Si hay un jugador imprimimos su representacion
                for (Jugador jugador : jugadores) {
                    if (jugador.coordenadas.equals(new Coordenadas(col, row))) {
                        toPrint = jugador.getChar();
                    }
                }
                System.out.print(toPrint);
            }
            System.out.println("|"); // End of row with a final column border

            // Print a row separator
            for (int col = 0; col < size; col++) {
                System.out.print("---");
            }
            System.out.println("-");
        }
    }
}
