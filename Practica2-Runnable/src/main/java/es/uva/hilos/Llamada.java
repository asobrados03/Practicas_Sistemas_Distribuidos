package es.uva.hilos;

public class Llamada {
	// Una llamada es simplemente el tiempo que necesitamos para atenderla
	private final int duracion;
	private final int id;

	public Llamada(int duracion, int id){
		this.duracion = duracion;
		this.id = id;
	}

	public int getDuracion(){
		return duracion;
	}

	public int getId(){
		return id;
	}
}
