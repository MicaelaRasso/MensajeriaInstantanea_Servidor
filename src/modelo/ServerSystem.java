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

    private final Map<String,Usuario> usuarios  = new ConcurrentHashMap<>();
    private final Map<String,List<String>> pendings = new ConcurrentHashMap<>();
    

    private ServerSystem() {}

    public static ServerSystem getInstance() {
        return INSTANCE;
    }

    /** Registro de un nuevo usuario */
    public synchronized String registrarUsuario(Request req,String header) {
        String nombre = req.getEmisor().getNombre();
        String address     = parseField(header,"ADDRESS");
        Usuario usuario = usuarios.get(nombre);
        String response;
        if(usuario != null && !usuario.isConnected()) {
        	usuario.setConnected(true);
        	System.out.println("[ServerSystem] Usuario reconectado: " + nombre + "@" + address);
        	response = "registrado";
        }else {
        	if(usuario == null) {
        		Usuario u = new Usuario(nombre,address);
        		usuarios.put(nombre, u);
        		System.out.println("[ServerSystem] Usuario registrado: " + nombre + "@" + address);
        		response = "registrado";
        	}else {
        		response = "en uso";
        		System.out.println("[ServerSystem] Usuario en uso: " + nombre);
        	}
        }
        pendings.putIfAbsent(nombre, new ArrayList<>());
        return response;
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
            resp.setContenido(buscado);
        } else {
            resp.setContenido("");
        }
        return resp;
    }

    /** Almacena un mensaje pendiente */
    public synchronized Request manejarMensaje(Request req) {
        String nombreReceptor = req.getReceptor().getNombre();
        String texto    = req.getContenido();
        Request resend = null;
        //pendings.computeIfAbsent(receptor, k -> new ArrayList<>()).add(texto);
        
        Usuario receptor = usuarios.get(nombreReceptor);
        if(receptor != null) {
        	if(receptor.isConnected()) {
        		resend = new Request();
        		resend.setContenido(texto);
        		resend.setEmisor(req.getEmisor());
        		resend.setReceptor(receptor);
        		resend.setFechaYHora(req.getFechaYHora());
        	}else{        		
        		System.out.println("[ServerSystem] Mensaje para " + nombreReceptor + " almacenado.");
        	}
        }
        
        return resend;
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
    
    /** Extrae valor tras "clave:valor" en un header separado por ';' */
    public String parseField(String header, String key) {
        for (String part : header.split(";")) {
            if (part.startsWith(key + ":")) {
                return part.substring(key.length()+1).trim();
            }
        }
        return "";
    }

	public Map<String, Usuario> getUsuarios() {
		return usuarios;
	}
    
}
