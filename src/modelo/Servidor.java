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
                        System.out.println("Nueva conexión desde " + socket.getInetAddress().getHostAddress());

                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String requestJson = in.readLine();
                        System.out.println(requestJson);
                        if (requestJson != null) {
                            procesaRequest(requestJson, socket);
                        } else {
                            socket.close();
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
            this.registrarUsuario(request.getEmisor(), socket);
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
                nuevoCliente.reconectarUsuario(usuarioExistente, nuevoCliente);
                nuevoCliente.start();
                System.out.println("Usuario reconectado: " + nombreUsuario);
            } else {
                // El usuario ya estaba conectado: no permitimos conexión duplicada
                System.out.println("Nombre de usuario en uso: " + nombreUsuario);
                socket.close();
                return;
            }
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("0"); // "0" significa que el nombre está en uso
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
        if (existeUsuario(nombreUsuario)) {
        	request.getEmisor().getCliente().enviarMensaje(request, mensajeJSON);
            System.out.println("Consulta: usuario " + nombreUsuario + " está en línea.");
        } else {
            System.out.println("Consulta: usuario " + nombreUsuario + " no encontrado.");
            request.setContenido("");
            String mensajeJSONnuevo = JsonConverter.toJson(request);
            request.getEmisor().getCliente().enviarMensaje(request, mensajeJSONnuevo);
        }
    }

    public void enviarMensaje(Request request, String mensajeJSON) {
        Usuario receptor = request.getReceptor();
        if (receptor != null) {
            receptor.getCliente().enviarMensaje(request, mensajeJSON);
        } else {
            System.out.println("Error: receptor no encontrado: " + request.getReceptor());
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

}
