package modelo;

import java.io.Serializable;
import java.util.Date;



public class Usuario implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String nombre;
	private String apellido;
	private String email;
	private String contraseña;
	private Date fechaNac;
	private int nivelActual;
	private static int cont = 1;

	

	
	public Usuario(String nombre, String apellido, String email, String contraseña, Date fechaNac, int nivelActual) {
		this.nombre = nombre;
		this.apellido = apellido;
		this.email = email;
		this.contraseña = contraseña;
		this.fechaNac = fechaNac;
		this.nivelActual=nivelActual;
		this.id= cont++;
	
	}
	



	public int getNivelActual() {
		return nivelActual;
	}



	public void setNivelActual(int nivelActual) {
		this.nivelActual = nivelActual;
	}





	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}



	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getContraseña() {
		return contraseña;
	}
	public void setContraseña(String contraseña) {
		this.contraseña = contraseña;
	}
	public Date getFechaNac() {
		return fechaNac;
	}
	public void setFechaNac(Date fechaNac) {
		this.fechaNac = fechaNac;
	}
	
	@Override
	public String toString() {
		return "Usuario [nombre=" + nombre + ", apellido=" + apellido + ", email=" + email + ", contraseña="
				+ contraseña + ", fechaNac=" + fechaNac + "]";
	}
	
	
	

}
