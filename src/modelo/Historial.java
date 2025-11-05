package modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Historial implements Serializable {
	
	

	    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		private String workoutId;
	    private String nombreWorkout;
	    private int nivel;
	    private String nivelTexto;
	    private LocalDate fecha;
	    private int tiempoTotal; // en segundos
	    private int porcentajeCompletado;
	    
		public Historial(String workoutId, String nombreWorkout, int nivel, String nivelTexto, LocalDate fecha,
				int tiempoTotal, int porcentajeCompletado) {
			super();
			this.workoutId = workoutId;
			this.nombreWorkout = nombreWorkout;
			this.nivel = nivel;
			this.nivelTexto = nivelATexto(nivel);
			this.fecha = fecha;
			this.tiempoTotal = tiempoTotal;
			this.porcentajeCompletado = porcentajeCompletado;
		};
		
		
		
		private String nivelATexto(int nivel) {
	        switch (nivel) {
	            case 0: return "Principiante";
	            case 1: return "Intermedio";
	            case 2: return "Avanzado";
	            default: return "Desconocido";
	        }
	

}



		public String getWorkoutId() {
			return workoutId;
		}



		public void setWorkoutId(String workoutId) {
			this.workoutId = workoutId;
		}



		public String getNombreWorkout() {
			return nombreWorkout;
		}



		public void setNombreWorkout(String nombreWorkout) {
			this.nombreWorkout = nombreWorkout;
		}



		public int getNivel() {
			return nivel;
		}



		public void setNivel(int nivel) {
			this.nivel = nivel;
		}



		public String getNivelTexto() {
			return nivelTexto;
		}



		public void setNivelTexto(String nivelTexto) {
			this.nivelTexto = nivelTexto;
		}



		public LocalDate getFecha() {
			return fecha;
		}



		public void setFecha(LocalDate fecha) {
			this.fecha = fecha;
		}



		public int getTiempoTotal() {
			return tiempoTotal;
		}



		public void setTiempoTotal(int tiempoTotal) {
			this.tiempoTotal = tiempoTotal;
		}



		public int getPorcentajeCompletado() {
			return porcentajeCompletado;
		}



		public void setPorcentajeCompletado(int porcentajeCompletado) {
			this.porcentajeCompletado = porcentajeCompletado;
		}
		











}
