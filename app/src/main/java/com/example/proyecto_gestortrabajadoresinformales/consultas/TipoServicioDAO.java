package com.example.proyecto_gestortrabajadoresinformales.consultas;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.proyecto_gestortrabajadoresinformales.Conexion;
import com.example.proyecto_gestortrabajadoresinformales.beans.TipoServicio;

import java.util.ArrayList;
import java.util.List;

public class TipoServicioDAO {
    private Conexion conexion;

    public TipoServicioDAO(Context context) {
        conexion = new Conexion(context);
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
        db.close();
        return lista;
    }
}