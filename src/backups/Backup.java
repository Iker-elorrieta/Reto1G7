package backups;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import modelo.Usuario;
import modelo.Workouts;

public class Backup {

	public  String generarBackups( List<Workouts> workouts, List<Usuario> usuarios) {
	    boolean crrct = false;

	    try {
	    	guardarEnDat("backups/workouts.dat", workouts);
	    	guardarEnDat("backups/usuarios.dat", usuarios);
	    	 crrct = true;
	    } catch (Exception e) {
	        e.printStackTrace();
	       
	    }

	    if (crrct) {
	        return "Backup Guardado.";
	    } else {
	        return "Error en el prceso de Backups";
	    }
	}

	 private void guardarEnDat(String ruta, Object objeto) throws IOException {
	        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta))) {
	            oos.writeObject(objeto);
	        }
	    }
	  
}
