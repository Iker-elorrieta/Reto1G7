package backups;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import modelo.Historial;
import modelo.Usuario;
import modelo.Workouts;

public class GenerarBackup {

	  private static final String RUTA_USUARIOS = "backups/clientes.dat";
	    private static final String RUTA_WORKOUTS = "backups/workouts.dat";
	    private static final String RUTA_HISTORICO = "backups/historico.xml";
	    private static final String NODO_RAIZ = "Usuarios";
	    
	 

	    public static void main(String[] args) {
	        // Punto de entrada si deseas ejecutar pruebas manuales
	    }

	    // === SERIALIZACIÓN DE WORKOUTS ===
	    public static void escribirWorkoutsEnArchivo(ArrayList<Workouts> workouts) {
	        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(RUTA_WORKOUTS))) {
	            for (Workouts workout : workouts) {
	                output.writeObject(workout);
	            }
	        } catch (IOException e) {
	            System.err.println("Error al guardar workouts:");
	            e.printStackTrace();
	        }
	    }

	    public static ArrayList<Workouts> leerWorkoutsDesdeArchivo() {
	        ArrayList<Workouts> workouts = new ArrayList<>();
	        try (FileInputStream fis = new FileInputStream(RUTA_WORKOUTS);
	             ObjectInputStream ois = new ObjectInputStream(fis)) {

	            while (fis.getChannel().position() < fis.getChannel().size()) {
	                Object obj = ois.readObject();
	                if (obj instanceof Workouts) {
	                    workouts.add((Workouts) obj);
	                }
	            }
	        } catch (FileNotFoundException e) {
	            System.out.println("Archivo de workouts no encontrado. Se creará uno nuevo.");
	        } catch (IOException | ClassNotFoundException e) {
	            System.err.println("Error al leer workouts:");
	            e.printStackTrace();
	        }
	        return workouts;
	    }

	    // === AUTENTICACIÓN DE USUARIOS ===
	    public static Usuario leerUsuariosDesdeArchivo(String email, String contrasena) {
	        try (FileInputStream fis = new FileInputStream(RUTA_USUARIOS);
	             ObjectInputStream ois = new ObjectInputStream(fis)) {

	            while (fis.getChannel().position() < fis.getChannel().size()) {
	                Object obj = ois.readObject();

	                if (obj instanceof Usuario usu) {
	                    if (usu.getEmail().equals(email) && usu.getContraseña().equals(contrasena)) {
	                        return usu;
	                    } else {
	                        System.out.println("Credenciales incorrectas para el usuario: " + usu.getEmail());
	                    }
	                } else {
	                    System.out.println("Objeto no es instancia de Usuario: " + obj.getClass().getSimpleName());
	                }
	            }

	        } catch (FileNotFoundException e) {
	            System.out.println("Archivo de usuarios no encontrado. Se creará uno nuevo.");
	        } catch (IOException | ClassNotFoundException e) {
	            System.err.println("Error al leer usuarios:");
	            e.printStackTrace();
	        }

	        System.out.println("No se encontró ningún usuario con las credenciales proporcionadas.");
	        return null;
	    }

	    // === PARSEO DE HISTÓRICO XML ===
	    public static ArrayList<Historial> obtenerHistoricoBackups() {
	        ArrayList<Historial> historico = new ArrayList<>();
	        File archivo = new File(RUTA_HISTORICO);

	        if (!archivo.exists()) {
	            System.out.println("Archivo histórico no encontrado.");
	            return historico;
	        }

	        try {
	            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder builder = factory.newDocumentBuilder();
	            Document doc = builder.parse(archivo);
	            doc.getDocumentElement().normalize();

	            NodeList usuarios = doc.getElementsByTagName(NODO_RAIZ);
	            for (int i = 0; i < usuarios.getLength(); i++) {
	                System.out.println(usuarios.item(i).getNodeName() + " #" + (i + 1));
	                NodeList campos = usuarios.item(i).getChildNodes();
	                for (int j = 0; j < campos.getLength(); j++) {
	                    System.out.println(campos.item(j).getNodeName() + ": " + campos.item(j).getTextContent());
	                }
	            }
	        } catch (ParserConfigurationException | SAXException | IOException e) {
	            System.err.println("Error al leer el archivo histórico:");
	            e.printStackTrace();
	        }

	        return historico;
	    }
	
}
