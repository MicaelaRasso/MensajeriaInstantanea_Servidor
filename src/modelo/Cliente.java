package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

class Cliente extends Thread {
    private Socket socket;
    private Servidor servidor;

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
        	        enviarMensaje(mensaje); 
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
    
    private void enviarMensaje(String mensaje) {
    	String[] m = mensaje.split("//");
    	if (m.length == 4) {
    		String nombreE = m[0]; nombreE.trim();
    		String nombreR = m[1]; nombreR.trim();
    		String contenido = m[2]; contenido.trim();
    		String fechaHora = m[3]; 
    		
    		//FALTAAAA
    		
    		
    	}else {
    		System.out.println("Error en el formato del mensaje");
    	}
    	
    	
    }
}
