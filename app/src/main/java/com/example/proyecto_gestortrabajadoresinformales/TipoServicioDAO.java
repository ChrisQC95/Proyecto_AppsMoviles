package com.example.proyecto_gestortrabajadoresinformales; // Adjust the package

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.example.proyecto_gestortrabajadoresinformales.TipoServicio;

public class TipoServicioDAO {

    private Conexion adminDB;

    public TipoServicioDAO(Context context) {
        adminDB = new Conexion(context);
    }
    public List<TipoServicio> obtenerTodosLosTiposDeServicio() {
        List<TipoServicio> listaTipos = new ArrayList<>();
        SQLiteDatabase db = adminDB.getReadableDatabase();

        Cursor cursor = db.query(
                Conexion.TABLE_TIPO_SERVICIO, // The table to query
                null, // All columns
                null, // No WHERE clause
                null, // No WHERE clause arguments
                null, // No GROUP BY
                null, // No HAVING
                Conexion.TIPO_SERVICIO_NOMBRE + " ASC" // Order by name ascending
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.TIPO_SERVICIO_ID));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(Conexion.TIPO_SERVICIO_NOMBRE));

                TipoServicio tipoServicio = new TipoServicio(id, nombre);
                listaTipos.add(tipoServicio);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaTipos;
    }
}
