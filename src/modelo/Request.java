package modelo;

class Request {
    private String operacion;
	private Usuario emisor;
    private Usuario receptor;
    private String contenido;
    private String fechaYHora;
    
    
    public Request() {
    }
    
    public String getOperacion() {
		return operacion;
	}


	public void setOperacion(String operacion) {
		this.operacion = operacion;
	}


	public Usuario getEmisor() {
		return emisor;
	}


	public void setEmisor(Usuario emisor) {
		this.emisor = emisor;
	}


	public Usuario getReceptor() {
		return receptor;
	}


	public void setReceptor(Usuario receptor) {
		this.receptor = receptor;
	}


	public String getContenido() {
		return contenido;
	}


	public void setContenido(String contenido) {
		this.contenido = contenido;
	}


	public String getFechaYHora() {
		return fechaYHora;
	}


	public void setFechaYHora(String fechaYHora) {
		this.fechaYHora = fechaYHora;
	}

}
