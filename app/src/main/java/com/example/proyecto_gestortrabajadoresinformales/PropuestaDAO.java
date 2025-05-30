package com.example.proyecto_gestortrabajadoresinformales;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class PropuestaDAO {
    private Conexion adminDB;

    public PropuestaDAO(Context context) {
        adminDB = new Conexion(context);
    }

    // Método original
    public List<Propuesta> obtenerPropuestasDisponibles() {
        List<Propuesta> listaPropuestas = new ArrayList<>();
        SQLiteDatabase db = adminDB.getReadableDatabase();

        String selection = Conexion.PROPUESTA_DISPONIBILIDAD + " = ?";
        String[] selectionArgs = { "1" };

        Cursor cursor = db.query(
                Conexion.TABLE_PROPUESTA,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_ID));
                int usuarioId = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_USUARIO_ID));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_TITULO));
                double precio = cursor.getDouble(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_PRECIO));
                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_DESCRIPCION));
                int tipoServicioId = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_TIPO_SERVICIO_ID));
                int disponibilidad = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_DISPONIBILIDAD));
                int calificacionPromedio = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_CALIFICACION_PROMEDIO));

                Propuesta propuesta = new Propuesta(id, usuarioId, titulo, precio, descripcion,
                        tipoServicioId, disponibilidad, calificacionPromedio);
                listaPropuestas.add(propuesta);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaPropuestas;
    }

    // Nuevo método agregado
    public List<Propuesta> obtenerPropuestasDisponiblesPorTipoServicio(int tipoServicioId) {
        List<Propuesta> listaPropuestas = new ArrayList<>();
        SQLiteDatabase db = adminDB.getReadableDatabase();

        String selection = Conexion.PROPUESTA_DISPONIBILIDAD + " = ? AND " + Conexion.PROPUESTA_TIPO_SERVICIO_ID + " = ?";
        String[] selectionArgs = { "1", String.valueOf(tipoServicioId) };

        Cursor cursor = db.query(
                Conexion.TABLE_PROPUESTA,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_ID));
                int usuarioId = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_USUARIO_ID));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_TITULO));
                double precio = cursor.getDouble(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_PRECIO));
                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_DESCRIPCION));
                int disponibilidad = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_DISPONIBILIDAD));
                int calificacionPromedio = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_CALIFICACION_PROMEDIO));

                Propuesta propuesta = new Propuesta(id, usuarioId, titulo, precio, descripcion,
                        tipoServicioId, disponibilidad, calificacionPromedio);
                listaPropuestas.add(propuesta);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaPropuestas;
    }

    // NUEVO MÉTODO AGREGADO - NO MODIFICA LO EXISTENTE
    public long insertarPropuesta(Propuesta propuesta) {
        SQLiteDatabase db = adminDB.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Conexion.PROPUESTA_USUARIO_ID, propuesta.getUsuarioId());
        values.put(Conexion.PROPUESTA_TITULO, propuesta.getTitulo());
        values.put(Conexion.PROPUESTA_PRECIO, propuesta.getPrecio());
        values.put(Conexion.PROPUESTA_DESCRIPCION, propuesta.getDescripcion());
        values.put(Conexion.PROPUESTA_TIPO_SERVICIO_ID, propuesta.getTipo_servicio());
        values.put(Conexion.PROPUESTA_DISPONIBILIDAD, propuesta.getDisponibilidad());
        values.put(Conexion.PROPUESTA_CALIFICACION_PROMEDIO, propuesta.getCalificacion());


        long id = db.insert(Conexion.TABLE_PROPUESTA, null, values);
        db.close();
        return id;
    }

    // MÉTODO ADICIONAL AGREGADO - insertar propuesta con parámetros separados (sin modificar otros métodos)
    public boolean insertarPropuesta(String titulo, String descripcion, int disponibilidad, String idTrabajador) {
        SQLiteDatabase db = adminDB.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Conexion.PROPUESTA_USUARIO_ID, Integer.parseInt(idTrabajador));
        values.put(Conexion.PROPUESTA_TITULO, titulo);
        values.put(Conexion.PROPUESTA_DESCRIPCION, descripcion);
        values.put(Conexion.PROPUESTA_DISPONIBILIDAD, disponibilidad);

        // Valores por defecto para los campos que no se reciben en este método
        values.put(Conexion.PROPUESTA_PRECIO, 0.0);
        values.put(Conexion.PROPUESTA_TIPO_SERVICIO_ID, 0);
        values.put(Conexion.PROPUESTA_CALIFICACION_PROMEDIO, 0);

        long id = db.insert(Conexion.TABLE_PROPUESTA, null, values);
        db.close();
        return id != -1;
    }

    // * MÉTODO QUE FALTA AGREGAR *

    public List<Propuesta> obtenerPropuestasPorUsuarioId(int usuarioId) {
        List<Propuesta> listaPropuestas = new ArrayList<>();
        SQLiteDatabase db = adminDB.getReadableDatabase();

        String selection = Conexion.PROPUESTA_USUARIO_ID + " = ?";
        String[] selectionArgs = { String.valueOf(usuarioId) };

        Cursor cursor = db.query(
                Conexion.TABLE_PROPUESTA,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_ID));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_TITULO));
                double precio = cursor.getDouble(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_PRECIO));
                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_DESCRIPCION));
                int tipoServicioId = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_TIPO_SERVICIO_ID));
                int disponibilidad = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_DISPONIBILIDAD));
                int calificacionPromedio = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_CALIFICACION_PROMEDIO));

                Propuesta propuesta = new Propuesta(id, usuarioId, titulo, precio, descripcion,
                        tipoServicioId, disponibilidad, calificacionPromedio);
                listaPropuestas.add(propuesta);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaPropuestas;
    }
    public long actualizarPropuesta(Propuesta propuesta) {
        SQLiteDatabase db = adminDB.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Conexion.PROPUESTA_TITULO, propuesta.getTitulo());
        values.put(Conexion.PROPUESTA_PRECIO, propuesta.getPrecio());
        values.put(Conexion.PROPUESTA_DESCRIPCION, propuesta.getDescripcion());
        values.put(Conexion.PROPUESTA_DISPONIBILIDAD, propuesta.getDisponibilidad());
        String whereClause = Conexion.PROPUESTA_ID + " = ?";
        String[] whereArgs = { String.valueOf(propuesta.getId()) };

        long resultado = db.update(Conexion.TABLE_PROPUESTA, values, whereClause, whereArgs);
        db.close();
        return resultado;
    }
}