package modelo;

import java.util.List;

public class Ejercicios {

	
    private String nombre;
    private String descripcion;
    private int orden;
    private int descanso;
    private List<Serie> series;

    public Ejercicios(String nombre, String descripcion, int orden, int descanso, List<Serie> series) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.orden = orden;
        this.descanso = descanso;
        this.series = series;
    }

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getOrden() {
		return orden;
	}

	public void setOrden(int orden) {
		this.orden = orden;
	}

	public int getDescanso() {
		return descanso;
	}

	public void setDescanso(int descanso) {
		this.descanso = descanso;
	}

	public List<Serie> getSeries() {
		return series;
	}

	public void setSeries(List<Serie> series) {
		this.series = series;
	}
}
