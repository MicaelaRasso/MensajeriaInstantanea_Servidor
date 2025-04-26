package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Cliente extends Thread {
    private Socket socket;
    private Servidor servidor;
    private PrintWriter out;

    public Cliente(Socket socket, Servidor servidor) {
        this.socket = socket;
        this.servidor = servidor;
    }


    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
        	try {
        	    String mensaje = in.readLine(); // Lee solo un mensaje
        	    if (mensaje != null && !"exit".equalsIgnoreCase(mensaje)) {
        	        //System.out.println("Recibiendo mensaje: " + message);
        	        Request request = JsonConverter.fromJson(mensaje);
        	        String op = request.getOperacion();
        	        if(op.equals("mensaje")) {
        	        	//crea un nuevo objeto mensaje
        	        	this.enviarMensaje(request,mensaje); // <-- tendria que seleccionar el tipo de operacion
        	        }else if(op.equals("registro")){
        	        	//registra el usuario
        	        }else if(op.equals("consulta")) {
        	        	//consulta si existe el 
        	        }
        	    }
        	} catch (Exception e) {
        	    e.printStackTrace();
        	}

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close(); // Asegurarse de cerrar el socket
                System.out.println("Conexion cerrada.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void conectar(Usuario usuario) throws IOException {
        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Conectado con " + usuario.getIP() + ":" + usuario.getPuerto());
        } catch (IOException e) {
            System.err.println("Error al conectar con " + usuario.getIP() + ":" + usuario.getPuerto() + " - " + e.getMessage());
            throw e;
        }
    }
    
    private void enviarMensaje(Mensaje mensaje,String mensajeJSON) throws IOException {
    	//tengo que chequear si el usuario receptor esta conectado
    	Usuario receptor;
    	receptor = this.servidor.getReceptorPorNombre(mensaje.getReceptor());
    	if(receptor == null) {
    		throw new IOException("No existe el receptor para este mensaje en el directorio");
    	}
    	
    	//aca deberia traer los datos del usuario receptor
    	
        try {
            conectar(receptor); // Intentar reconectar si el socket está cerrado
        } catch (IOException e) {
        	receptor.getMensajesAlmacenados().add(mensaje);
            //throw new IOException("No se pudo reconectar con " + receptor.getIP() + ":" + receptor.getPuerto(), e);
        }
        

        if (out == null) {
        	receptor.getMensajesAlmacenados().add(mensaje);
            throw new IOException("No hay conexión establecida con " + receptor.getIP() + ":" + receptor.getPuerto());
        }

        out.println(mensajeJSON);
        out.flush();
        System.out.println("Enviando mensaje: " + mensaje);
        System.out.println("Mensaje enviado a " + receptor.getIP() + ":" + receptor.getPuerto());
        cerrarConexion(receptor);
    }
    
    public void cerrarConexion(Usuario usuario) {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Conexión cerrada con " + usuario.getIP() + ":" + usuario.getPuerto());
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar la conexión con " + usuario.getIP()+ ":" + usuario.getPuerto());
        }
    }
    
}
