package modelo;

public class JsonConverter {

    /**
     * Convierte un JSON en un objeto Request.
     */
    public static Request fromJson(String json) {
        Request req = new Request();
        req.setOperacion(extractValue(json, "operacion"));

        String emisorBlock = extractBlock(json, "emisor");
        Usuario em = new Usuario();
        em.setNombre(extractValue(emisorBlock, "nombre"));
        em.setIP(extractValue(emisorBlock, "ip"));
        em.setPuerto(Integer.parseInt(extractValue(emisorBlock, "puerto")));
        req.setEmisor(em);

        String receptorBlock = extractBlock(json, "receptor");
        Usuario rec = new Usuario();
        rec.setNombre(extractValue(receptorBlock, "nombre"));
        // ip y puerto quedan nulos/0 si no existen
        req.setReceptor(rec);

        req.setContenido(extractValue(json, "contenido"));
        req.setFechaYHora(extractValue(json, "fechaYHora"));
        return req;
    }

    /**
     * Convierte un objeto Request en su representación JSON.
     */
    public static String toJson(Request request) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"operacion\":\"")
          .append(request.getOperacion()).append("\",");

        // Emisor
        sb.append("\"emisor\":{");
        sb.append("\"nombre\":\"")
          .append(request.getEmisor().getNombre()).append("\",");
        sb.append("\"ip\":\"")
          .append(request.getEmisor().getIP()).append("\",");
        sb.append("\"puerto\":")
          .append(request.getEmisor().getPuerto());
        sb.append("},");

        // Receptor
        sb.append("\"receptor\":{");
        sb.append("\"nombre\":\"")
          .append(request.getReceptor().getNombre()).append("\"");
        sb.append("},");

        sb.append("\"contenido\":\"")
          .append(request.getContenido()).append("\",");
        sb.append("\"fechaYHora\":\"")
          .append(request.getFechaYHora()).append("\"");

        sb.append("}");
        return sb.toString();
    }

    // ------------------ Métodos auxiliares ------------------

    /**
     * Extrae el bloque JSON para una clave con objeto anidado.
     */
    private static String extractBlock(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\\{";
        int start = json.indexOf(key + "");
        if (start < 0) return "";
        int braceOpen = json.indexOf('{', start);
        int depth = 0;
        for (int i = braceOpen; i < json.length(); i++) {
            if (json.charAt(i) == '{') depth++;
            else if (json.charAt(i) == '}') depth--;
            if (depth == 0) {
                return json.substring(braceOpen + 1, i);
            }
        }
        return "";
    }

    /**
     * Extrae el valor de cadena para una clave dada.
     */
    private static String extractValue(String json, String key) {
        String quotedKey = "\"" + key + "\"";
        int idx = json.indexOf(quotedKey);
        if (idx < 0) return null;
        int colon = json.indexOf(':', idx);
        int start;
        // ¿Valor entre comillas? o número.
        if (json.charAt(colon + 1) == '"') {
            start = colon + 2;
            int end = json.indexOf('"', start);
            return json.substring(start, end);
        } else {
            start = colon + 1;
            int end = start;
            while (end < json.length() &&
                   (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) {
                end++;
            }
            return json.substring(start, end).trim();
        }
    }
}