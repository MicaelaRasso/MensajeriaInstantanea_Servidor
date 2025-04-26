package modelo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


public class Servidor {
	private final int puerto;
	private ServerSocket serverSocket;
	private HashMap<String, Usuario> directorio = new HashMap<String, Usuario>();
	    
	    
	public Servidor(int puerto) {
		this.puerto = puerto;
	}
	
	// Método para iniciar el servidor en un hilo separado
	public void iniciar() throws IOException {
		Servidor servidor = this;
	    Runnable servidorRunnable = new Runnable() {
	        @Override
	        public void run() {
	            try {	                    
	                serverSocket = new ServerSocket(puerto);
	                System.out.println("Servidor escuchando en el puerto " + puerto);
	
	                while (true) {
	                    Socket socket = serverSocket.accept();
	                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	                    
	                    String conexion = in.readLine();
	                    String[] partes = conexion.split("//");
	                    
	                    String nombreUsuario = partes[0];
	                    String IP = partes[1];
	                    String p = partes[2];
	                    int puerto = Integer.parseInt(p);
	                    
	                    
	                    if (directorio.containsKey(nombreUsuario)) {
	                        System.out.println("Nombre de usuario en uso: " + nombreUsuario);
	                        out.println("0");
	                        socket.close();
	                        continue;
	                    }else {
		                    Cliente cliente = new Cliente(socket, servidor);
		                    Usuario u = new Usuario(nombreUsuario, IP, puerto, cliente);
		                    directorio.put(nombreUsuario, u);
		                    cliente.start();
	                    	
	                    }
	                }
	
	            } catch (IOException e) {
	                try {
	                    throw e;
	                } catch (IOException ex) {
	                    ex.printStackTrace();
	                }
	            }
	        }
	    };
	    new Thread(servidorRunnable).start();
	}
	
	
	    
	public void terminar() throws IOException {
	    if (serverSocket != null) {
	        serverSocket.close(); // Cerrar el ServerSocket cuando el servidor termine
	    }
	}
	    
	
	public static void main(String[] args) {
		int p = 5000;
		try {
			new Servidor(p).iniciar();
		} catch (IOException e) {
			System.out.println("Ocurrio un error y el servidor no puede iniciarse");
				e.printStackTrace();
			}
		}

	public Usuario getReceptorPorNombre(String receptor) {
	
		return this.directorio.get(receptor);
	}
	
	}
