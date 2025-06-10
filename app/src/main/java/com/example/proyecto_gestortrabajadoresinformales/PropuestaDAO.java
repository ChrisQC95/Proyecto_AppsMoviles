package com.example.proyecto_gestortrabajadoresinformales;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class PropuestaDAO {
    private Conexion adminDB;


    public PropuestaDAO(Conexion conexion) {
        this.adminDB = conexion;
    }
    // Método original (no modificado)
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

    // Método para obtener propuestas por tipo de servicio (no modificado)
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
                // El tipoServicioId ya lo tenemos por el filtro
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

    // MÉTODO MODIFICADO: Obtener propuestas disponibles filtradas por Tipo de Servicio, Distrito Y/O Calificación
    public List<Propuesta> obtenerPropuestasDisponiblesFiltradas(String tipoServicioId, String distritoId, int calificacionMinima) {
        List<Propuesta> listaPropuestas = new ArrayList<>();
        // Obtener la base de datos legible
        SQLiteDatabase db = adminDB.getReadableDatabase(); // Mantener esta línea

        StringBuilder query = new StringBuilder();
        query.append("SELECT P.* FROM ")
                .append(Conexion.TABLE_PROPUESTA).append(" P ")
                .append(" INNER JOIN ").append(Conexion.TABLE_USUARIO).append(" U ON P.").append(Conexion.PROPUESTA_USUARIO_ID).append(" = U.").append(Conexion.USUARIO_ID)
                .append(" INNER JOIN ").append(Conexion.TABLE_PERFIL).append(" PER ON U.").append(Conexion.USUARIO_ID).append(" = PER.").append(Conexion.PERFIL_USUARIO_ID)
                .append(" WHERE P.").append(Conexion.PROPUESTA_DISPONIBILIDAD).append(" = 1");

        List<String> selectionArgsList = new ArrayList<>();

        if (tipoServicioId != null && !tipoServicioId.equals("-1")) {
            query.append(" AND P.").append(Conexion.PROPUESTA_TIPO_SERVICIO_ID).append(" = ?");
            selectionArgsList.add(tipoServicioId);
        }

        if (distritoId != null && !distritoId.equals("-1")) {
            query.append(" AND PER.").append(Conexion.PERFIL_DISTRITO_ID).append(" = ?");
            selectionArgsList.add(distritoId);
        }

        if (calificacionMinima > 0) {
            query.append(" AND P.").append(Conexion.PROPUESTA_CALIFICACION_PROMEDIO).append(" >= ?");
            selectionArgsList.add(String.valueOf(calificacionMinima));
        }

        String[] selectionArgs = selectionArgsList.toArray(new String[0]);
        Cursor cursor = null; // Inicializar cursor a null para el bloque finally

        try {
            cursor = db.rawQuery(query.toString(), selectionArgs);

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_ID));
                    int usuarioId = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_USUARIO_ID));
                    String titulo = cursor.getString(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_TITULO));
                    double precio = cursor.getDouble(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_PRECIO));
                    String descripcion = cursor.getString(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_DESCRIPCION));
                    int tipoServicio = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_TIPO_SERVICIO_ID));
                    int disponibilidad = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_DISPONIBILIDAD));
                    int calificacionPromedio = cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_CALIFICACION_PROMEDIO));

                    Propuesta propuesta = new Propuesta(id, usuarioId, titulo, precio, descripcion,
                            tipoServicio, disponibilidad, calificacionPromedio);
                    listaPropuestas.add(propuesta);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // NO CERRAR db aquí para métodos de lectura si esperas múltiples llamadas rápidas
            // Si la DB debe cerrarse, hazlo en onDestroy de la Activity o de forma centralizada.
        }
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
        values.put(Conexion.PROPUESTA_TIPO_SERVICIO_ID, propuesta.getTipo_servicio());

        String whereClause = Conexion.PROPUESTA_ID + " = ?";
        String[] whereArgs = { String.valueOf(propuesta.getId()) };

        long resultado = db.update(Conexion.TABLE_PROPUESTA, values, whereClause, whereArgs);
        db.close();
        return resultado;
    }
}