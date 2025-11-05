package modelo;

import java.io.Serializable;
import java.util.List;

public class Ejercicios implements Serializable {

	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nombre;
    private String descripcion;
    private int descanso;
    private List<Serie> series;

    public Ejercicios(String nombre, String descripcion, int descanso, List<Serie> series) {
        this.nombre = nombre;
        this.descripcion = descripcion;
     
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
