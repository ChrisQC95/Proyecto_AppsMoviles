package com.example.proyecto_gestortrabajadoresinformales.consultas;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.proyecto_gestortrabajadoresinformales.Conexion;
import com.example.proyecto_gestortrabajadoresinformales.beans.Distrito;
import com.example.proyecto_gestortrabajadoresinformales.beans.TipoServicio;

import java.util.ArrayList;
import java.util.List;

public class TipoServicioDAO {
    private Conexion conexion;

    public TipoServicioDAO(Conexion conexion) {
        this.conexion = conexion;
    }
    public List<TipoServicio> obtenerTodosLosTipoServicios() {
        List<TipoServicio> lista = new ArrayList<>();
        SQLiteDatabase db = conexion.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, nombre FROM tipo_servicio", null);

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                String nombre = cursor.getString(1);
                lista.add(new TipoServicio(id, nombre));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return lista;
    }

    public TipoServicio obtenerTipoServicioPorId(String idTipoServicio) {
        TipoServicio tipoServicio = null;
        SQLiteDatabase db = conexion.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + Conexion.TIPO_SERVICIO_ID + ", " + Conexion.TIPO_SERVICIO_NOMBRE +
                        " FROM " + Conexion.TABLE_TIPO_SERVICIO +
                        " WHERE " + Conexion.TIPO_SERVICIO_ID + " = ?",
                new String[]{String.valueOf(idTipoServicio)}
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndexOrThrow(Conexion.TIPO_SERVICIO_ID);
                int nombreIndex = cursor.getColumnIndexOrThrow(Conexion.TIPO_SERVICIO_NOMBRE);

                String id = cursor.getString(idIndex);
                String nombre = cursor.getString(nombreIndex);
                tipoServicio = new TipoServicio(id, nombre);
            }
            cursor.close();
        }
        // ELIMINADO: db.close();
        return tipoServicio;
    }
}