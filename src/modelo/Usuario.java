package modelo;

public class Usuario {
    private String nombre;
    private String IP;

    public Usuario() {
        this.nombre = "";
        this.IP = "";
    }

    public Usuario(String nombre, String IP) {
        this.nombre = nombre;
        this.IP     = IP;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    @Override
    public String toString() {
        return nombre + "@" + IP;
    }
}
