package modelo;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton que mantiene el estado del servidor:
 * - Usuarios registrados (nombre → IP)
 * - Mensajes pendientes para cada receptor
 * - Último heartbeat de cada servidor (si quisieras llevarlo)
 */
public class ServerSystem {
    private static final ServerSystem INSTANCE = new ServerSystem();

    private final Map<String,String> usuarios  = new ConcurrentHashMap<>();
    private final Map<String,List<String>> pendings = new ConcurrentHashMap<>();

    private ServerSystem() {}

    public static ServerSystem getInstance() {
        return INSTANCE;
    }

    /** Registro de un nuevo usuario */
    public synchronized void registrarUsuario(Request req) {
        String nombre = req.getEmisor().getNombre();
        String ip     = req.getEmisor().getIP();
        usuarios.put(nombre, ip);
        pendings.putIfAbsent(nombre, new ArrayList<>());
        System.out.println("[ServerSystem] Usuario registrado: " + nombre + "@" + ip);
    }

    /** Atiende una consulta de existencia de usuario */
    public Request manejarConsulta(Request req) {
        String buscado = req.getContenido();
        Request resp = new Request();
        resp.setOperacion("consultaResp");
        resp.setEmisor(new Usuario("SERVER", "0.0.0.0"));
        resp.setReceptor(req.getEmisor());
        resp.setFechaYHora(LocalDateTime.now());
        if (usuarios.containsKey(buscado)) {
            resp.setContenido("EXISTE");
        } else {
            resp.setContenido("NO_EXISTE");
        }
        return resp;
    }

    /** Almacena un mensaje pendiente */
    public synchronized void manejarMensaje(Request req) {
        String receptor = req.getReceptor().getNombre();
        String texto    = req.getContenido();
        pendings.computeIfAbsent(receptor, k -> new ArrayList<>()).add(texto);
        System.out.println("[ServerSystem] Mensaje para " + receptor + " almacenado.");
    }

    /** Devuelve y limpia la lista de pendientes de un usuario */
    public synchronized List<String> entregarPendientes(String usuario) {
        return pendings.remove(usuario);
    }

    /** Actualiza el timestamp de un heartbeat (solo logging) */
    public void actualizarHeartbeat(Request req) {
        System.out.println("[ServerSystem] Heartbeat recibido de "
            + req.getEmisor().getNombre() + " a las " + LocalDateTime.now());
    }
}
