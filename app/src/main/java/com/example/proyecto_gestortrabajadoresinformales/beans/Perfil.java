package com.example.proyecto_gestortrabajadoresinformales.beans;

public class Perfil {
    private String id;
    private String usuarioId;
    private String distritoId;
    private String especialidad;
    private String fotoPerfil;

    public Perfil(String id, String usuarioId, String distritoId, String especialidad, String fotoPerfil) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.distritoId = distritoId;
        this.especialidad = especialidad;
        this.fotoPerfil = fotoPerfil;
    }

    public Perfil() {
    }

    public Perfil(String usuarioId, String distritoId, String especialidad, String fotoPerfil) {
        this.usuarioId = usuarioId;
        this.distritoId = distritoId;
        this.especialidad = especialidad;
        this.fotoPerfil = fotoPerfil;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getDistritoId() {
        return distritoId;
    }

    public void setDistritoId(String distritoId) {
        this.distritoId = distritoId;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }
}
