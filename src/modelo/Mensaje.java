package modelo;

import java.time.LocalDateTime;

public class Mensaje {
	private Usuario emisor;
	private Usuario receptor;
	private String contenido;
	private LocalDateTime fechaYHora;


	public Mensaje(Usuario emisor, Usuario receptor, String contenido,  LocalDateTime fechaYHora) {
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

	public Usuario getEmisor() {
		return emisor;
	}

	public Usuario getReceptor() {
		return receptor;
	}

	@Override
	public String toString() {
		String salto = System.lineSeparator();
		return emisor.toString() + salto + contenido + salto + fechaYHora + salto + receptor.toString();
	}
	
	public String paraMostrar() {
		String salto = System.lineSeparator();
		return emisor.getNombre() + ": " + salto + contenido + salto + fechaYHora;
	}

}
