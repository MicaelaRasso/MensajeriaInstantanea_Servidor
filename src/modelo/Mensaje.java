package modelo;

import java.time.LocalDateTime;

public class Mensaje {
	private String emisor;
	private String receptor;
	private String contenido;
	private LocalDateTime fechaYHora;


	public Mensaje(String emisor, String receptor, String contenido,  LocalDateTime fechaYHora) {
		super();
		this.emisor = emisor;
		this.receptor = receptor;
		this.contenido = contenido;
		this.fechaYHora = fechaYHora;
	}

	public String getContenido() {
		return contenido;
	}

	public LocalDateTime getFechaYHora() {
		return fechaYHora;
	}

	public String getEmisor() {
		return emisor;
	}

	public String getReceptor() {
		return receptor;
	}

	@Override
	public String toString() {
		String salto = System.lineSeparator();
		return emisor.toString() + salto + contenido + salto + fechaYHora + salto + receptor.toString();
	}
	
	public String paraMostrar() {
		String salto = System.lineSeparator();
		return emisor + ": " + salto + contenido + salto + fechaYHora;
	}

}
