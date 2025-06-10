package com.example.proyecto_gestortrabajadoresinformales; // Asegúrate de que el paquete sea correcto

import android.os.Parcel;
import android.os.Parcelable;

public class Propuesta implements Parcelable {
    private Integer id;
    private Integer usuarioId;
    private String titulo;
    private Double precio;
    private String descripcion;
    private Integer tipo_servicio;
    private Integer disponibilidad;
    private Integer calificacion;
    private String tipoServicioNombre; // Este campo sí lo estás usando, así que lo incluimos.

    public Propuesta() {
        // Constructor vacío
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

    // Constructor con tipoServicioNombre (útil para el adapter y la vista de detalle)
    public Propuesta(Integer id, Integer usuarioId, String titulo, Double precio, String descripcion, Integer tipo_servicio, Integer disponibilidad, Integer calificacion, String tipoServicioNombre) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.titulo = titulo;
        this.precio = precio;
        this.descripcion = descripcion;
        this.tipo_servicio = tipo_servicio;
        this.disponibilidad = disponibilidad;
        this.calificacion = calificacion;
        this.tipoServicioNombre = tipoServicioNombre;
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


    // Implementación de Parcelable

    // Constructor para Parcelable
    protected Propuesta(Parcel in) {
        // Los Integer y Double deben leerse con readInt() y readDouble(),
        // pero pueden ser null. Para manejar nullabilidad, readValue y writeValue son mejores.
        // O si sabes que nunca serán null, readInt() y readDouble() están bien.
        // Aquí asumiré que los IDs y valores numéricos no son null.
        id = (Integer) in.readValue(Integer.class.getClassLoader());
        usuarioId = (Integer) in.readValue(Integer.class.getClassLoader());
        titulo = in.readString();
        precio = (Double) in.readValue(Double.class.getClassLoader());
        descripcion = in.readString();
        tipo_servicio = (Integer) in.readValue(Integer.class.getClassLoader());
        disponibilidad = (Integer) in.readValue(Integer.class.getClassLoader());
        calificacion = (Integer) in.readValue(Integer.class.getClassLoader());
        tipoServicioNombre = in.readString();
    }

    // El CREATOR es necesario para Parcelable
    public static final Creator<Propuesta> CREATOR = new Creator<Propuesta>() {
        @Override
        public Propuesta createFromParcel(Parcel in) {
            return new Propuesta(in);
        }

        @Override
        public Propuesta[] newArray(int size) {
            return new Propuesta[size];
        }
    };

    @Override
    public int describeContents() {
        return 0; // Para objetos simples, 0 es suficiente.
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Escribir cada campo en el Parcel en el mismo orden en que se leen en el constructor
        dest.writeValue(id);
        dest.writeValue(usuarioId);
        dest.writeString(titulo);
        dest.writeValue(precio);
        dest.writeString(descripcion);
        dest.writeValue(tipo_servicio);
        dest.writeValue(disponibilidad);
        dest.writeValue(calificacion);
        dest.writeString(tipoServicioNombre);
    }
}