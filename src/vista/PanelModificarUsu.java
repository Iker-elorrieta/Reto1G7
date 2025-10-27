package vista;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.google.cloud.firestore.DocumentSnapshot;

import controlador.Controlador;
import modelo.Usuario;
import modelo.Workouts;

public class PanelModificarUsu extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtNmbr;
	private JTextField txtAplld;
	private JTextField txtFechaNacmnt;
	private JTextField txtEmail;
	private JLabel lblApellido;
	private JLabel lblFechaNacimiento;
	private JLabel lblEmail;
	private JLabel lblContr;
	private JPasswordField passwordField;
	private JButton btnNewButton;
	Controlador controlador = new Controlador();
	
	/**
	 * Create the frame.
	 * @param usuarioActual 
	 */
	public PanelModificarUsu(DocumentSnapshot usu, List<Workouts> todosLosWorkouts) {
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setBounds(100, 100, 657, 617);
	    contentPane = new JPanel();
	    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	    setContentPane(contentPane);
	    contentPane.setLayout(null);

	    JPanel panel = new JPanel();
	    panel.setBackground(new Color(192, 192, 192));
	    panel.setBounds(0, 0, 641, 578);
	    contentPane.add(panel);
	    panel.setLayout(null);

	    // 🔹 Extraer datos del usuario desde Firestore
	    String nombre = usu.getString("nombre");
	    String apellido = usu.getString("apellido");
	    String email = usu.getString("email");
	    String contraseña = usu.getString("contraseña");
	    String fechaTexto = "";
	    if (usu.getDate("fechaNacimiento") != null) {
	 
	    	String fechaRaw = usu.getString("fechaNacimiento");

	    	if (fechaRaw != null && !fechaRaw.isEmpty()) {
	    	    fechaTexto = fechaRaw;
	    	}

	    }

	    // 🔹 Título
	    JLabel lblTitulo = new JLabel("Hola, " + nombre);
	    lblTitulo.setFont(new Font("Verdana", Font.BOLD, 28));
	    lblTitulo.setBounds(167, 47, 348, 40);
	    panel.add(lblTitulo);

	    // 🔹 Campos
	    txtNmbr = new JTextField(nombre);
	    txtNmbr.setBounds(240, 139, 226, 26);
	    panel.add(txtNmbr);

	    txtAplld = new JTextField(apellido);
	    txtAplld.setBounds(240, 193, 226, 26);
	    panel.add(txtAplld);

	    txtFechaNacmnt = new JTextField(fechaTexto);
	    txtFechaNacmnt.setBounds(240, 244, 226, 26);
	    panel.add(txtFechaNacmnt);

	    txtEmail = new JTextField(email);
	    txtEmail.setBounds(240, 290, 226, 26);
	    panel.add(txtEmail);

	    passwordField = new JPasswordField(contraseña);
	    passwordField.setBounds(240, 335, 226, 35);
	    panel.add(passwordField);

	    // 🔹 Labels
	    JLabel lblNombre = new JLabel("NOMBRE:");
	    lblNombre.setForeground(Color.WHITE);
	    lblNombre.setFont(new Font("Candara", Font.BOLD, 18));
	    lblNombre.setBounds(119, 144, 94, 20);
	    panel.add(lblNombre);

	    lblApellido = new JLabel("APELLIDO:");
	    lblApellido.setForeground(Color.WHITE);
	    lblApellido.setFont(new Font("Candara", Font.BOLD, 18));
	    lblApellido.setBounds(119, 199, 111, 20);
	    panel.add(lblApellido);

	    lblFechaNacimiento = new JLabel("FECHA NACIMIENTO:");
	    lblFechaNacimiento.setForeground(Color.WHITE);
	    lblFechaNacimiento.setFont(new Font("Candara", Font.BOLD, 18));
	    lblFechaNacimiento.setBounds(32, 250, 181, 26);
	    panel.add(lblFechaNacimiento);

	    lblEmail = new JLabel("EMAIL:");
	    lblEmail.setFont(new Font("Candara", Font.BOLD, 18));
	    lblEmail.setForeground(Color.WHITE);
	    lblEmail.setBounds(133, 296, 64, 20);
	    panel.add(lblEmail);

	    lblContr = new JLabel("CONTRASEÑA:");
	    lblContr.setForeground(Color.WHITE);
	    lblContr.setFont(new Font("Candara", Font.BOLD, 18));
	    lblContr.setBounds(79, 344, 118, 20);
	    panel.add(lblContr);
	}
}

