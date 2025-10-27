package modelo;

public class Serie {

	private String nombre;
    private int repeticiones;
    private int duracion;
    private String imagenURL;

    public Serie(String nombre, int repeticiones, int duracion, String imagenURL) {
        this.nombre = nombre;
        this.repeticiones = repeticiones;
        this.duracion = duracion;
        this.imagenURL = imagenURL;
    }

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getRepeticiones() {
		return repeticiones;
	}

	public void setRepeticiones(int repeticiones) {
		this.repeticiones = repeticiones;
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
