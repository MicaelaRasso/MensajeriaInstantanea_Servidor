package modelo;

import java.time.LocalDateTime;

public class JsonConverter {

    public static Request fromJson(String json) {
    	
    	if(json.equals(""))
    		return null;
    	
        Request req = new Request();
        req.setOperacion(extractValue(json, "operacion"));

        String emisorBlock = extractBlock(json, "emisor");
        Usuario em = new Usuario();
        em.setNombre(extractValue(emisorBlock, "nombre"));
        req.setEmisor(em);

        String receptorBlock = extractBlock(json, "receptor");
        Usuario rec = new Usuario();
        rec.setNombre(extractValue(receptorBlock, "nombre"));
        req.setReceptor(rec);

        req.setContenido(extractValue(json, "contenido"));
        if (!extractValue(json, "fechaYHora").equals("null") || !extractValue(json, "fechaYHora").equals("")) {
        	LocalDateTime dateTime = LocalDateTime.parse(extractValue(json, "fechaYHora"));
        	req.setFechaYHora(dateTime);
		}
        return req;
    }

    public static String toJson(Request request) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"operacion\":\"").append(request.getOperacion()).append("\",");

        sb.append("\"emisor\":{");
        sb.append("\"nombre\":\"").append(request.getEmisor().getNombre()).append("\"},");

        sb.append("\"receptor\":{");
        sb.append("\"nombre\":\"").append(request.getReceptor().getNombre()).append("\"},");

        sb.append("\"contenido\":\"").append(request.getContenido()).append("\",");
        sb.append("\"fechaYHora\":\"").append(request.getFechaYHora()).append("\"");

        sb.append("}");
        return sb.toString();
    }

    private static String extractBlock(String json, String key) {
        int start = json.indexOf("\"" + key + "\"");
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

    private static String extractValue(String json, String key) {
        String quotedKey = "\"" + key + "\"";
        int idx = json.indexOf(quotedKey);
        if (idx < 0) return null;
        int colon = json.indexOf(':', idx);
        int start;
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