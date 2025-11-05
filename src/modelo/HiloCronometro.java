package modelo;

import javax.swing.JLabel;

public class HiloCronometro extends Thread {
	JLabel cronometro;
	int minutos;
	int segundos;
	boolean terminar = false;
	boolean parar = false;
	String textoLabel = "";

	public HiloCronometro(JLabel cronometro) {
		// TODO Auto-generated constructor stub
		this.cronometro = cronometro;
		textoLabel = cronometro.getText().split(":")[0] + ": ";
	}

	public void run() {
		while (!terminar) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (parar) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			segundos++;
			if (segundos == 60) {
				minutos++;
				segundos = 0;
			}
			if (segundos < 10 && minutos < 10) {
				cronometro.setText(textoLabel + 0 + minutos + ":" + 0 + segundos);
			} else if (segundos >= 10 && minutos < 10) {
				cronometro.setText(textoLabel + 0 + minutos + ":" + segundos);
			} else if (segundos < 10 && minutos >= 10) {
				cronometro.setText(textoLabel + minutos + ":" + 0 + segundos);
			} else {
				cronometro.setText(textoLabel  + minutos + ":" + segundos);
			}
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

	public JLabel getCronometro() {
		return cronometro;
	}

	public void setCronometro(JLabel cronometro) {
		this.cronometro = cronometro;
	}

	public int getMinutos() {
		return minutos;
	}

	public void setMinutos(int minutos) {
		this.minutos = minutos;
	}

	public int getSegundos() {
		return segundos;
	}

	public void setSegundos(int segundos) {
		this.segundos = segundos;
	}
}

