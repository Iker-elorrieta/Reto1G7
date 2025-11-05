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

    // Left: cronómetro principal
    private JLabel lblCronoPrincipal;
    private HiloCronometro hiloCronoPrincipal;

    // Center: panel ejercicio + lista series
    private JPanel panelCentral;
    private DefaultTableModel modeloTabla;
    private JTable tablaEjers;

    // Bottom: control
    private JButton btnIniciar;
    private JButton btnSalir;

    // Datos del workout
    private Workouts workoutSeleccionado;
    private List<Ejercicios> ejercicios;
    private int indiceEjercicio = 0; // índice del ejercicio actual
    private int contSeriesActual = 0; // serie actual dentro del ejercicio
    private ArrayList<Integer> tiemposPorEjercicio = new ArrayList<>();

    // Hilos activos por ejercicio/serie
    private HiloRegresivo hiloSerie;
    private HiloRegresivo hiloDescanso;
    private HiloCronometro hiloEjercicio;

    // Labels dinámicos para la serie activa (nombre, repeticiones, foto, cronometro regresivo)
    private JLabel lblNombreEjercicio;
    private JLabel lblRepeticiones;
    private JLabel lblFotoEjercicio;
    private JLabel lblCronoRegresivo; // muestra 5s de pre-cuenta y luego tiempo de serie

    // Controlador auxiliar (ya lo usabas)
    private Controlador controlador = new Controlador();

    public PanelEjercicios(DocumentSnapshot usu, Workouts workoutSeleccionado) {
        this.workoutSeleccionado = workoutSeleccionado;
        this.ejercicios = workoutSeleccionado.getEjers();
        initialize();
    }

    private void initialize() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1221, 741);
        contentPane = new JPanel();
        contentPane.setBackground(Color.DARK_GRAY);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // --------- Cronómetro principal (izquierda) ----------
        lblCronoPrincipal = new JLabel("Total: 00:00");
        lblCronoPrincipal.setForeground(Color.WHITE);
        lblCronoPrincipal.setFont(new Font("Tahoma", Font.BOLD, 24));
        lblCronoPrincipal.setBounds(20, 20, 240, 40);
        contentPane.add(lblCronoPrincipal);

        // No instanciamos hilo todavía; se creará la primera vez que se pulse iniciar
        hiloCronoPrincipal = new HiloCronometro(lblCronoPrincipal);

        // --------- Nombre del workout (arriba derecha) ----------
        String nombreWork = workoutSeleccionado.getNombre();
        JLabel lblWorkout = new JLabel("Workout: " + nombreWork);
        lblWorkout.setForeground(Color.WHITE);
        lblWorkout.setFont(new Font("Tahoma", Font.PLAIN, 18));
        lblWorkout.setBounds(950, 20, 250, 30);
        contentPane.add(lblWorkout);

        // --------- Panel central con info del ejercicio actual ----------
        panelCentral = new JPanel();
        panelCentral.setBackground(Color.GRAY);
        panelCentral.setBounds(300, 70, 850, 530);
        panelCentral.setLayout(null);
        contentPane.add(panelCentral);

        // Labels que muestran la serie actual
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

        // Cronómetro regresivo visible en el centro para la serie / descanso
        lblCronoRegresivo = new JLabel("Serie: 00:00");
        lblCronoRegresivo.setForeground(Color.WHITE);
        lblCronoRegresivo.setFont(new Font("Tahoma", Font.BOLD, 28));
        lblCronoRegresivo.setBounds(350, 200, 300, 50);
        panelCentral.add(lblCronoRegresivo);

        // --------- Tabla con todas las series (derecha dentro del central) ----------
        String[] columnas = { "Nombre", "Repeticiones", "Imagen" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaEjers = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaEjers);
        scroll.setBounds(350, 20, 480, 150);
        panelCentral.add(scroll);

        // Rellenar tabla con todos los ejercicios/series (vista global)
        cargarTablaSeries();

        // --------- Botones inferiores ----------
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

        // Acción boton iniciar/pausar/siguiente
        btnIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manejarBotonIniciar();
            }
        });

        // Acción boton salir -> summary
        btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarResumen(true); // true -> salimos antes de terminar
            }
        });

        // Mostrar primer ejercicio si existe
        if (!ejercicios.isEmpty()) {
            mostrarEjercicio(indiceEjercicio);
        } else {
            lblNombreEjercicio.setText("No hay ejercicios en este workout");
            btnIniciar.setEnabled(false);
        }
    }

    // Carga la tabla con la lista completa de ejercicios (nombre, repeticiones, placeholder imagen)
    private void cargarTablaSeries() {
        modeloTabla.setRowCount(0);
        for (Ejercicios ej : ejercicios) {
            // suponiendo que Ejercicios tiene getNombre() y getRepeticiones() o similar
            String reps = (ej.getSeries() != null ? String.valueOf(ej.getSeries().size()) + " series" : "0");
            modeloTabla.addRow(new Object[] { ej.getNombre(), reps, "img" });
        }
    }

    // Muestra los datos del ejercicio con índice idx en el centro
    private void mostrarEjercicio(int idx) {
        if (idx < 0 || idx >= ejercicios.size()) return;
        Ejercicios ej = ejercicios.get(idx);
        lblNombreEjercicio.setText(ej.getNombre());
        lblRepeticiones.setText(ej.getDescripcion() != null ? ej.getDescripcion() : "");
        // si tu clase tiene URL/Imagen -> cargarla. Aquí usamos placeholder
        lblFotoEjercicio.setText("Foto: " + ej.getNombre());
        // Reset de contSeries para este ejercicio
        contSeriesActual = 0;

        // Preparar cronometro del ejercicio (no arrancar aún)
        hiloEjercicio = new HiloCronometro(new JLabel("")); // label temporal para obtener minutos/segundos
        // No ponemos hiloEjercicio.start() aquí — se inicia con el primer inicio de la serie.
    }

    // Maneja el comportamiento del botón iniciar/pausar/siguiente
    private synchronized void manejarBotonIniciar() {
        String texto = btnIniciar.getText();

        // Primer arranque del cronómetro principal si no ha empezado nunca
        if (!hiloCronoPrincipal.isAlive() && 
            ("Iniciar".equals(texto) || "Siguiente Serie".equals(texto) || "Siguiente Ejercicio".equals(texto))) {
            hiloCronoPrincipal = new HiloCronometro(lblCronoPrincipal);
            hiloCronoPrincipal.start();
        }

        // ---- Caso 1: INICIAR / SIGUIENTE SERIE / SIGUIENTE EJERCICIO ----
        if ("Iniciar".equals(texto) || "Siguiente Serie".equals(texto) || "Siguiente Ejercicio".equals(texto)) {
            btnIniciar.setText("Pausar");
            btnIniciar.setBackground(Color.ORANGE);
            btnIniciar.setForeground(Color.BLACK);

            // Arranca cronómetro de ejercicio si no estaba corriendo
            if (hiloEjercicio == null || !hiloEjercicio.isAlive()) {
                hiloEjercicio = new HiloCronometro(new JLabel("Ejercicio: 00:00"));
                hiloEjercicio.start();
            }

            // Validar índice de ejercicio
            if (indiceEjercicio < ejercicios.size()) {
                Ejercicios ejercicioActual = ejercicios.get(indiceEjercicio);

                // Si aún quedan series del ejercicio actual
                if (ejercicioActual.getSeries() != null && contSeriesActual < ejercicioActual.getSeries().size()) {

                    // --- 5 segundos de pre-cuenta ---
                    lblCronoRegresivo.setText("Preparación: 0:05");
                    final HiloRegresivo preCuenta = new HiloRegresivo(lblCronoRegresivo, 5);
                    preCuenta.start();

                    // --- Esperar fin de preCuenta y lanzar serie ---
                    Thread hiloInicioSerie = new Thread() {
                        public void run() {
                            try {
                                while (preCuenta.isAlive()) {
                                    Thread.sleep(50);
                                }
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }

                            // Iniciar serie y descanso
                            int segundosSerie = 0;
                            int tiempoDescanso = 0;
                            try {
                          
                                tiempoDescanso = ejercicioActual.getSeries().get(contSeriesActual).getDuracion();
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
                                    indiceEjercicio, 
                                    workoutSeleccionado, 
                                    ejercicioActual, 
                                    contSeriesActual,
                                    null, 
                                    panelCentral, 
                                    null, 
                                    tiemposPorEjercicio
                            );

                            hiloEsperar.start();
                        }
                    };
                    hiloInicioSerie.start();

                } else {
                    // Si ya no hay series -> preparar siguiente ejercicio
                    btnIniciar.setText("Siguiente Ejercicio");
                    btnIniciar.setBackground(new Color(0, 128, 0));
                    btnIniciar.setForeground(Color.WHITE);
                }

            } else {
                // Si ya no quedan ejercicios, marcamos como terminado
                btnIniciar.setText("Workout Terminado");
                btnIniciar.setEnabled(false);
            }
        }

        // ---- Caso 2: PAUSAR ----
        else if ("Pausar".equals(texto)) {
            btnIniciar.setText("Reanudar");
            btnIniciar.setBackground(new Color(70, 130, 180));
            btnIniciar.setForeground(Color.WHITE);

            if (hiloCronoPrincipal != null) hiloCronoPrincipal.cambiarEstado();
            if (hiloEjercicio != null) hiloEjercicio.cambiarEstado();
            if (hiloSerie != null) hiloSerie.cambiarEstado();
            if (hiloDescanso != null) hiloDescanso.cambiarEstado();
        }

        // ---- Caso 3: REANUDAR ----
        else if ("Reanudar".equals(texto)) {
            btnIniciar.setText("Pausar");
            btnIniciar.setBackground(Color.ORANGE);
            btnIniciar.setForeground(Color.BLACK);

            if (hiloCronoPrincipal != null) hiloCronoPrincipal.cambiarEstado();
            if (hiloEjercicio != null) hiloEjercicio.cambiarEstado();
            if (hiloSerie != null) hiloSerie.cambiarEstado();
            if (hiloDescanso != null) hiloDescanso.cambiarEstado();
        }

        // ---- Caso 4: SIGUIENTE EJERCICIO ----
        else if ("Siguiente Ejercicio".equals(texto)) {
            indiceEjercicio++;
            if (indiceEjercicio < ejercicios.size()) {
                mostrarEjercicio(indiceEjercicio);
                btnIniciar.setText("Iniciar");
                btnIniciar.setBackground(new Color(0, 128, 0));
                btnIniciar.setForeground(Color.WHITE);
            } else {
                mostrarResumen(false);
            }
        }

        // ---- Caso 5: WORKOUT TERMINADO ----
        else if ("Workout Terminado".equals(texto)) {
            mostrarResumen(false);
        }
    }


    // Muestra resumen final. si esSalida=true -> usuario pulsó salir, si false -> final natural
    private void mostrarResumen(boolean esSalida) {
        // Parar hilos si están corriendo
        try {
            if (hiloCronoPrincipal != null) hiloCronoPrincipal.terminar();
            if (hiloEjercicio != null) hiloEjercicio.terminar();
            if (hiloSerie != null) hiloSerie.terminar();
            if (hiloDescanso != null) hiloDescanso.terminar();
        } catch (Exception e) { /* ignore */ }

        // Calcular tiempo total invertido (tomamos el cronómetro principal)
        String tiempoTotal = lblCronoPrincipal.getText(); // formato "Total: mm:ss" o similar

        // porcentaje completado
        int realizados = Math.min(indiceEjercicio + (contSeriesActual > 0 ? 1 : 0), ejercicios.size());
        int total = ejercicios.size();
        int porcentaje = total == 0 ? 100 : (realizados * 100 / total);

        // mensaje motivacional simple según porcentaje
        String mensaje;
        if (porcentaje == 100 && !esSalida) mensaje = "¡Enhorabuena! Has completado el workout 💪";
        else if (porcentaje >= 75) mensaje = "¡Muy bien! Estás cerca del objetivo.";
        else if (porcentaje >= 40) mensaje = "Buen trabajo — sigue así.";
        else mensaje = "¡A por ello! Cada minuto cuenta.";

        // Dialogo resumen
        String resumen = String.format("<html><body style='width:300px'>Tiempo total: %s<br/>Ejercicios completados: %d/%d (%d%%)<br/><br/>%s</body></html>",
                tiempoTotal, realizados, total, porcentaje, mensaje);

        int opcion = JOptionPane.showOptionDialog(this, resumen, "Resumen workout",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                new String[] { "Confirmar" }, "Confirmar");

        // Al confirmar, volvemos a la pantalla principal (en este ejemplo cerramos la ventana)
        if (opcion == 0) {
            this.dispose();
            // Aquí en tu app deberías abrir la pantalla principal de workouts
        }
    }

}
