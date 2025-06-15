package com.example.proyecto_gestortrabajadoresinformales;

public class Calificacion {
    private String nombreCliente;
    private String tipoTrabajo;
    private float puntuacion;
    private String comentario;

    // Constructor, getters y setters
    public Calificacion(String nombreCliente, String tipoTrabajo, float puntuacion, String comentario) {
        this.nombreCliente = nombreCliente;
        this.tipoTrabajo = tipoTrabajo;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
    }

    public String getNombreCliente() { return nombreCliente; }
    public String getTipoTrabajo() { return tipoTrabajo; }
    public float getPuntuacion() { return puntuacion; }
    public String getComentario() { return comentario; }
}
