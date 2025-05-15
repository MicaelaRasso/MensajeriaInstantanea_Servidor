package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class Servidor {
    private final int puerto;
    private ServerSocket serverSocket;
    private ConcurrentHashMap<String, Usuario> directorio = new ConcurrentHashMap<>();

    public Servidor(int puerto) {
        this.puerto = puerto;
    }

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
                        System.out.println("Nueva conexión desde " + socket.getInetAddress().getHostAddress() + socket.getPort());

                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        try {                        	
                        	String requestJson = in.readLine();
                            System.out.println(requestJson);
                            if (requestJson != null) {
                                procesaRequest(requestJson,socket);
                            }
                        }catch(IOException e){
                        	System.out.println("Se ha perdido la conexion con el cliente :  " + socket.getInetAddress().getHostAddress() + socket.getPort());
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(servidorRunnable).start();
    }

    public void procesaRequest(String requestJson, Socket socket) throws IOException {
        Request request = JsonConverter.fromJson(requestJson);
        String operacion = request.getOperacion();
        if (operacion.equals("registro")) {
            this.registrarUsuario(request.getEmisor(),socket);
        } else if (operacion.equals("consulta")) {
            this.consultarUsuario(request, requestJson);
        } else if (operacion.equals("mensaje")) {
            this.enviarMensaje(request, requestJson);
        }
    }

    public void registrarUsuario(Usuario usuario, Socket socket) throws IOException {
        String nombreUsuario = usuario.getNombre();
        if (existeUsuario(nombreUsuario)) {
            Usuario usuarioExistente = directorio.get(nombreUsuario);
            
            if (!usuarioExistente.isConectado()) {
                // El usuario existe pero estaba desconectado: lo reconectamos
                Cliente nuevoCliente = new Cliente(socket, this);
                nuevoCliente.setUsuario(usuarioExistente);
                nuevoCliente.start();
                usuarioExistente.setCliente(nuevoCliente);
                usuarioExistente.reconectarUsuario();
            } else {
                // El usuario ya estaba conectado: no permitimos conexión duplicada
                System.out.println("Nombre de usuario en uso: " + nombreUsuario);
                socket.close();
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println("0"); // "0" significa que el nombre está en uso
                return;
            }
        } else {
            // Usuario nuevo
            String ip = socket.getInetAddress().getHostAddress();
            int puertoCliente = socket.getPort();
            Cliente cliente = new Cliente(socket, this);
            Usuario u = new Usuario(nombreUsuario, ip, puertoCliente, cliente);
            directorio.put(nombreUsuario, u);
            cliente.start(); // Arranca el hilo del cliente
            System.out.println("Usuario registrado: " + nombreUsuario);
        }

    }

    public void consultarUsuario(Request request, String mensajeJSON) throws IOException {
    	String nombreUsuario = request.getContenido();
    	System.out.println("Consultando por usuario " + nombreUsuario);
        if (existeUsuario(nombreUsuario)) {
        	System.out.println("Usuario Encontrado avisando al cliente...");
        	System.out.println("Consulta: usuario " + nombreUsuario + " está en línea.");       	
        	this.directorio.get(request.getEmisor().getNombre()).getCliente().enviarMensaje(request,mensajeJSON);        	
        } else {
            System.out.println("Consulta: usuario " + nombreUsuario + " no encontrado.");
            request.setContenido("");
            String mensajeJSONnuevo = JsonConverter.toJson(request);
            this.directorio.get(request.getEmisor().getNombre()).getCliente().enviarMensaje(request,mensajeJSONnuevo);
        }
    }

    public void enviarMensaje(Request request, String mensajeJSON) {
        Usuario receptor = request.getReceptor();
        if (receptor != null) {
        	Usuario usuario = this.directorio.get(receptor.getNombre());
        	usuario.enviarMensaje(request,mensajeJSON);
        } else {
            System.out.println("Error: receptor no encontrado");
        }
    }
    
    public void terminar() throws IOException {
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    public static void main(String[] args) {
        int p = 5000;
        try {
            new Servidor(p).iniciar();
        } catch (IOException e) {
            System.out.println("Ocurrió un error y el servidor no puede iniciarse");
            e.printStackTrace();
        }
    }

    public Usuario getReceptorPorNombre(String receptor) {
        return this.directorio.get(receptor);
    }

    public Boolean existeUsuario(String nombreUsuario) {
        return this.directorio.containsKey(nombreUsuario);
    }
    
    public ConcurrentHashMap<String, Usuario> getDirectorio() {
		return directorio;
	}

}
