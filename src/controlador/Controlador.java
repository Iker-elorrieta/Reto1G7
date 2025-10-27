package controlador;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import conexion.Conexion;
import modelo.Ejercicios;
import modelo.Serie;
import modelo.Workouts;

public class Controlador {

	public boolean verificarUsu(String email, String contraseña) {
	    try {
	        Firestore db = Conexion.conectar();

	        ApiFuture<QuerySnapshot> future = db.collection("usuarios")
	            .whereEqualTo("email", email)
	            .whereEqualTo("contraseña", contraseña)
	            .get();

	        List<QueryDocumentSnapshot> cliente = future.get().getDocuments();

	        return cliente != null && !cliente.isEmpty();

	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	
	public boolean registrarUsuario(String id, String nmbr, String aplld, Date fechaNacmnt, String email, String contrsñ, int nivelActl) {
	    try {
	        Firestore db = Conexion.conectar();
	        Map<String, Object> datos = new HashMap<>();
	        datos.put("nombre", nmbr);
	        datos.put("apellido", aplld);
	        datos.put("fechaNacimiento", fechaNacmnt);
	        datos.put("email", email);
	        datos.put("contraseña", contrsñ);
	        datos.put("nivel", nivelActl);

	        //Comprobamos para que no haya usuarios o email identicos
	        QuerySnapshot snapshot = db.collection("usuarios")
	            .whereEqualTo("email", email)
	            .get()
	            .get();

	        if (!snapshot.isEmpty()) {
	            JOptionPane.showMessageDialog(null, "Ya existe un usuario con ese email.");
	            return false; 
	        } else {
	          
	        	//Utilizamos ApiFuture y el set en vez del add para crear los nombres del id nosotros
	            ApiFuture<WriteResult> usuario = db.collection("usuarios").document(id).set(datos);

	            usuario.get();
	            return true;
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	
	public void mostrarWorkoutsPorNivel(int nivelSeleccionado, DefaultTableModel modelo, List<Workouts> todosLosWorkouts) {
		// TODO Auto-generated method stub
		   modelo.setRowCount(0);
	        for (Workouts w : todosLosWorkouts) {
	            if (w.getNivel() <= nivelSeleccionado) {
	                modelo.addRow(new Object[] {
	                    w.getNombre(),
	                    w.getNivel(),
	                    w.getNumEjers(),
	                    w.getVideoUrl()
	                });
	            }
	        }
	}

	 //Carga workouts completos desde Firestore
	public List<Workouts> cargarWorkoutsDesdeFirestore(Firestore db) {
		// TODO Auto-generated method stub
		   List<Workouts> workoutsDisp = new ArrayList<>();
	        try {
	            List<QueryDocumentSnapshot> docsWorkouts = db.collection("workouts").get().get().getDocuments();

	            for (QueryDocumentSnapshot doc : docsWorkouts) {
	                Workouts w = new Workouts(
	                    doc.getId(),
	                    doc.getLong("numEjercicios").intValue(),
	                    doc.getLong("nivel").intValue(),
	                    doc.getString("nombre"),
	                    doc.getString("videoURL")
	                );

	                // 🔹 Cargar ejercicios
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
	                            s.getLong("repeticiones").intValue(),
	                            s.getLong("duracion").intValue(),
	                            s.getString("foto")
	                        );
	                        series.add(serie);
	                    }

	                    Ejercicios ejercicio = new Ejercicios(
	                        ej.getString("nombre"),
	                        ej.getString("descripcion"),
	                        ej.getLong("orden").intValue(),
	                        ej.getLong("descanso").intValue(),
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
}
