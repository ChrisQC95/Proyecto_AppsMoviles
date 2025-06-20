package com.example.proyecto_gestortrabajadoresinformales.consultas;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.proyecto_gestortrabajadoresinformales.beans.Perfil;

public class PerfilDAO {
    private Conexion conexion;

    public PerfilDAO(Conexion conexion) {
        this.conexion = conexion;
    }

    // Obtener perfil por ID de usuario
    public Perfil obtenerPerfilPorUsuarioId(String usuarioId) {
        SQLiteDatabase db = conexion.getReadableDatabase();
        Perfil perfil = null;

        Cursor cursor = db.rawQuery("SELECT * FROM perfil WHERE usuario_id = ?", new String[]{usuarioId});
        if (cursor.moveToFirst()) {
            perfil = new Perfil();
            perfil.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
            perfil.setUsuarioId(cursor.getString(cursor.getColumnIndexOrThrow("usuario_id")));
            perfil.setDistritoId(cursor.getString(cursor.getColumnIndexOrThrow("distrito_id")));
            perfil.setEspecialidad(cursor.getString(cursor.getColumnIndexOrThrow("especialidad")));
            perfil.setFotoPerfil(cursor.getString(cursor.getColumnIndexOrThrow("foto_perfil")));
        }
        cursor.close();
        db.close();
        return perfil;
    }

    // Insertar nuevo perfil
    public long insertarPerfil(Perfil perfil) {
        SQLiteDatabase db = conexion.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("usuario_id", perfil.getUsuarioId());
        values.put("distrito_id", perfil.getDistritoId());
        values.put("especialidad", perfil.getEspecialidad());
        values.put("foto_perfil", perfil.getFotoPerfil());

        long result = db.insert("perfil", null, values);
        db.close();
        return result;
    }

    // Actualizar perfil existente
    public int actualizarPerfil(Perfil perfil) {
        SQLiteDatabase db = conexion.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("distrito_id", perfil.getDistritoId());
        values.put("especialidad", perfil.getEspecialidad());
        values.put("foto_perfil", perfil.getFotoPerfil());

        int rows = db.update("perfil", values, "usuario_id = ?", new String[]{perfil.getUsuarioId()});
        db.close();
        return rows;
    }
}

