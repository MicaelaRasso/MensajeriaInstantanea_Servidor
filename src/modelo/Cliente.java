package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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

            String mensaje;
            while ((mensaje = in.readLine()) != null) {
                Request request = JsonConverter.fromJson(mensaje);
                String op = request.getOperacion();

                if (op.equals("mensaje")) {
                    this.enviarMensaje(request, mensaje);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cerrarConexion();
        }
    }

    public void enviarMensaje(Request request, String mensajeJSON) {
        Usuario receptor = request.getReceptor();
        String nombreReceptor = receptor.getNombre();

        Cliente clienteReceptor = receptor.getCliente();

        if (clienteReceptor == null || clienteReceptor.out == null) {
            // El receptor no tiene una conexión activa, almacenar mensaje
            receptor.getMensajesAlmacenados().add(new Mensaje(
                    request.getEmisor().getNombre(),
                    request.getReceptor().getNombre(),
                    request.getContenido(),
                    request.getFechaYHora()
            ));
            System.out.println("Receptor no conectado, mensaje almacenado.");
            return;
        }

        // Enviar el mensaje JSON al receptor
        out.println(mensajeJSON);
        out.flush();
        System.out.println("Mensaje enviado a " + nombreReceptor);
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
}
