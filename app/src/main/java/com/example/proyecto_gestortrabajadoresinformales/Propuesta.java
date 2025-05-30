package com.example.proyecto_gestortrabajadoresinformales;

public class Propuesta {
    private Integer id;
    private Integer usuario_id;
    private String titulo;
    private Double precio;
    private String descripcion;
    private Integer tipo_servicio;
    private Integer disponibilidad;
    private Integer calificacion;
    private String tipoServicioNombre;
    public Propuesta() {
    }

    public Propuesta(Integer usuario_id, String titulo, Double precio, String descripcion, Integer tipo_servicio, Integer disponibilidad, Integer calificacion) {
        this.usuario_id = usuario_id;
        this.titulo = titulo;
        this.precio = precio;
        this.descripcion = descripcion;
        this.tipo_servicio = tipo_servicio;
        this.disponibilidad = disponibilidad;
        this.calificacion = calificacion;
    }

    public Propuesta(Integer id, Integer usuario_id, String titulo, Double precio, String descripcion, Integer tipo_servicio, Integer disponibilidad, Integer calificacion) {
        this.id = id;
        this.usuario_id = usuario_id;
        this.titulo = titulo;
        this.precio = precio;
        this.descripcion = descripcion;
        this.tipo_servicio = tipo_servicio;
        this.disponibilidad = disponibilidad;
        this.calificacion = calificacion;
    }

    public Propuesta(Integer id, Integer usuario_id, String titulo, Double precio, String descripcion, Integer tipo_servicio, Integer disponibilidad, Integer calificacion, String tipoServicioNombre) {
        this.id = id;
        this.usuario_id = usuario_id;
        this.titulo = titulo;
        this.precio = precio;
        this.descripcion = descripcion;
        this.tipo_servicio = tipo_servicio;
        this.disponibilidad = disponibilidad;
        this.calificacion = calificacion;
        this.tipoServicioNombre = tipoServicioNombre;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private Integer usuarioId;

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void setUsuario_id(Integer usuario_id) {
        this.usuario_id = usuario_id;
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
