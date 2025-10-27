package vista;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.google.cloud.firestore.Firestore;

import controlador.Controlador;
import modelo.Usuario;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import java.awt.Color;

public class PanelRegistro extends JFrame {

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
	private JLabel lblNewLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PanelRegistro frame = new PanelRegistro();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public PanelRegistro() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 657, 617);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(192, 192, 192));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(192, 192, 192));
		panel.setBounds(0, 0, 641, 578);
		contentPane.add(panel);
		panel.setLayout(null);
		
		txtNmbr = new JTextField();
		txtNmbr.setBounds(240, 139, 226, 26);
		panel.add(txtNmbr);
		txtNmbr.setColumns(10);
		
		txtAplld = new JTextField();
		txtAplld.setColumns(10);
		txtAplld.setBounds(240, 193, 226, 26);
		panel.add(txtAplld);
		
		txtFechaNacmnt = new JTextField();
		txtFechaNacmnt.setColumns(10);
		txtFechaNacmnt.setBounds(240, 244, 226, 26);
		panel.add(txtFechaNacmnt);
		
		txtEmail = new JTextField();
		txtEmail.setColumns(10);
		txtEmail.setBounds(240, 290, 226, 26);
		panel.add(txtEmail);
		
		JLabel lblNombre = new JLabel("NOMBRE:");
		lblNombre.setForeground(new Color(255, 255, 255));
		lblNombre.setFont(new Font("Candara", Font.BOLD, 18));
		lblNombre.setBounds(119, 144, 94, 20);
		panel.add(lblNombre);
		
		lblApellido = new JLabel("APELLIDO:");
		lblApellido.setForeground(new Color(255, 255, 255));
		lblApellido.setFont(new Font("Candara", Font.BOLD, 18));
		lblApellido.setBounds(119, 199, 111, 20);
		panel.add(lblApellido);
		
		lblFechaNacimiento = new JLabel("FECHA NACIMIENTO:");
		lblFechaNacimiento.setForeground(new Color(255, 255, 255));
		lblFechaNacimiento.setFont(new Font("Candara", Font.BOLD, 18));
		lblFechaNacimiento.setBounds(32, 250, 181, 26);
		panel.add(lblFechaNacimiento);
		
		lblEmail = new JLabel("EMAIL:");
		lblEmail.setFont(new Font("Candara", Font.BOLD, 18));
		lblEmail.setForeground(new Color(255, 255, 255));
		lblEmail.setBounds(133, 296, 64, 20);
		panel.add(lblEmail);
		
		lblContr = new JLabel("CONTRASEÑA:");
		lblContr.setForeground(new Color(255, 255, 255));
		lblContr.setFont(new Font("Candara", Font.BOLD, 18));
		lblContr.setBounds(79, 344, 118, 20);
		panel.add(lblContr);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(240, 335, 226, 35);
		panel.add(passwordField);
		
		btnNewButton = new JButton("REGISTRAR");
		btnNewButton.setBackground(new Color(255, 255, 255));
		btnNewButton.setFont(new Font("Candara", Font.BOLD, 18));
		btnNewButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        String nmbr = txtNmbr.getText();
		        String aplld = txtAplld.getText();
		        String email = txtEmail.getText();
		        String contrsñ = passwordField.getText();
		        String fechaTexto = txtFechaNacmnt.getText();
		        Date fechaNacmnt = null;

		        boolean datosValidos = true;

		        // 🔹 Validación de campos vacíos
		        if (nmbr.isEmpty() || aplld.isEmpty() || email.isEmpty() || contrsñ.isEmpty()) {
		            JOptionPane.showMessageDialog(null, "Campos vacíos. Por favor, completa todos los datos.");
		            datosValidos = false;
		        }

		        // 🔹 Validación de formato de correo
		        if (!email.contains("@")) {
		            JOptionPane.showMessageDialog(null, "Correo inválido. Debe contener '@'.");
		            datosValidos = false;
		        }

		        // 🔹 Validación de fecha
		        try {
		            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		            formato.setLenient(false);
		            fechaNacmnt = formato.parse(fechaTexto);
		        } catch (Exception ex) {
		            JOptionPane.showMessageDialog(null, "Fecha inválida. Usa el formato dd/MM/yyyy");
		            datosValidos = false;
		        }

		        // 🔹 Verificación de correo duplicado
		        boolean correoDuplicado = controlador.verificarGmail(email);
		        if (correoDuplicado) {
		            JOptionPane.showMessageDialog(null, "Este correo ya está registrado.");
		            datosValidos = false;
		        }

		        // 🔹 Si todo es válido, registrar
		        if (datosValidos) {
		            try {
		                Firestore db = conexion.Conexion.conectar();
		                List<com.google.cloud.firestore.QueryDocumentSnapshot> documentos = db.collection("usuarios").get().get().getDocuments();
		                String id = "usu" + (documentos.size() + 1);
		                int nivelActl = 0;

		                boolean registrado = controlador.registrarUsuario(id, nmbr, aplld, fechaNacmnt, email, contrsñ, nivelActl);

		                if (registrado) {
		                    JOptionPane.showMessageDialog(null, "Usuario registrado correctamente.");
		                    PanelLogin principal = new PanelLogin();
		                    principal.setVisible(true);
		                    dispose();
		                } else {
		                    JOptionPane.showMessageDialog(null, "Error al registrar el usuario.");
		                }
		            } catch (Exception ex) {
		                JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.");
		                ex.printStackTrace();
		            }
		        }
		    }
		});

		btnNewButton.setBounds(275, 395, 156, 35);
		panel.add(btnNewButton);
		
		lblNewLabel = new JLabel("");
		lblNewLabel.setBackground(new Color(192, 192, 192));
		lblNewLabel.setIcon(new ImageIcon("C:\\Users\\in2dm3-a\\Pictures\\logoBajaExposicion.png"));
		lblNewLabel.setBounds(0, 0, 641, 579);
		panel.add(lblNewLabel);

	}

}
