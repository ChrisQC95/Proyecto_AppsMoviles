package com.example.proyecto_gestortrabajadoresinformales.consultas;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.proyecto_gestortrabajadoresinformales.Conexion;
import com.example.proyecto_gestortrabajadoresinformales.beans.Usuario;

public class UsuarioDAO {
    private Conexion conexion;

    public UsuarioDAO(Conexion conexion) {
        this.conexion = conexion;
    }

    public boolean insertarUsuario(Usuario usuario) {
        SQLiteDatabase db = conexion.getWritableDatabase();

        if (db != null) {
            ContentValues values = new ContentValues();
            values.put("nombres", usuario.getNombres());
            values.put("apellidos", usuario.getApellidos());
            values.put("telefono", usuario.getTelefono());
            values.put("correo", usuario.getCorreo());
            values.put("contrasena", usuario.getContrasena());
            values.put("tipo_usuario", usuario.getTipoUsuario());

            long result = db.insert("usuario", null, values);
            db.close();
            return result != -1;
        }
        return false;
    }

    public Usuario obtenerUsuarioPorCredenciales(String correo, String contrasena) {
        SQLiteDatabase db = conexion.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM usuario WHERE correo = ? AND contrasena = ?",
                new String[]{correo, contrasena}
        );

        if (cursor.moveToFirst()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            String nombres = cursor.getString(cursor.getColumnIndexOrThrow("nombres"));
            String apellidos = cursor.getString(cursor.getColumnIndexOrThrow("apellidos"));
            String telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono"));
            String tipoUsuario = cursor.getString(cursor.getColumnIndexOrThrow("tipo_usuario"));
            cursor.close();
            return new Usuario(id, nombres, apellidos, telefono, correo, contrasena, tipoUsuario);
        }
        cursor.close();
        return null;
    }

    public String obtenerContrasenaPorCorreo(String correo) {
        SQLiteDatabase db = conexion.getReadableDatabase();
        String contrasena = null;

        Cursor cursor = db.rawQuery(
                "SELECT contrasena FROM usuario WHERE correo = ?",
                new String[]{correo}
        );

        if (cursor.moveToFirst()) {
            contrasena = cursor.getString(cursor.getColumnIndexOrThrow("contrasena"));
        }
        cursor.close();
        db.close();
        return contrasena;
    }

    public Usuario obtenerUsuarioPorId(String id) {
        Usuario usuario = null;
        SQLiteDatabase db = conexion.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuario WHERE id = ?", new String[]{String.valueOf(id)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                usuario = new Usuario();
                usuario.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                usuario.setNombres(cursor.getString(cursor.getColumnIndexOrThrow("nombres")));
                usuario.setApellidos(cursor.getString(cursor.getColumnIndexOrThrow("apellidos")));
                usuario.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow("telefono")));
                usuario.setCorreo(cursor.getString(cursor.getColumnIndexOrThrow("correo")));
                usuario.setContrasena(cursor.getString(cursor.getColumnIndexOrThrow("contrasena")));
                usuario.setTipoUsuario(cursor.getString(cursor.getColumnIndexOrThrow("tipo_usuario")));
            }
            cursor.close();
        }
        db.close();
        return usuario;
    }


    // Puedes agregar otros métodos como:
    // buscarUsuario(), actualizarUsuario(), eliminarUsuario(), listarUsuarios()...
}

