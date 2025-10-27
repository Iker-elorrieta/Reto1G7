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

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;

import conexion.Conexion;
import controlador.Controlador;
import modelo.Ejercicios;
import modelo.Serie;
import modelo.Usuario;
import modelo.Workouts;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PanelWorkouts extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable tablaWorkouts;
    private JComboBox<Integer> nivel;
    private DocumentSnapshot usu;
    private List<Workouts> todosLosWorkouts;
    private JPanel panelNivel;
    private JProgressBar progressBar;
    static Controlador cntrldr = new Controlador();
    private JButton btnPerfil;
	private String nombre;
	private int nivelActual;

    /**
     * Launch the application.


    /**
     * Create the frame.
     */
    public PanelWorkouts(DocumentSnapshot usu, List<Workouts> workoutsDisp) {
       
        this.todosLosWorkouts = workoutsDisp;
        this.usu = usu;
        
     
        
        this.nombre= usu.getString("nombre");
        this.nivelActual = usu.contains("nivelActual") ? usu.getLong("nivelActual").intValue() : 0;


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
        

        // 🔹 Panel de filtro por nivel
        panelNivel = new JPanel();
        panelNivel.setBounds(44, 146, 89, 31);
        contentPane.add(panelNivel);

        nivel = new JComboBox<>();
        for (int i = 0; i <=nivelActual; i++) {
            nivel.addItem(i);
        }
        nivel.setSelectedItem(nivelActual);
        panelNivel.add(new JLabel("Nivel:"));
        panelNivel.add(nivel);

        tablaWorkouts = new JTable();
        DefaultTableModel modelo = new DefaultTableModel(
            new Object[][] {},
            new String[] { "Nombre", "Nivel", "Ejercicios", "Video URL" }
        );
        tablaWorkouts.setModel(modelo);
        JScrollPane scrollPane = new JScrollPane(tablaWorkouts);
        scrollPane.setBorder(null); 
        scrollPane.setBounds(44, 176, 793, 450);
        contentPane.add(scrollPane);

        // 🔹 Botón historial
        JButton btnHstrl = new JButton("Ver historial");
        btnHstrl.setBounds(847, 514, 150, 43);
        contentPane.add(btnHstrl);

        // 🔹 Botón detalles
        JButton btnDtlls = new JButton("Ver detalles");
        btnDtlls.setBounds(847, 583, 150, 43);
        contentPane.add(btnDtlls);
        
        ImageIcon iconoPerfil = new ImageIcon("C:\\Users\\in2dm3-a\\Downloads\\eclipse-workspaceX\\Reto1G7\\img\\usuIcon.png");

        btnPerfil = new JButton(iconoPerfil);
        btnPerfil.setBounds(1106, 11, 89, 86);
        btnPerfil.setBorderPainted(false);
        btnPerfil.setContentAreaFilled(false);
        btnPerfil.setFocusPainted(false);

        btnPerfil.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // acción del botón
System.out.println(usu);
            	 PanelModificarUsu principal = new PanelModificarUsu(usu,todosLosWorkouts);
	                principal.setVisible(true);
	                dispose(); 
            }
        });

        contentPane.add(btnPerfil);

        
        // 🔹 Mostrar workouts disponibles
       cntrldr.mostrarWorkoutsPorNivel((int) nivel.getSelectedItem(), modelo,todosLosWorkouts);

       
       JLabel fondo = new JLabel(new ImageIcon("C:\\Users\\in2dm3-a\\Downloads\\eclipse-workspaceX\\Reto1G7\\img\\fondoGym.jpg"));
       fondo.setBounds(0, 0, 1221, 702);
       contentPane.add(fondo);
       fondo.setLayout(null);
    

   


 
    }
}
