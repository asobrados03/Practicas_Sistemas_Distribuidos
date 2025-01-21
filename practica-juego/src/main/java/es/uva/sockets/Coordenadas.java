package es.uva.sockets;

public class Coordenadas {
    private final int x;
    private final int y;

    public Coordenadas(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean dentroDeLimites(int size) {
        return x >= 0 && x < size && y >= 0 && y < size;
    }

    public static Coordenadas generarAleatoria(int size) {
        int x = (int) (Math.random() * size);
        int y = (int) (Math.random() * size);
        return new Coordenadas(x, y);
    }


    public Coordenadas mover(Direccion dir){
        //TODO: Devolver unas coordenadas movidas según direccion

        int nuevaX = this.x;
        int nuevaY = this.y;

        // Modificar las coordenadas según la dirección
        switch (dir) {
            case UP:
                nuevaY = this.y - 1; // Mueve hacia arriba
                break;
            case DOWN:
                nuevaY = this.y + 1; // Mueve hacia abajo
                break;
            case LEFT:
                nuevaX = this.x - 1; // Mueve hacia la izquierda
                break;
            case RIGHT:
                nuevaX = this.x + 1; // Mueve hacia la derecha
                break;
        }

        // Devuelve una nueva instancia de Coordenadas con las nuevas coordenadas
        return new Coordenadas(nuevaX, nuevaY);
    }

    public boolean equals(Coordenadas otras) {
        return (this.x == otras.x) && (this.y == otras.y);
    }
}
