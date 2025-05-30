package com.example.proyecto_gestortrabajadoresinformales;

public class Propuesta {
    private Integer id;
    private Integer usuarioId;  // usar solo este
    private String titulo;
    private Double precio;
    private String descripcion;
    private Integer tipo_servicio;
    private Integer disponibilidad;
    private Integer calificacion;
    private String tipoServicioNombre;

    public Propuesta() {
    }

    public Propuesta(Integer usuarioId, String titulo, Double precio, String descripcion, Integer tipo_servicio, Integer disponibilidad, Integer calificacion) {
        this.usuarioId = usuarioId;
        this.titulo = titulo;
        this.precio = precio;
        this.descripcion = descripcion;
        this.tipo_servicio = tipo_servicio;
        this.disponibilidad = disponibilidad;
        this.calificacion = calificacion;
    }

    public Propuesta(Integer id, Integer usuarioId, String titulo, Double precio, String descripcion, Integer tipo_servicio, Integer disponibilidad, Integer calificacion) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.titulo = titulo;
        this.precio = precio;
        this.descripcion = descripcion;
        this.tipo_servicio = tipo_servicio;
        this.disponibilidad = disponibilidad;
        this.calificacion = calificacion;
    }

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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getTipo_servicio() {
        return tipo_servicio;
    }

    public void setTipo_servicio(Integer tipo_servicio) {
        this.tipo_servicio = tipo_servicio;
    }

    public Integer getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(Integer disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public Integer getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Integer calificacion) {
        this.calificacion = calificacion;
    }

    public String getTipoServicioNombre() {
        return tipoServicioNombre;
    }

    public void setTipoServicioNombre(String tipoServicioNombre) {
        this.tipoServicioNombre = tipoServicioNombre;
    }

    @Override
    public String toString() {
        return "Propuesta{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", precio=" + precio +
                ", disponibilidad=" + disponibilidad +
                '}';
    }
}