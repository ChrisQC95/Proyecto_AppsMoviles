package com.example.proyecto_gestortrabajadoresinformales.beans;

public class Distrito {
    private String id;
    private String nombre;

    public Distrito(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre; // Esto es lo que se mostrará en el Spinner
    }
}
