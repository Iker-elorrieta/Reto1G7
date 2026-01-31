package controlador;


import java.io.FileOutputStream;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import javax.swing.table.DefaultTableModel;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import conexion.Conexion;
import modelo.Ejercicios;
import modelo.Historial;
import modelo.Serie;
import modelo.Usuario;
import modelo.Workouts;


public class Controlador {
	
	/*
	 * ------------------------------------------------------- Desde Login para Verificar el Usuario con el email y contraseña -----------------------------------------------------------------------------------------------------------------------------------
	 * */
	public DocumentSnapshot verificarUsu(String email, String contraseña) {
	    try {
	        Firestore db = Conexion.conectar();

	        List<QueryDocumentSnapshot> resultados = db.collection("usuarios")
	            .whereEqualTo("email", email)
	            .whereEqualTo("contraseña", contraseña)
	            .get()
	            .get()
	            .getDocuments();

	        if (resultados != null && !resultados.isEmpty()) {
	            return resultados.get(0); // Devuelve el documento del usuario
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return null; 
	}


    /*
     * ----------------------------------------------  Desde Registro Onstruimos con Map ,Verificacion de clave Unica (email) y Formato de id del Usuario-------------------------------------------------------------------------------------------------------
     * */
	
	public boolean registrarUsuario(String id, String nmbr, String aplld, Timestamp fechaTimestamp, String email, String contrsñ, int nivelActl) {
		boolean registrado = false; 

	    try {
	        Firestore db = Conexion.conectar();

	        // Construimos el mapa de datos del nuevo usuario
	        Map<String, Object> datos = new HashMap<>();
	        datos.put("nombre", nmbr);
	        datos.put("apellido", aplld);
	        datos.put("fechaNacimiento", fechaTimestamp);
	        datos.put("email", email);
	        datos.put("contraseña", contrsñ);
	        datos.put("nivel", nivelActl);

	        // Verificamos si ya existe un usuario con el mismo email
	        QuerySnapshot snapshot = db.collection("usuarios")
	            .whereEqualTo("email", email)
	            .get()
	            .get();

	        if (!snapshot.isEmpty()) {
	       
	            JOptionPane.showMessageDialog(null, "Ya existe un usuario con ese email.");
	            registrado = false;
	        } else {
	        	 //Utilizamos ApiFuture y el set en vez del add para crear los nombres del id nosotros
	            ApiFuture<WriteResult> usuario = db.collection("usuarios").document(id).set(datos);
	            usuario.get(); // Esperamos a que se complete la escritura
	            registrado = true;
	        }

	    } catch (Exception e) {
	          e.printStackTrace();
	        registrado = false;
	    }

	    return registrado; 
	}

	
	public List<Workouts> mostrarWorkoutsPorNivel(int nivel, DefaultTableModel modelo, List<Workouts> todos) {
	    List<Workouts> filtrados = new ArrayList<>();
	    modelo.setRowCount(0);
	    for (Workouts w : todos) {
	        if (w.getNivel() == nivel) {
	            filtrados.add(w);
	            modelo.addRow(new Object[] {
	                w.getNombre(),
	                w.getNivel(),
	                w.getNumEjers(),
	                w.getVideoUrl()
	            });
	        }
	    }
	    return filtrados;
	}



	 //Carga workouts completos desde Firestore
	public List<Workouts> cargarWorkoutsDesdeFirestore(Firestore db) {
	    List<Workouts> workoutsDisp = new ArrayList<>();
	    try {
	        List<QueryDocumentSnapshot> docsWorkouts = db.collection("workouts").get().get().getDocuments();

	        for (QueryDocumentSnapshot doc : docsWorkouts) {
	            // Validar campos numéricos
	            Long numEjersLong = doc.getLong("numEjers");
	            Long nivelLong = doc.getLong("nivel");

	            int numEjers = numEjersLong != null ? numEjersLong.intValue() : 0;
	            int nivel = nivelLong != null ? nivelLong.intValue() : 0;

	            Workouts w = new Workouts(
	                doc.getId(),
	                numEjers,
	                nivel,
	                doc.getString("nombre"),
	                doc.getString("video") // campo correcto según tu BD
	            );

	            List<Ejercicios> ejercicios = new ArrayList<>();
	            List<QueryDocumentSnapshot> docsEj = db.collection("workouts")
	                .document(w.getId())
	                .collection("ejercicios")
	                .get().get().getDocuments();

	            for (QueryDocumentSnapshot ej : docsEj) {
	                List<Serie> series = new ArrayList<>();
	                List<QueryDocumentSnapshot> docsSeries = ej.getReference()
	                    .collection("series")
	                    .get().get().getDocuments();

	                for (QueryDocumentSnapshot s : docsSeries) {	            	                                
	                    Serie serie = new Serie(
	                        s.getString("nombre"),
	                       s.getLong("tiempo").intValue(),
	                        s.getString("foto") 
	                    );
	                    series.add(serie);
	                }

	           

	                Ejercicios ejercicio = new Ejercicios(
	                    ej.getString("nombre"),
	                    ej.getString("descripcion"),
	                    ej.getLong("tiempoDescanso").intValue(),
	                    series
	                );
	                ejercicios.add(ejercicio);
	            }

	            w.setEjers(ejercicios);
	            workoutsDisp.add(w);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return workoutsDisp;
	}

	
	public void filtrarPorNivel(int nivelActual, List<Workouts> todosLosWorkouts, JComboBox<Integer> nivel) {
		  int[] niveles = new int[6]; 
		    int contador = 0;

		    for (Workouts workout : todosLosWorkouts) {
		        int nivelWorkout = workout.getNivel();
		        

		        // Solo incluir niveles menores o iguales al nivel del usuario
		        if (nivelWorkout <= nivelActual) {
		        boolean repetido = false;

		        for (int j = 0; j < contador; j++) {
		            if (niveles[j] == nivelWorkout) {
		                repetido = true;
		                break;
		            }
		        }

		        if (!repetido) {
		            nivel.addItem(nivelWorkout);
		            niveles[contador] = nivelWorkout;
		            contador++;
		        }
		    }
		    }

		    // Seleccionar el nivel actual del usuario
		    nivel.setSelectedItem(nivelActual);
		
	}




	public boolean verificarGmail(String email) {
	    try {
	        Firestore db = conexion.Conexion.conectar();
	        List<com.google.cloud.firestore.QueryDocumentSnapshot> documentos = db.collection("usuarios").get().get().getDocuments();

	        for (com.google.cloud.firestore.QueryDocumentSnapshot doc : documentos) {
	            String correoExistente = doc.getString("email");
	            if (correoExistente != null && correoExistente.equalsIgnoreCase(email)) {
	                JOptionPane.showMessageDialog(null, "Este correo ya está registrado.");
	                return true;
	            }
	        }
	    } catch (Exception e) {
	        JOptionPane.showMessageDialog(null, "Error al verificar el correo.");
	        e.printStackTrace();
	    }

	    return false;
	}

	

	public Date validarDatosRegistro(String nmbr, String aplld, String email, String contrsñ, String fechaTexto) {
	
		    boolean datosValidos = true;
		    Date fechaNacmnt = null;

		    if (nmbr.isEmpty() || aplld.isEmpty() || email.isEmpty() || contrsñ.isEmpty()) {
		        JOptionPane.showMessageDialog(null, "Campos vacíos. Por favor, completa todos los datos.");
		        datosValidos = false;
		    }

		    if (!email.contains("@")) {
		        JOptionPane.showMessageDialog(null, "Correo inválido. Debe contener '@'.");
		        datosValidos = false;
		    }

		    try {
		        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		        formato.setLenient(false);
		        fechaNacmnt = formato.parse(fechaTexto);
		    } catch (Exception ex) {
		        JOptionPane.showMessageDialog(null, "Fecha inválida. Usa el formato dd/MM/yyyy");
		        datosValidos = false;
		    }

		    boolean correoDuplicado = verificarGmail(email);
		    if (correoDuplicado) {
		        JOptionPane.showMessageDialog(null, "Este correo ya está registrado.");
		        datosValidos = false;
		    }

		    return datosValidos ? fechaNacmnt : null;
		}


	public boolean registrarUsuarioEnFirestore(String nmbr, String aplld, String email, String contrsñ, Date fechaNacmnt) {
	    try {
	        Firestore db = conexion.Conexion.conectar();
	        List<QueryDocumentSnapshot> documentos = db.collection("usuarios").get().get().getDocuments();
	        String id = "usu" + (documentos.size() + 1);
	        int nivelActl = 0;
	        Timestamp fechaTimestamp = Timestamp.of(fechaNacmnt);

	     
	        boolean registrado = registrarUsuario(id, nmbr, aplld, fechaTimestamp, email, contrsñ, nivelActl);

	        if (registrado) {
	          
	            return true;
	        } else {
	            JOptionPane.showMessageDialog(null, "Error al registrar el usuario.");
	            return false;
	        }

	    } catch (Exception ex) {
	        JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.");
	        ex.printStackTrace();
	        return false;
	    }
	}


/**
 * ------------------------------------------------------------Boton Perfil-------------------------------------------------------------------
 * */




	public DocumentSnapshot nvsDatos(String id, String nombre, String apellido, Timestamp fechaNacimiento, String contraseña) {
	    try {
	        Firestore db = Conexion.conectar();

	        Map<String, Object> datosActualizados = new HashMap<>();
	        datosActualizados.put("nombre", nombre);
	        datosActualizados.put("apellido", apellido);
	        datosActualizados.put("fechaNacimiento", fechaNacimiento);
	        datosActualizados.put("contraseña", contraseña);

	        db.collection("usuarios").document(id).update(datosActualizados).get();

	     
	        DocumentSnapshot actualizado = db.collection("usuarios").document(id).get().get();
	        return actualizado;

	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	
	/**
	 * ------------------------------------------------------------Historial-------------------------------------------------------------------
	 * */

	public List<Historial> obtenerHistorialWorkouts(String id) {
		// TODO Auto-generated method stub
		List<Historial> historialList = new ArrayList<>();
		   Firestore db;
		try {
			db = Conexion.conectar();
		
		
	    CollectionReference historialRef = db.collection("usuarios").document(id).collection("historial");

	    ApiFuture<QuerySnapshot> future = historialRef.get();
	    try {
	        List<QueryDocumentSnapshot> documentos = future.get().getDocuments();
	        for (QueryDocumentSnapshot doc : documentos) {
	            String workoutId = doc.getString("workoutId");
	            String nombreWorkout = doc.getString("nombreWorkout");
	            int nivel = doc.getLong("nivel").intValue();
	            String nivelTexto = doc.getString("nivelTexto");
	            LocalDate fecha = doc.getDate("fecha").toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	            int tiempoTotal = doc.getLong("tiempoTotal").intValue();
	            int porcentaje = doc.getLong("porcentajeCompletado").intValue();

	            Historial h = new Historial(workoutId, nombreWorkout, nivel, nivelTexto, fecha, tiempoTotal, porcentaje);
	            historialList.add(h);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    return historialList;
	}
	/**
	 * ------------------------------------------------------------Ejers-------------------------------------------------------------------
	 * */

	
		// TODO Auto-generated method stub
		public void mostrarEjerciciosPorWorkout(Workouts workoutSeleccionado, DefaultTableModel modeloEjers) {
		    modeloEjers.setRowCount(0);

		    if (workoutSeleccionado != null && workoutSeleccionado.getEjers() != null) {
		        for (Ejercicios ej : workoutSeleccionado.getEjers()) {
		            modeloEjers.addRow(new Object[] {
		                ej.getNombre(),
		                ej.getDescripcion(),
		                ej.getDescanso()
		            });
		        }
		    }
		}

		
		
		/*
		 *---------------------------------------------------------------------------BACKUP-----------------------------------------------------------------------------------------------------------------------------------
		 * */
	
		


		public ArrayList<String> recogerDatosUsu(DocumentSnapshot nvUsu) {
			// TODO Auto-generated method stub
			 ArrayList<String> datos = new ArrayList<>();

			    // 🔹 Extraer campos básicos
			    String nombre = nvUsu.getString("nombre");
			    String apellido = nvUsu.getString("apellido");
			    String email = nvUsu.getString("email");
			    String contraseña = nvUsu.getString("contraseña");

			    // 🔹 Fecha de nacimiento (convertida a texto legible)
			    String fechaTexto = "";
			    Object fchRecibida = nvUsu.get("fechaNacimiento");
			    if (fchRecibida instanceof com.google.cloud.Timestamp ts) {
			        fechaTexto = ts.toDate().toString(); // o formatear con SimpleDateFormat si prefieres
			    }

			    // 🔹 Nivel (si existe)
			    String nivelTexto = "";
			    Long nivel = nvUsu.getLong("nivel");
			    if (nivel != null) {
			        nivelTexto = String.valueOf(nivel);
			    }

			    // 🔹 Agregar al array
			    datos.add("Nombre: " + nombre);
			    datos.add("Apellido: " + apellido);
			    datos.add("Email: " + email);
			    datos.add("Contraseña: " + contraseña);
			    datos.add("Fecha de nacimiento: " + fechaTexto);
			    datos.add("Nivel: " + nivelTexto);

			    return datos;
			}

		public boolean guardarBackupUsuario(Usuario usuario, List<Workouts> workoutsDisp, ArrayList<Historial> historialList) {
			boolean exito = false;

		    try {
		        // 🔹 Guardar usuario
		        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("backups/usuarios.dat"))) {
		            output.writeObject(usuario);
		        }

		        // 🔹 Guardar workouts
		        escribirWorkoutsEnArchivo(workoutsDisp);

		        // 🔹 Guardar historial (si quieres serializarlo también)
		        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("backups/historial.dat"))) {
		            for (Historial h : historialList) {
		                output.writeObject(h);
		            }
		        }

		        exito = true;
		        System.out.println("✅ Backup generado correctamente.");

		    } catch (IOException e) {
		        System.err.println("❌ Error al generar backup:");
		        e.printStackTrace();
		    }

		    return exito;
			
			
		}

		public static void escribirWorkoutsEnArchivo(List<Workouts> workouts) {
		    String ruta = "backups/workouts.dat";

		    try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(ruta))) {
		        for (Workouts workout : workouts) {
		            output.writeObject(workout);
		        }
		        System.out.println("✅ Workouts guardados en: " + ruta);
		    } catch (IOException e) {
		        System.err.println("❌ Error al guardar workouts:");
		        e.printStackTrace();
		    }
		}

		public void actualizarNivelUsuario(String id, int nuevoNivel) {
			// TODO Auto-generated method stub
			  try {
				  Firestore db = Conexion.conectar();
			        db.collection("usuarios").document(id)
			          .update("nivel", nuevoNivel);
			    } catch (Exception e) {
			        e.printStackTrace();
			    }
		}


	

}
	


