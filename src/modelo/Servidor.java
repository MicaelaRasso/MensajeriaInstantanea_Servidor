package modelo;

import java.io.*;
import java.net.*;

public class Servidor {
    private final int puerto;
    private ServerSocket serverSocket;

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

        @Override
        public void run() {
            try {
                in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String header;
                while ((header = in.readLine()) != null) {
                    // header: "OPERACION:xxx;USER:yyy"
                    String payload = in.readLine();      // JSON del Request
                    Request req   = JsonConverter.fromJson(payload);

                    ServerSystem sys = ServerSystem.getInstance();
                    switch (req.getOperacion()) {
                        case "registro":
                            sys.registrarUsuario(req);
                            out.println("ACK");
                            break;
                        case "consulta":
                            Request resp = sys.manejarConsulta(req);
                            out.println("ACK");
                            out.println(JsonConverter.toJson(resp));
                            break;
                        case "mensaje":
                            sys.manejarMensaje(req);
                            out.println("ACK");
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
}
