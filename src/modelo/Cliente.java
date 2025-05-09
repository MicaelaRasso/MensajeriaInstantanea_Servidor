package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

class Cliente extends Thread {
    private Socket socket;
	private Servidor servidor;
    private PrintWriter out;

    public Cliente(Socket socket, Servidor servidor) {
        this.socket = socket;
        this.servidor = servidor;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            this.out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("entra ");

            String mensaje;
            while ((mensaje = in.readLine()) != null) {
                Request request = JsonConverter.fromJson(mensaje);
                String op = request.getOperacion();

                if (op.equals("mensaje")) {
                    this.enviarMensaje(request, mensaje);
                }
            }

        } catch (IOException e) {
        	System.out.println("El usuario se ha desconectado");
            cerrarConexion();
        }
    }

    public void enviarMensaje(Request request, String mensajeJSON) {
        Usuario receptor = this.servidor.getDirectorio().get(request.getReceptor().getNombre());
        String nombreReceptor = receptor.getNombre();

        if (this.socket == null || this.socket.isClosed()) {
            desconectarUsuario(receptor);
            receptor.getMensajesAlmacenados().add(new Mensaje(
                    request.getEmisor().getNombre(),
                    request.getReceptor().getNombre(),
                    request.getContenido(),
                    request.getFechaYHora()
            ));
            System.out.println("Receptor no conectado, mensaje almacenado.");
            return;
        } else {
			this.out.println(mensajeJSON);
			System.out.println(this.socket.getPort());
	        System.out.println("Mensaje enviado a " + nombreReceptor);
		}       
    }
 

    private void cerrarConexion() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar la conexión");
        }
    }
    
    public void desconectarUsuario(Usuario receptor) {
        receptor.setConectado(false);
        receptor.setCliente(null);
        System.out.println("Usuario desconectado: " + receptor.getNombre());
    }
    
    public void reconectarUsuario(Usuario usuario, Cliente nuevoCliente) {
        usuario.setConectado(true);
        System.out.println("Usuario reconectado: " + usuario.getNombre());

        // Enviar los mensajes almacenados
        enviarMensajesAlmacenados(usuario);
    }

    public void enviarMensajesAlmacenados(Usuario usuario) {
        ArrayList<Mensaje> mensajesPendientes = usuario.getMensajesAlmacenados();
        
        if (mensajesPendientes.isEmpty()) {
            return;
        }

        PrintWriter out = usuario.getCliente().getOut();
        if (out == null) {
            System.out.println("No se puede enviar mensajes almacenados a " + usuario.getNombre() + " porque no tiene salida activa.");
            return;
        }

        for (Mensaje mensaje : mensajesPendientes) {
            Request request = new Request();
            Usuario emisor = new Usuario();
            emisor.setNombre(mensaje.getEmisor());

            request.setOperacion("mensaje");
            request.setEmisor(emisor);
            request.setReceptor(usuario);
            request.setContenido(mensaje.getContenido());
            request.setFechaYHora(mensaje.getFechaYHora());

            String mensajeJson = JsonConverter.toJson(request);
            enviarMensaje(request, mensajeJson);
        }

        System.out.println("Se enviaron " + mensajesPendientes.size() + " mensajes almacenados a " + usuario.getNombre());

        // Una vez enviados, vaciamos la lista
        mensajesPendientes.clear();
    }
    
    public PrintWriter getOut() {
        return out;
    }

    public Socket getSocket() {
		return socket;
	}
}
