package modelo;
import java.util.ArrayList;

public class Usuario {
	private String nombre;
	private String IP;
	private int puerto;
	private boolean conectado;
	private ArrayList<Mensaje> mensajesAlmacenados = new ArrayList<Mensaje>();
	private Cliente cliente;
	
	public Usuario(String nombre, String IP, int puerto, Cliente cliente) {
		super();
		this.nombre = nombre;
		this.IP = IP;
		this.puerto = puerto;
		this.conectado = true;
		this.cliente = cliente;
	}

	public String getNombre() {
		return nombre;
	}

	public String getIP() {
		return IP;
	}

	public int getPuerto() {
		return puerto;
	}

	public boolean isConectado() {
		return conectado;
	}

	public ArrayList<Mensaje> getMensajesAlmacenados() {
		return mensajesAlmacenados;
	}

	@Override
	public String toString() {
		return "Usuario [nombre=" + nombre + ", IP=" + IP + ", puerto=" + puerto + "]";
	} 
	
	
	
}
