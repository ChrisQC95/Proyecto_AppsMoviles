package com.example.proyecto_gestortrabajadoresinformales.consultas;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.proyecto_gestortrabajadoresinformales.beans.Distrito;

import java.util.ArrayList;
import java.util.List;

public class DistritoDAO {
    private Conexion conexion;

    public DistritoDAO(Conexion conexion) {
        this.conexion = conexion;
    }

    public List<Distrito> obtenerTodosLosDistritos() {
        List<Distrito> lista = new ArrayList<>();
        SQLiteDatabase db = conexion.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, nombre FROM distrito", null);

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                String nombre = cursor.getString(1);
                lista.add(new Distrito(id, nombre));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }
    public Distrito obtenerDistritoPorId(String idDistrito) {
        Distrito distrito = null;
        SQLiteDatabase db = conexion.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + Conexion.DISTRITO_ID + ", " + Conexion.DISTRITO_NOMBRE +
                        " FROM " + Conexion.TABLE_DISTRITO +
                        " WHERE " + Conexion.DISTRITO_ID + " = ?",
                new String[]{String.valueOf(idDistrito)}
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndexOrThrow(Conexion.DISTRITO_ID);
                int nombreIndex = cursor.getColumnIndexOrThrow(Conexion.DISTRITO_NOMBRE);

                String id = cursor.getString(idIndex);
                String nombre = cursor.getString(nombreIndex);
                distrito = new Distrito(id, nombre);
            }
            cursor.close();
        }
        // ELIMINADO: db.close();
        return distrito;
    }
}

