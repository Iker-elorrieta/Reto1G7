package modelo;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

import controlador.Controlador;

public class HiloEsperar extends Thread {
	HiloRegresivo serie, descanso;
	HiloCronometro hiloEjercicio;
	JButton botonEmpezar;
	int contEjercicios, contSeries;
	Workouts workout;
	Ejercicios ejercicioActivo;
	ArrayList<Component> labels, fotos;
	JPanel panelEjercicio;
	Controlador controlador = new Controlador();
	ArrayList<Integer> tiemposEjercicio = new ArrayList<Integer>();

	public void run() {
		serie.start();
		while (serie.isAlive()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		contSeries++;

		descanso.start();
		while (descanso.isAlive()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (ejercicioActivo.getSeries().size() > contSeries) {
			botonEmpezar.setBackground(new Color(0, 128, 0));
			botonEmpezar.setForeground(Color.WHITE);
			botonEmpezar.setText("Siguiente Serie");
		} else {
			hiloEjercicio.terminar();
			tiemposEjercicio.add((hiloEjercicio.getMinutos() * 60) + hiloEjercicio.getSegundos());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			hiloEjercicio.getCronometro()
					.setText(hiloEjercicio.getCronometro().getText().split(":")[0] + ": Ejercicio Terminado");
			contEjercicios++;
			botonEmpezar.setBackground(new Color(0, 128, 0));
			botonEmpezar.setForeground(Color.WHITE);
			if (workout.getEjers().size() > contEjercicios) {
				botonEmpezar.setText("Siguiente Ejercicio");

			} else {
				botonEmpezar.setText("Workout Terminado");
				botonEmpezar.setEnabled(false);
			}
		}

	}

	
	public HiloEsperar(HiloRegresivo serie, HiloRegresivo descanso, HiloCronometro hiloEjercicio, JButton botonEmpezar,
			int contEjercicios, Workouts workout, Ejercicios ejercicioActivo, int contSeries, ArrayList<Component> labels,
			JPanel panelEjercicio, ArrayList<Component> fotos, ArrayList<Integer> tiemposEjercicio) {

		this.serie = serie;
		this.descanso = descanso;
		this.hiloEjercicio = hiloEjercicio;
		this.botonEmpezar = botonEmpezar;
		this.contEjercicios = contEjercicios;
		this.workout = workout;
		this.ejercicioActivo = ejercicioActivo;
		this.contSeries = contSeries;
		this.labels = labels;
		this.panelEjercicio = panelEjercicio;
		this.fotos = fotos;
		this.tiemposEjercicio = tiemposEjercicio;
	}

}
