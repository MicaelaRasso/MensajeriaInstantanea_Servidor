package modelo;

import java.io.*;
import java.net.*;

public class Servidor {
    private final int puerto;
    private ServerSocket serverSocket;
    private static final String PROXY_HOST = "127.0.0.1";
    private static final int PROXY_PORT = 60000;

    public Servidor(int puerto) {
        this.puerto = puerto;
    }

    public void iniciar() throws IOException {
        serverSocket = new ServerSocket(puerto);
        System.out.println("Servidor escuchando en el puerto " + puerto);

        while (true) {
            Socket socket = serverSocket.accept();
            new ClientHandler(socket).start();
        }
    }
    
    private void registrarServidor() throws IOException {
        Socket socket = new Socket(PROXY_HOST, PROXY_PORT);
        try {        	
        	BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        	PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        	String header = "OPERACION:REGISTER;IP:127.0.0.1;PUERTO:5000";
        	String response;
        	out.println(header);
        	while ((response = in.readLine()) != null) {
        		System.out.println(response);
        		if(response.equals("RESPUESTA:ACK")) {
        			System.out.println("Conectado al Proxy en " + PROXY_HOST + ":" + PROXY_PORT);
        			this.iniciar();
        		}else {
        			System.out.println("No se ha podido registrar el servidor en el proxy");
        			socket.close();
        		}
        	}
        }catch(IOException e) {
        	System.out.println(e);
        	socket.close();
        }
    }
    

    public void terminar() throws IOException {
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }
        
        private void enviarMensaje(Request req,String address) throws IOException {
            Socket socket = new Socket(PROXY_HOST, PROXY_PORT);
            try {        	
            	BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            	PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            	String header = "OPERACION:SEND_MESSAGE;ADDRESS:" + address;
            	String body = JsonConverter.toJson(req);
            	String response;
            	out.println(header);
            	out.println(body);
            	while ((response = in.readLine()) != null) {
            		System.out.println(response);
            		if(response.equals("RESPUESTA:ACK")) {
            			System.out.println("Conectado al Proxy en " + PROXY_HOST + ":" + PROXY_PORT);
            			System.out.println("mensaje enviado");
            		}else {
            			System.out.println("No se ha podido registrar el servidor en el proxy");
            			socket.close();
            		}
            	}
            }catch(IOException e) {
            	System.out.println(e);
            	socket.close();
            }
        }

        @Override
        public void run() {
            try {
                in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String header;
                while ((header = in.readLine()) != null) {
                	System.out.println(header);
                    // header: "OPERACION:xxx;USER:yyy"
                    String payload = in.readLine();      // JSON del Request
                    Request req   = JsonConverter.fromJson(payload);
                    ServerSystem sys = ServerSystem.getInstance();
                    
                    if(req == null) {
                    	if(sys.parseField(header,"OPERACION").equals("DISCONNECT")) {
                    		String address = sys.parseField(header,"ADDRESS");
                    		System.out.println("desconectando usuario");
                    		if(!address.equals("")) {                    			
                    			for (Usuario usuario : sys.getUsuarios().values()) {
                    			    if(usuario.getAddress().equals(address)) {
                    			    	System.out.println("Usuario encontrado, desconectando");
                    			    	usuario.setConnected(false);
                    			    }
                    			}
                    		}
                    	}
                    	out.println("ACK");
                    	out.println("Desconexion confirmada");
                    }else {                    	
                    	String response;
                    	switch (req.getOperacion()) {
                    	case "registro":
                    		response = sys.registrarUsuario(req,header);
                    		out.println("ACK");
                    		out.println(response);
                    		break;
                    	case "consulta":
                    		Request resp = sys.manejarConsulta(req);
                    		out.println("ACK");
                    		out.println(JsonConverter.toJson(resp));
                    		break;
                    	case "mensaje":
                    		Request r = sys.manejarMensaje(req);
                    		if(r != null) {                    			
                    			enviarMensaje(r,r.getReceptor().getAddress());
                    		}
                    		out.println("ACK");
                    		out.println("enviado");
                    		break;
                    	case "heartbeat":
                    		sys.actualizarHeartbeat(req);
                    		out.println("ACK");
                    		break;
                    	default:
                    		System.err.println("Operación desconocida: " + req.getOperacion());
                    		out.println("ACK");
                    	}
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    System.out.println("[ClientHandler] Conexión cerrada con " +
                        socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    
    public static void main(String[] args) throws IOException {
 
		Servidor s = new Servidor(5000);
		s.registrarServidor();
        System.out.println("[SERVIDOR] Arrancando servidor en puerto " + s.puerto);
    }
}
