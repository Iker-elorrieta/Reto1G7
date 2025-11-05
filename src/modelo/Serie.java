package modelo;

import java.io.Serializable;

public class Serie implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nombre;
    private int duracion;
    private String imagenURL;

    public Serie(String nombre, int duracion, String imagenURL)
 {
        this.nombre = nombre;

        this.duracion = duracion;
        this.imagenURL = imagenURL;
    }

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}



	public int getDuracion() {
		return duracion;
	}

	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}

	public String getImagenURL() {
		return imagenURL;
	}

	public void setImagenURL(String imagenURL) {
		this.imagenURL = imagenURL;
	}
    
}
