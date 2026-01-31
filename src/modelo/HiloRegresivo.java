package modelo;

import javax.swing.JLabel;

public class HiloRegresivo extends Thread{
	JLabel cronometroRegresivo;
	int minutos;
	int segundos;
	boolean terminar = false;
	boolean parar = false;
	String textoLabel;

	public HiloRegresivo(JLabel cronometroRegresivo, int segundos) {
		if (segundos >= 60) {
			this.minutos = transformarTiempo(segundos);
			this.segundos = segundos - (minutos * 60);
		} else {
			this.segundos = segundos;
		}

		this.cronometroRegresivo = cronometroRegresivo;
		textoLabel = cronometroRegresivo.getText().split(":")[0] + ": ";
	}

	private int transformarTiempo(int segundos) {

		return segundos / 60;
	}

	public void run() {
		while (!terminar) {
			try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

			while (parar) {
				try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
			}

			if (segundos == 0 && minutos == 0) {
				cronometroRegresivo.setText(textoLabel + "00:00");
				terminar();
				break; // <- salir del bucle inmediatamente
			}

			if (segundos == 0) {
				minutos--;
				segundos = 59;
			} else {
				segundos--;
			}

			String txt = textoLabel + minutos + ":" + (segundos < 10 ? "0" + segundos : segundos);
			cronometroRegresivo.setText(txt);
		}
	}

	public void terminar() {
		terminar = true;
	}

	public void cambiarEstado() {
		if (parar) {
			parar = false;
		} else {
			parar = true;
		}
	}
}


