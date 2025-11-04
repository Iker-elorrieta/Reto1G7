package vista;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

import conexion.Conexion;
import controlador.Controlador;
import modelo.Ejercicios;
import modelo.Historial;
import modelo.Usuario;
import modelo.Workouts;

import javax.swing.JLabel;
import java.awt.Font;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class PanelWorkouts extends JFrame {

	 private static final long serialVersionUID = 1L;
	    private JPanel contentPane;
	    private JTable tablaWorkouts;
	    private JTable tablaEjers;
	    private JComboBox<Integer> nivel;
	
	    private List<Workouts> todosLosWorkouts;
	    private JButton btnPerfil;
	    private int nivelActual;  
	    private DocumentSnapshot usu;
	    private List<Workouts> workoutsVisibles = new ArrayList<>();
	    private DefaultTableModel modeloWorkouts;




	    static Controlador cntrldr = new Controlador();
	    private JScrollPane scrollWorkouts;
	    private JScrollPane scrollEjers;
	    
	    

	    public PanelWorkouts(DocumentSnapshot docUsu, List<Workouts> workoutsDisp) {
	    	    Firestore db;
				try {
					db = Conexion.conectar();
					  this.todosLosWorkouts = cntrldr.cargarWorkoutsDesdeFirestore(db);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	  
	        this.usu = docUsu;
	        this.nivelActual = docUsu.contains("nivel") ? docUsu.getLong("nivel").intValue() : 0;
	     
	      
	     

	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        setBounds(100, 100, 1221, 741);
	        contentPane = new JPanel();
	        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	        setContentPane(contentPane);
	        contentPane.setLayout(null);

	        JLabel lblTitulo = new JLabel("WORKOUTS");
	        lblTitulo.setForeground(Color.WHITE);
	        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 35));
	        lblTitulo.setBounds(474, 60, 234, 80);
	        contentPane.add(lblTitulo);
	        JPanel panelNivel = new JPanel();
	        panelNivel.setBounds(44, 146, 200, 31); // ancho ampliado para texto
	        contentPane.add(panelNivel);

	        
	        
	        /*
	         * ---------Nivel--------------
	         * */
	        nivel = new JComboBox<>();
	        panelNivel.add(new JLabel("Nivel:"));
	        panelNivel.add(nivel);

	        cntrldr.filtrarPorNivel(nivelActual,todosLosWorkouts,nivel); // primero llenar el combo
	        nivel.setSelectedItem(nivelActual); // luego seleccionar el valor actual

	        nivel.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                int nivelSeleccionado = (int) nivel.getSelectedItem();

	                DefaultTableModel modeloWorkouts = (DefaultTableModel) tablaWorkouts.getModel();
	                DefaultTableModel modeloEjers = (DefaultTableModel) tablaEjers.getModel();
	                modeloWorkouts.setRowCount(0);
	                modeloEjers.setRowCount(0);
	                //Mostramos los workouts que sean del nivel que hemos cogido antes
	                workoutsVisibles = cntrldr.mostrarWorkoutsPorNivel(nivelSeleccionado, modeloWorkouts, todosLosWorkouts);
	            }
	        });
	   
	                    
	      /*----------------------------------------------------------------------------------------------------------------------
	       * */
	    

	             
	            
	
	        /*
	         * -------------- TABLA WORKOUTS-----------------
	         * */
	        // Tabla de workouts
	         modeloWorkouts = new DefaultTableModel(
	            new Object[][] {},
	            new String[] { "Nombre", "Nivel", "Ejercicios", "Video URL" }
	        );
	        tablaWorkouts = new JTable(modeloWorkouts);
	        tablaWorkouts.setDefaultEditor(Object.class, null);
	        
	        scrollWorkouts = new JScrollPane(tablaWorkouts);
	        scrollWorkouts.setBorder(null);
	        scrollWorkouts.setBounds(44, 176, 541, 450);
	        contentPane.add(scrollWorkouts);

	        modeloWorkouts = (DefaultTableModel) tablaWorkouts.getModel();
	        cntrldr.mostrarWorkoutsPorNivel(nivelActual, modeloWorkouts, todosLosWorkouts);

	        

	        tablaWorkouts.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseClicked(MouseEvent evt) {
	                int filaSeleccionada = tablaWorkouts.getSelectedRow();
	                if (filaSeleccionada >= 0 && filaSeleccionada < workoutsVisibles.size()) {
	                    Workouts workoutSeleccionado = workoutsVisibles.get(filaSeleccionada);
	                    DefaultTableModel modeloEjers = (DefaultTableModel) tablaEjers.getModel();
	                    modeloEjers.setRowCount(0);
	                    cntrldr.mostrarEjerciciosPorWorkout(workoutSeleccionado, modeloEjers);
	                }
	            }
	        });

	        
            
     /*----------------------------------------------------------------------------------------------------------------------
      * */
   

	        
	        /*
	         * ------------------TABLA EJERCICIOS-----------------
	         * */
	        // Tabla de ejercicios
	        DefaultTableModel modeloEjers = new DefaultTableModel(
	            new Object[][] {},
	            new String[] { "Nombre", "Descripción", "Descanso (seg)" }
	        );
	        tablaEjers = new JTable(modeloEjers);
	        tablaEjers.setDefaultEditor(Object.class, null);
	        scrollEjers = new JScrollPane(tablaEjers);
	        scrollEjers.setBorder(null);
	        scrollEjers.setBounds(620, 176, 541, 450);
	        contentPane.add(scrollEjers);
                       

	        // Evento al seleccionar ejercicio
	        tablaEjers.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseClicked(MouseEvent evt) {
	                int filaEj = tablaEjers.getSelectedRow();
	                int filaWorkout = tablaWorkouts.getSelectedRow();
	                boolean ejercicioEncontrado = false;

	                if (filaEj >= 0 && filaWorkout >= 0) {
	                    String nombreWorkout = (String) tablaWorkouts.getValueAt(filaWorkout, 0);
	                    Workouts workoutSeleccionado = null;

	                    for (Workouts w : todosLosWorkouts) {
	                        if (w.getNombre().equals(nombreWorkout)) {
	                            workoutSeleccionado = w;
	                            ejercicioEncontrado = true;
	                            break;
	                        }
	                    }

	                    if (ejercicioEncontrado && workoutSeleccionado != null) {
	                        PanelEjercicios panelEj = new PanelEjercicios(docUsu, workoutSeleccionado);
	                        panelEj.setVisible(true);
	                        dispose();
	                    } else {
	                        JOptionPane.showMessageDialog(null, "Ejercicio no encontrado.");
	                    }
	                }
	            }
	        });
	        
            
     /*----------------------------------------------------------------------------------------------------------------------
      * */
   
	        
	        

        // 🔹 Botón historial
        JButton btnHstrl = new JButton("Ver historial");
        btnHstrl.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		List<Historial> historialRealizado = cntrldr.obtenerHistorialWorkouts(docUsu.getId());
        		PanelHistorialWrkts historial = new PanelHistorialWrkts(docUsu, historialRealizado, todosLosWorkouts);

                historial.setVisible(true);
        	}
        });
        btnHstrl.setBounds(1045, 97, 150, 43);
        contentPane.add(btnHstrl);
        
        
        

        
        
        
        
        /*
         * --------BOTON DE PERFIL----------------
         * */
        ImageIcon iconoPerfil = new ImageIcon(getClass().getResource("/img/usuIcon.png"));

        btnPerfil = new JButton(iconoPerfil);
        btnPerfil.setBounds(1106, 11, 89, 86);
        btnPerfil.setBorderPainted(false);
        btnPerfil.setContentAreaFilled(false);
        btnPerfil.setFocusPainted(false);

        btnPerfil.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // acción del botón

            	 PanelUsu principal = new PanelUsu(docUsu,todosLosWorkouts);
	                principal.setVisible(true);
	                dispose(); 
            }
        });

        contentPane.add(btnPerfil);

       //FONDO
       
        JLabel fondo = new JLabel(new ImageIcon(getClass().getResource("/img/fondoGym.jpg")));
       fondo.setBounds(0, 0, 1221, 702);
       contentPane.add(fondo);
       fondo.setLayout(null);
    

   


 
    }

		
}