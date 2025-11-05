
package vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.google.cloud.firestore.DocumentSnapshot;
import modelo.Workouts;
import modelo.Ejercicios;
import modelo.Historial;
import modelo.Serie;

import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PanelHistorialWrkts extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable tableWrkts;

    public PanelHistorialWrkts(DocumentSnapshot usu, List<Historial> historialRealizado, List<Workouts> workoutsDisponibles) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1221, 741);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblTitulo = new JLabel("Historial Workouts");
        lblTitulo.setFont(new Font("Verdana", Font.BOLD, 35));
        lblTitulo.setBounds(393, 21, 384, 62);
        contentPane.add(lblTitulo);

        JButton btnAtras = new JButton("Atras");
        btnAtras.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		PanelWorkouts historial = new PanelWorkouts(usu, workoutsDisponibles);
        		historial.setVisible(true);
	                dispose(); 
        	}
        });
        btnAtras.setBounds(528, 628, 160, 38);
        contentPane.add(btnAtras);

        DefaultTableModel modeloHstrlWorkouts = new DefaultTableModel(
            new Object[][] {},
            new String[] { "Nombre Workout", "Nivel", "Tiempo Total", "Tiempo Previsto", "Fecha", "%Ejercicios Completados" }
        );

        tableWrkts = new JTable(modeloHstrlWorkouts);
        JScrollPane scrollWorkouts = new JScrollPane(tableWrkts);
        scrollWorkouts.setBounds(62, 82, 1082, 524);
        contentPane.add(scrollWorkouts);

        
        
        // Ordenar historial por fecha descendente
        ordenarHistorialPorFechaDesc(historialRealizado);
        
        
        

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Historial h : historialRealizado) {
            String nombre = h.getNombreWorkout();
            String nivelTexto = h.getNivelTexto();
            int tiempoTotal = h.getTiempoTotal();
            String fecha = h.getFecha().format(formatter);
            int porcentaje = h.getPorcentajeCompletado();

            // Buscar workout por ID para calcular tiempo previsto
            Workouts workout = buscarWorkoutPorId(workoutsDisponibles, h.getWorkoutId());
            int tiempoPrevisto = workout != null ? calcularTiempoPrevisto(workout) : 0;

            modeloHstrlWorkouts.addRow(new Object[] {
                nombre,
                nivelTexto,
                tiempoTotal + ":00",
                tiempoPrevisto + ":00",
                fecha,
                porcentaje + "%"
            });
        }
    }

    
    
    
    private void ordenarHistorialPorFechaDesc(List<Historial> lista) {
        for (int i = 0; i < lista.size() - 1; i++) {
            for (int j = i + 1; j < lista.size(); j++) {
                if (lista.get(i).getFecha().compareTo(lista.get(j).getFecha()) < 0) {
                    Historial temp = lista.get(i);
                    lista.set(i, lista.get(j));
                    lista.set(j, temp);
                }
            }
        }
    }

    
    
    private Workouts buscarWorkoutPorId(List<Workouts> lista, String id) {
        for (Workouts w : lista) {
            if (w.getId().equals(id)) {
                return w;
            }
        }
        return null;
    }
    
    

    private int calcularTiempoPrevisto(Workouts workout) {
        int total = 0;
        if (workout.getEjers() != null) {
            for (Ejercicios ejer : workout.getEjers()) {
                int descanso = ejer.getDescanso();
                if (ejer.getSeries() != null) {
                    for (Serie serie : ejer.getSeries()) {
                        total += serie.getDuracion() + descanso;
                    }
                }
            }
        }
        return total;
    }
    
    
}
