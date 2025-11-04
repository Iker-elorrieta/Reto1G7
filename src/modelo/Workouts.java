package modelo;

import java.io.Serializable;
import java.util.List;

public  class  Workouts implements Serializable {
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	String nombre;
	int nivel;
	int numEjers;
	String videoUrl;
	List<Ejercicios> ejers;
	
	 public Workouts(String id, int numEjers, int nivel, String nombre, String videoUrl) {
	        this.id = id;
	        this.nombre = nombre;
	        this.nivel = nivel;
	        this.numEjers = numEjers;
	        this.videoUrl = videoUrl;
	    }


	public Workouts() {
		// TODO Auto-generated constructor stub
	}


	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}

	public int getNumEjers() {
		return numEjers;
	}

	public void setNumEjers(int numEjers) {
		this.numEjers = numEjers;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}


	public List<Ejercicios> getEjers() {
		return ejers;
	}


	public void setEjers(List<Ejercicios> ejers) {
		this.ejers = ejers;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	
	
			}
