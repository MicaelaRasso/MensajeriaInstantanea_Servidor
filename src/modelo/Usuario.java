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
	
	public Usuario() {}

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

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public void setPuerto(int puerto) {
		this.puerto = puerto;
	}

	public void setConectado(boolean conectado) {
		this.conectado = conectado;
	}

	public void setMensajesAlmacenados(ArrayList<Mensaje> mensajesAlmacenados) {
		this.mensajesAlmacenados = mensajesAlmacenados;
	}

	public void enviarMensaje(Request request, String mensajeJSON) {
		// TODO Auto-generated method stub
		if(cliente != null) {
			cliente.enviarMensaje(request, mensajeJSON);
		}else {
            this.conectado = false;
            this.getMensajesAlmacenados().add(new Mensaje(
                    request.getEmisor().getNombre(),
                    request.getReceptor().getNombre(),
                    request.getContenido(),
                    request.getFechaYHora()
            ));
            System.out.println("Receptor no conectado, mensaje almacenado.");
            return;
		}
	}
	
    public void reconectarUsuario() {
        setConectado(true);
        System.out.println("Usuario reconectado: " + getNombre());
    }
    /*
    public void desconectarUsuario() {
        setConectado(false);
        setCliente(null);
        System.out.println("Usuario desconectado: " + getNombre());
    }*/
}
