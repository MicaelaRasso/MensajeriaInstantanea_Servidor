package modelo;

public class Usuario {
    private String nombre;
    private String address;
    private boolean isConnected = false;

    public Usuario() {
        this.nombre = "";
        this.address = "";
    }

    public Usuario(String nombre, String address) {
        this.nombre = nombre;
        this.address = address;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return nombre + "@" + address;
    }

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}
    
}
