package com.example.proyecto_gestortrabajadoresinformales.beans;

public class Solicitud {
    private Integer id; // AUTOINCREMENT, puede ser null al insertar
    private Integer usuarioId; // ID del cliente que hace la solicitud
    private Integer propuestaId; // ID de la propuesta a la que se hace la solicitud
    private String mensaje;
    private String estado; // Por defecto 'ENVIADA'

    public Solicitud() {
    }

    // Constructor para insertar una nueva solicitud
    public Solicitud(Integer usuarioId, Integer propuestaId, String mensaje) {
        this.usuarioId = usuarioId;
        this.propuestaId = propuestaId;
        this.mensaje = mensaje;
        // El estado por defecto se establece en la DB, no es necesario pasarlo aquí.
    }

    // Constructor completo (por ejemplo, para cargar desde la DB)
    public Solicitud(Integer id, Integer usuarioId, Integer propuestaId, String mensaje, String estado) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.propuestaId = propuestaId;
        this.mensaje = mensaje;
        this.estado = estado;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getPropuestaId() {
        return propuestaId;
    }

    public void setPropuestaId(Integer propuestaId) {
        this.propuestaId = propuestaId;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}