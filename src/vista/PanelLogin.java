package vista;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;


import conexion.Conexion;
import controlador.Controlador;
import modelo.Historial;
import modelo.Usuario;
import modelo.Workouts;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;
import java.awt.Color;
import javax.swing.ImageIcon;

public class PanelLogin extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtEmail;
	private JButton btnLogin;
	private JButton btnRgstr;
	Controlador cntrldr = new Controlador();
	private JPasswordField txtCntr;



	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PanelLogin frame = new PanelLogin();
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
	public PanelLogin() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 638, 614);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(192, 192, 192));
		panel.setBounds(0, 0, 622, 575);
		contentPane.add(panel);
		panel.setLayout(null);
		

		
		txtEmail = new JTextField();
		txtEmail.setBounds(191, 192, 220, 26);
		panel.add(txtEmail);
		txtEmail.setColumns(10);
		
		JLabel lblUsu = new JLabel("USUARIO:");
		lblUsu.setForeground(new Color(255, 255, 255));
		lblUsu.setFont(new Font("Candara", Font.BOLD, 18));
		lblUsu.setBounds(263, 155, 89, 26);
		panel.add(lblUsu);
		
		JLabel lblCntr = new JLabel("CONTRASEÑA:");
		lblCntr.setForeground(new Color(255, 255, 255));
		lblCntr.setFont(new Font("Candara", Font.BOLD, 17));
		lblCntr.setBounds(242, 243, 138, 26);
		panel.add(lblCntr);
		
		txtCntr = new JPasswordField();
		txtCntr.setBounds(191, 280, 220, 26);
		panel.add(txtCntr);
		
		
		
		
		btnLogin = new JButton("LOGIN");
		btnLogin.setBackground(new Color(255, 255, 255));
		btnLogin.setFont(new Font("Candara", Font.BOLD, 13));
	
		
		/*
		 * ---------------------------------------Conexion con Firebase para buscar al Usuario y recoger su Informacion(Datos de Usuario y Workouts)------------------------------------------------------------------------------------------------------------
		 * 
		 * */
		btnLogin.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        String email = txtEmail.getText().trim();
		        String contraseña = new String(txtCntr.getPassword()).trim();

		        if (email.isEmpty() || contraseña.isEmpty()) {
		            JOptionPane.showMessageDialog(null, "Rellena todos los campos");
		            return;
		        }

		        try {
		            Firestore db = Conexion.conectar();

		            boolean usuValido = cntrldr.verificarUsu(email, contraseña);

		            if (!usuValido) {
		                JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos.");
		                return;
		            }

		            List<QueryDocumentSnapshot> resultados = db.collection("usuarios")
		                .whereEqualTo("email", email)
		                .whereEqualTo("contraseña", contraseña)
		                .get()
		                .get()
		                .getDocuments();

		            if (resultados.isEmpty()) {
		                JOptionPane.showMessageDialog(null, "El usuario no existe en la base de datos.");
		                return;
		            }

		            DocumentSnapshot docUsu = resultados.get(0);

		            Usuario usuario = new Usuario(
		                docUsu.getString("nombre"),
		                docUsu.getString("apellido"),
		                docUsu.getString("email"),
		                docUsu.getString("contraseña"),
		                docUsu.getDate("fechaNacimiento"),
		                docUsu.contains("nivel") ? docUsu.getLong("nivel").intValue() : 0
		            );

		            List<Workouts> workoutsDisp = cntrldr.cargarWorkoutsDesdeFirestore(db);
		            

		            JOptionPane.showMessageDialog(null, "Accediendo a la Aplicación...");
		            PanelWorkouts siguiente = new PanelWorkouts(docUsu, workoutsDisp);

		       
		            ArrayList<Historial> historialList = new ArrayList<>(cntrldr.obtenerHistorialWorkouts(docUsu.getId()));
		            boolean backupOk = cntrldr.guardarBackupUsuario(usuario,workoutsDisp,historialList);

		            if (backupOk)
		                System.out.println("✅ Backup generado para: " + usuario.getNombre());
		            else
		                System.out.println("❌ Error generando backup para usuario.");
		          

		            siguiente.setVisible(true);
		            dispose();

		        } catch (InterruptedException | ExecutionException | IOException ex) {
		            ex.printStackTrace();
		            JOptionPane.showMessageDialog(null, "Error al conectar con Firestore.");
		        }
		    }
		});



		btnLogin.setBounds(138, 347, 121, 32);
		panel.add(btnLogin);
		
		btnRgstr = new JButton("REGISTRAR");
		btnRgstr.setFont(new Font("Candara", Font.BOLD, 13));
		btnRgstr.setBackground(new Color(255, 255, 255));
		btnRgstr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				   PanelRegistro principal = new PanelRegistro();
	                principal.setVisible(true);
	                dispose(); 
			}
		});
		btnRgstr.setBounds(341, 347, 121, 32);
		panel.add(btnRgstr);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon("C:\\Users\\in2dm3-a\\Pictures\\logoBajaExposicion.png"));
		lblNewLabel.setBounds(23, 0, 571, 564);
		panel.add(lblNewLabel);
		
		
		
		

	}
}
