package com.example.proyecto_gestortrabajadoresinformales;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.proyecto_gestortrabajadoresinformales.Propuesta;

import java.util.ArrayList;
import java.util.List;

public class PropuestaDAO {
    private Conexion adminDB;
    public PropuestaDAO(Context context){
        adminDB = new Conexion(context);
    }
    public List<Propuesta> obtenerPropuestasDisponiblesPorTipoServicio(int tipoServicioId) {
        List<Propuesta> listaPropuestas = new ArrayList<>();
        SQLiteDatabase db = adminDB.getReadableDatabase();

        String query = "SELECT p." + Conexion.PROPUESTA_ID + ", " +
                "p." + Conexion.PROPUESTA_USUARIO_ID + ", " +
                "p." + Conexion.PROPUESTA_TITULO + ", " +
                "p." + Conexion.PROPUESTA_PRECIO + ", " +
                "p." + Conexion.PROPUESTA_DESCRIPCION + ", " +
                "p." + Conexion.PROPUESTA_TIPO_SERVICIO_ID + ", " +
                "p." + Conexion.PROPUESTA_DISPONIBILIDAD + ", " +
                "p." + Conexion.PROPUESTA_CALIFICACION_PROMEDIO + ", " +
                "ts." + Conexion.TIPO_SERVICIO_NOMBRE + " " +
                "FROM " + Conexion.TABLE_PROPUESTA + " p " +
                "INNER JOIN " + Conexion.TABLE_TIPO_SERVICIO + " ts ON p." + Conexion.PROPUESTA_TIPO_SERVICIO_ID + " = ts." + Conexion.TIPO_SERVICIO_ID + " " +
                "WHERE p." + Conexion.PROPUESTA_DISPONIBILIDAD + " = ? AND p." + Conexion.PROPUESTA_TIPO_SERVICIO_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{"1", String.valueOf(tipoServicioId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_ID));
                int usuarioId = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_USUARIO_ID));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_TITULO));
                double precio = cursor.getDouble(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_PRECIO));
                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_DESCRIPCION));
                int tipoServicioIdResultado = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_TIPO_SERVICIO_ID));
                int disponibilidad = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_DISPONIBILIDAD));
                int calificacionPromedio = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_CALIFICACION_PROMEDIO));
                String tipoServicioNombre = cursor.getString(cursor.getColumnIndexOrThrow(Conexion.TIPO_SERVICIO_NOMBRE));

                Propuesta propuesta = new Propuesta(id, usuarioId, titulo, precio, descripcion,
                        tipoServicioIdResultado, disponibilidad, calificacionPromedio, tipoServicioNombre);
                listaPropuestas.add(propuesta);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaPropuestas;
    }
    public List<Propuesta> obtenerPropuestasDisponibles() {
        List<Propuesta> listaPropuestas = new ArrayList<>();
        SQLiteDatabase db = adminDB.getReadableDatabase();

        String query = "SELECT p." + Conexion.PROPUESTA_ID + ", " +
                "p." + Conexion.PROPUESTA_USUARIO_ID + ", " +
                "p." + Conexion.PROPUESTA_TITULO + ", " +
                "p." + Conexion.PROPUESTA_PRECIO + ", " +
                "p." + Conexion.PROPUESTA_DESCRIPCION + ", " +
                "p." + Conexion.PROPUESTA_TIPO_SERVICIO_ID + ", " +
                "p." + Conexion.PROPUESTA_DISPONIBILIDAD + ", " +
                "p." + Conexion.PROPUESTA_CALIFICACION_PROMEDIO + ", " +
                "ts." + Conexion.TIPO_SERVICIO_NOMBRE + " " +
                "FROM " + Conexion.TABLE_PROPUESTA + " p " +
                "INNER JOIN " + Conexion.TABLE_TIPO_SERVICIO + " ts ON p." + Conexion.PROPUESTA_TIPO_SERVICIO_ID + " = ts." + Conexion.TIPO_SERVICIO_ID + " " +
                "WHERE p." + Conexion.PROPUESTA_DISPONIBILIDAD + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{"1"});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_ID));
                int usuarioId = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_USUARIO_ID));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_TITULO));
                double precio = cursor.getDouble(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_PRECIO));
                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_DESCRIPCION));
                int tipoServicioIdResultado = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_TIPO_SERVICIO_ID));
                int disponibilidad = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_DISPONIBILIDAD));
                int calificacionPromedio = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_CALIFICACION_PROMEDIO));
                String tipoServicioNombre = cursor.getString(cursor.getColumnIndexOrThrow(Conexion.TIPO_SERVICIO_NOMBRE));

                Propuesta propuesta = new Propuesta(id, usuarioId, titulo, precio, descripcion,
                        tipoServicioIdResultado, disponibilidad, calificacionPromedio, tipoServicioNombre);
                listaPropuestas.add(propuesta);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaPropuestas;
    }
}
