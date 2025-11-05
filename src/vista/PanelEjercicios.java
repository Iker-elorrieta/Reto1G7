package vista;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.google.cloud.firestore.DocumentSnapshot;

import controlador.Controlador;
import modelo.Ejercicios;
import modelo.HiloCronometro;
import modelo.HiloEsperar;
import modelo.HiloRegresivo;
import modelo.Workouts;

public class PanelEjercicios extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    /*******************************
     * BLOQUE: Variables principales de la interfaz
     *******************************/
    private JLabel lblCronoPrincipal;
    private HiloCronometro hiloCronoPrincipal;

    private JPanel panelCentral;
    private DefaultTableModel modeloTabla;
    private JTable tablaEjers;

    private JButton btnIniciar;
    private JButton btnSalir;

    /*******************************
     * BLOQUE: Datos del workout y control de estado
     *******************************/
    private Workouts workoutSeleccionado;
    private List<Ejercicios> ejercicios;
    private int indiceEjercicio = 0; // índice del ejercicio actual
    private int contSeriesActual = 0; // serie actual dentro del ejercicio
    private ArrayList<Integer> tiemposPorEjercicio = new ArrayList<>();
    private int ejerciciosCompletados = 0;
    private DocumentSnapshot usuario; //
    private ArrayList<Component> fotos;

    /*******************************
     * BLOQUE: Hilos activos (series, descansos y cronómetros)
     *******************************/
    private HiloRegresivo hiloSerie;
    private HiloRegresivo hiloDescanso;
    private HiloCronometro hiloEjercicio;

    /*******************************
     * BLOQUE: Labels dinámicos (nombre, reps, imagen, cronómetro)
     *******************************/
    private JLabel lblNombreEjercicio;
    private JLabel lblRepeticiones;
    private JLabel lblFotoEjercicio;
    private JLabel lblCronoRegresivo;
    private JLabel labelProgreso;
    private JProgressBar barraProgreso;
    

    private Controlador controlador = new Controlador();

    /*******************************
     * BLOQUE: Constructor principal
     *******************************/
    public PanelEjercicios(DocumentSnapshot usu, Workouts workoutSeleccionado) {
    	this.usuario=usu;
        this.workoutSeleccionado = workoutSeleccionado;
        this.ejercicios = workoutSeleccionado.getEjers();
        initialize();
    }

    /*******************************
     * BLOQUE: Inicialización de la interfaz
     *******************************/
    private void initialize() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1221, 741);
        contentPane = new JPanel();
        contentPane.setBackground(Color.DARK_GRAY);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        
        
        /***** Subbloque: Barra de progreso *****/
        barraProgreso = new JProgressBar(0, ejercicios.size());
        barraProgreso.setValue(0);
        barraProgreso.setStringPainted(true);
        barraProgreso.setBounds(670, 620, 400, 50);
        contentPane.add(barraProgreso);


        /***** Subbloque: Cronómetro principal *****/
        lblCronoPrincipal = new JLabel("Total: 00:00");
        lblCronoPrincipal.setForeground(Color.WHITE);
        lblCronoPrincipal.setFont(new Font("Tahoma", Font.BOLD, 24));
        lblCronoPrincipal.setBounds(20, 20, 240, 40);
        contentPane.add(lblCronoPrincipal);
        hiloCronoPrincipal = new HiloCronometro(lblCronoPrincipal);

        /***** Subbloque: Nombre del workout *****/
        String nombreWork = workoutSeleccionado.getNombre();
        JLabel lblWorkout = new JLabel("Workout: " + nombreWork);
        lblWorkout.setForeground(Color.WHITE);
        lblWorkout.setFont(new Font("Tahoma", Font.PLAIN, 18));
        lblWorkout.setBounds(950, 20, 250, 30);
        contentPane.add(lblWorkout);

        /***** Subbloque: Panel central con info del ejercicio actual *****/
        panelCentral = new JPanel();
        panelCentral.setBackground(Color.GRAY);
        panelCentral.setBounds(300, 70, 850, 530);
        panelCentral.setLayout(null);
        contentPane.add(panelCentral);

        lblNombreEjercicio = new JLabel("");
        lblNombreEjercicio.setForeground(Color.WHITE);
        lblNombreEjercicio.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblNombreEjercicio.setBounds(20, 10, 600, 30);
        panelCentral.add(lblNombreEjercicio);

        lblRepeticiones = new JLabel("");
        lblRepeticiones.setForeground(Color.WHITE);
        lblRepeticiones.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblRepeticiones.setBounds(20, 50, 300, 25);
        panelCentral.add(lblRepeticiones);

        lblFotoEjercicio = new JLabel();
        lblFotoEjercicio.setOpaque(true);
        lblFotoEjercicio.setBackground(Color.LIGHT_GRAY);
        lblFotoEjercicio.setBounds(20, 90, 300, 250);
        lblFotoEjercicio.setHorizontalAlignment(SwingConstants.CENTER);
        lblFotoEjercicio.setText("Foto");
        panelCentral.add(lblFotoEjercicio);

        lblCronoRegresivo = new JLabel("Serie: 00:00");
        lblCronoRegresivo.setForeground(Color.WHITE);
        lblCronoRegresivo.setFont(new Font("Tahoma", Font.BOLD, 28));
        lblCronoRegresivo.setBounds(350, 200, 300, 50);
        panelCentral.add(lblCronoRegresivo);

        /***** Subbloque: Tabla de ejercicios (vista global) *****/
        String[] columnas = { "Nombre", "Repeticiones", "Imagen" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaEjers = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaEjers);
        scroll.setBounds(350, 20, 480, 150);
        panelCentral.add(scroll);
        cargarTablaSeries();

        /***** Subbloque: Botones inferiores *****/
        btnIniciar = new JButton("Iniciar");
        btnIniciar.setBackground(new Color(0, 128, 0));
        btnIniciar.setForeground(Color.WHITE);
        btnIniciar.setBounds(300, 620, 200, 50);
        contentPane.add(btnIniciar);

        btnSalir = new JButton("Salir");
        btnSalir.setBackground(new Color(128, 0, 0));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setBounds(520, 620, 120, 50);
        contentPane.add(btnSalir);

        /***** Subbloque: Listeners de botones *****/
        /***** Subbloque: Listeners de botones *****/
        btnIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manejarBotonIniciar(); // Llama al método que maneja los estados del botón
            }
        });

        btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarResumen(true); // Llama al método para mostrar el resumen y cerrar
            }
        });

        /***** Subbloque: Mostrar primer ejercicio *****/
        if (!ejercicios.isEmpty()) {
            mostrarEjercicio(indiceEjercicio);
        } else {
            lblNombreEjercicio.setText("No hay ejercicios en este workout");
            btnIniciar.setEnabled(false);
        }
    }

    /*******************************
     * BLOQUE: Cargar tabla con los ejercicios
     *******************************/
    private void cargarTablaSeries() {
        modeloTabla.setRowCount(0);
        for (Ejercicios ej : ejercicios) {
            String reps = (ej.getSeries() != null ? String.valueOf(ej.getSeries().size()) + " series" : "0");
            modeloTabla.addRow(new Object[] { ej.getNombre(), reps, "img" });
        }
    }

    /*******************************
     * BLOQUE: Mostrar ejercicio actual en el panel central
     *******************************/
    private void mostrarEjercicio(int idx) {
        if (idx < 0 || idx >= ejercicios.size()) return;
        Ejercicios ej = ejercicios.get(idx);
        lblNombreEjercicio.setText(ej.getNombre());
        lblRepeticiones.setText(ej.getDescripcion() != null ? ej.getDescripcion() + " — " + ej.getSeries().size() + " series" : "");
        lblFotoEjercicio.setText("Foto: " + ej.getNombre());
        contSeriesActual = 0;
        hiloEjercicio = new HiloCronometro(new JLabel("")); // no visible
    }
    
    /*******************************
     * BLOQUE: Cargar siguiente ejercicio
     *******************************/
    private void cargarSiguienteEjercicio() {
        indiceEjercicio++;
        if (indiceEjercicio < ejercicios.size()) {
            mostrarEjercicio(indiceEjercicio);
            btnIniciar.setText("Iniciar");
            btnIniciar.setBackground(new Color(0, 128, 0));
            btnIniciar.setForeground(Color.WHITE);
        } else {
            barraProgreso.setValue(ejercicios.size());
            subirNivelUsuario();
            mostrarResumen(false);
        }
    }
    /*******************************
     * BLOQUE: Lógica del botón Iniciar / Pausar / Reanudar / Siguiente
     *******************************/
    private synchronized void manejarBotonIniciar() {
        String texto = btnIniciar.getText();

        // ---- Iniciar cronómetro principal si no está activo ----
        if (!hiloCronoPrincipal.isAlive() && 
            ("Iniciar".equals(texto) || "Siguiente Serie".equals(texto) || "Siguiente Ejercicio".equals(texto))) {
            hiloCronoPrincipal = new HiloCronometro(lblCronoPrincipal);
            hiloCronoPrincipal.start();
        }

        /***** Caso 1: Iniciar / siguiente serie o ejercicio *****/
        if ("Iniciar".equals(texto) || "Siguiente Serie".equals(texto) || "Siguiente Ejercicio".equals(texto)) {
            btnIniciar.setText("Pausar");
            btnIniciar.setBackground(Color.ORANGE);
            btnIniciar.setForeground(Color.BLACK);

            if (hiloEjercicio == null || !hiloEjercicio.isAlive()) {
                hiloEjercicio = new HiloCronometro(new JLabel("Ejercicio: 00:00"));
                hiloEjercicio.start();
            }

            if (indiceEjercicio < ejercicios.size()) {
                Ejercicios ejercicioActual = ejercicios.get(indiceEjercicio);

                if (ejercicioActual.getSeries() != null && contSeriesActual < ejercicioActual.getSeries().size()) {

                    // --- 5 segundos de pre-cuenta ---
                    lblCronoRegresivo.setText("Preparación: 0:05");
                    final HiloRegresivo preCuenta = new HiloRegresivo(lblCronoRegresivo, 5);
                    preCuenta.start();

                    // --- Esperar preCuenta y lanzar serie ---
                    Thread hiloInicioSerie = new Thread(() -> {
                        try {
                            while (preCuenta.isAlive()) Thread.sleep(50);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                        int segundosSerie = 0;
                        int tiempoDescanso = 0;
                        try {
                            // 🔧 Posible corrección: probablemente aquí faltaba duración de la SERIE, no del descanso
                        	segundosSerie = ejercicioActual.getSeries().get(contSeriesActual).getDuracion();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        hiloSerie = new HiloRegresivo(lblCronoRegresivo, segundosSerie);
                        hiloDescanso = new HiloRegresivo(new JLabel("Descanso: 0:00"), tiempoDescanso);

                        HiloEsperar hiloEsperar = new HiloEsperar(
                        	    hiloSerie,
                        	    hiloDescanso,
                        	    hiloEjercicio,
                        	    btnIniciar,
                        	    ejerciciosCompletados,
                        	    workoutSeleccionado,
                        	    ejercicioActual,
                        	    contSeriesActual,
                        	    null,                 // 🔹 labels (no lo usas)
                        	    panelCentral,
                        	    fotos,
                        	    tiemposPorEjercicio,
                        	    barraProgreso,        // 🔹 referencia a la barra
                        	    labelProgreso         // 🔹 referencia al texto de progreso
                        	);

                        hiloEsperar.start();
                    });
                    hiloInicioSerie.start();

                    
                    
                } else {
                    btnIniciar.setText("Siguiente Ejercicio");
                    btnIniciar.setBackground(new Color(0, 128, 0));
                    btnIniciar.setForeground(Color.WHITE);
                    cargarSiguienteEjercicio();
                }

            } else {
                btnIniciar.setText("Workout Terminado");
                btnIniciar.setEnabled(false);
            }
        }

        /***** Caso 2: Pausar *****/
        else if ("Pausar".equals(texto)) {
            btnIniciar.setText("Reanudar");
            btnIniciar.setBackground(new Color(70, 130, 180));
            btnIniciar.setForeground(Color.WHITE);

            if (hiloCronoPrincipal != null) hiloCronoPrincipal.cambiarEstado();
            if (hiloEjercicio != null) hiloEjercicio.cambiarEstado();
            if (hiloSerie != null) hiloSerie.cambiarEstado();
            if (hiloDescanso != null) hiloDescanso.cambiarEstado();
        }

        /***** Caso 3: Reanudar *****/
        else if ("Reanudar".equals(texto)) {
            btnIniciar.setText("Pausar");
            btnIniciar.setBackground(Color.ORANGE);
            btnIniciar.setForeground(Color.BLACK);

            if (hiloCronoPrincipal != null) hiloCronoPrincipal.cambiarEstado();
            if (hiloEjercicio != null) hiloEjercicio.cambiarEstado();
            if (hiloSerie != null) hiloSerie.cambiarEstado();
            if (hiloDescanso != null) hiloDescanso.cambiarEstado();
        }

        /***** Caso 4: Siguiente ejercicio *****/
        else if ("Siguiente Ejercicio".equals(texto)) {
            ejerciciosCompletados++;
            barraProgreso.setValue(ejerciciosCompletados);

            indiceEjercicio++;
            if (indiceEjercicio < ejercicios.size()) {
                mostrarEjercicio(indiceEjercicio);
                btnIniciar.setText("Iniciar");
                btnIniciar.setBackground(new Color(0, 128, 0));
                btnIniciar.setForeground(Color.WHITE);
            } else {
                // Workout completado
                barraProgreso.setValue(ejercicios.size());
                 subirNivelUsuario(); // ⬅️ actualiza el nivel
                mostrarResumen(false);
            }
        }


        /***** Caso 5: Workout terminado *****/
        else if ("Workout Terminado".equals(texto)) {
            mostrarResumen(false);
        }
    }

    private void subirNivelUsuario() {
		// TODO Auto-generated method stub
    
    	    try {
    	        int nivelActual = usuario.getLong("nivel").intValue();
    	        int nuevoNivel = nivelActual + 1;

    	        controlador.actualizarNivelUsuario(usuario.getId(), nuevoNivel);

    	        JOptionPane.showMessageDialog(
    	            this,
    	            "¡Nivel completado! Has subido al nivel " + nuevoNivel + " 🎉",
    	            "Nivel aumentado",
    	            JOptionPane.INFORMATION_MESSAGE
    	        );

    	    } catch (Exception e) {
    	        e.printStackTrace();
    	        JOptionPane.showMessageDialog(
    	            this,
    	            "Error al actualizar el nivel del usuario.",
    	            "Error",
    	            JOptionPane.ERROR_MESSAGE
    	        );
    	    }
    	}


    /*******************************
     * BLOQUE: Mostrar resumen final
     *******************************/
    private void mostrarResumen(boolean esSalida) {
        try {
            if (hiloCronoPrincipal != null) hiloCronoPrincipal.terminar();
            if (hiloEjercicio != null) hiloEjercicio.terminar();
            if (hiloSerie != null) hiloSerie.terminar();
            if (hiloDescanso != null) hiloDescanso.terminar();
        } catch (Exception e) { /* ignore */ }

        String tiempoTotal = lblCronoPrincipal.getText();
        int realizados = Math.min(indiceEjercicio + (contSeriesActual > 0 ? 1 : 0), ejercicios.size());
        int total = ejercicios.size();
        int porcentaje = total == 0 ? 100 : (realizados * 100 / total);

        String mensaje;
        if (porcentaje == 100 && !esSalida) mensaje = "¡Enhorabuena! Has completado el workout 💪";
        else if (porcentaje >= 75) mensaje = "¡Muy bien! Estás cerca del objetivo.";
        else if (porcentaje >= 40) mensaje = "Buen trabajo — sigue así.";
        else mensaje = "¡A por ello! Cada minuto cuenta.";

        String resumen = String.format(
            tiempoTotal, realizados, total, porcentaje, mensaje
        );

        int opcion = JOptionPane.showOptionDialog(
            this, resumen, "Resumen workout",
            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
            null, new String[] { "Confirmar" }, "Confirmar"
        );

        if (opcion == 0) this.dispose();
    }
}
