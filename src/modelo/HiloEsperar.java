package modelo;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

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
    JProgressBar barraProgreso;
    JLabel labelProgreso;
    Controlador controlador = new Controlador();
    ArrayList<Integer> tiemposEjercicio = new ArrayList<>();

    public HiloEsperar(
        HiloRegresivo serie,
        HiloRegresivo descanso,
        HiloCronometro hiloEjercicio,
        JButton botonEmpezar,
        int contEjercicios,
        Workouts workout,
        Ejercicios ejercicioActivo,
        int contSeries,
        ArrayList<Component> labels,
        JPanel panelEjercicio,
        ArrayList<Component> fotos,
        ArrayList<Integer> tiemposEjercicio,
        JProgressBar barraProgreso,
        JLabel labelProgreso
    ) {
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
        this.barraProgreso = barraProgreso;
        this.labelProgreso = labelProgreso;
    }

    @Override
    public void run() {
        try {
            // Inicia serie
            serie.start();
            while (serie.isAlive()) {
                Thread.sleep(100);
            }
            contSeries++;

            // Inicia descanso
            descanso.start();
            while (descanso.isAlive()) {
                Thread.sleep(100);
            }

            // Si quedan series, prepara siguiente serie
            if (ejercicioActivo.getSeries().size() > contSeries) {
                botonEmpezar.setBackground(new Color(0, 128, 0));
                botonEmpezar.setForeground(Color.WHITE);
                botonEmpezar.setText("Siguiente Serie");
            } else {
                // Ejercicio completado
                hiloEjercicio.terminar();
                tiemposEjercicio.add((hiloEjercicio.getMinutos() * 60) + hiloEjercicio.getSegundos());
                hiloEjercicio.getCronometro().setText("Ejercicio Terminado");
                contEjercicios++;

                // Actualización directa (sin SwingUtilities)
                if (barraProgreso != null && labelProgreso != null) {
                    int total = workout.getEjers().size();
                    barraProgreso.setValue(contEjercicios);
                    labelProgreso.setText("Progreso: " + contEjercicios + " / " + total);
                }

                botonEmpezar.setBackground(new Color(0, 128, 0));
                botonEmpezar.setForeground(Color.WHITE);

                if (workout.getEjers().size() > contEjercicios) {
                    botonEmpezar.setText("Siguiente Ejercicio");
                } else {
                    botonEmpezar.setText("Workout Terminado");
                    botonEmpezar.setEnabled(false);
            

                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public int getContEjercicios() {
        return contEjercicios;
    }
}
