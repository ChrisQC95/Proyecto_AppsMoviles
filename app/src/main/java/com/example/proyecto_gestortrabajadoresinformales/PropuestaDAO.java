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
    public List<Propuesta> obtenerPropuestasDisponibles() {
        List<Propuesta> listaPropuestas = new ArrayList<>();
        SQLiteDatabase db = adminDB.getReadableDatabase();

        // Definimos la cláusula WHERE para filtrar por disponibilidad = 1
        String selection = Conexion.PROPUESTA_DISPONIBILIDAD + " = ?";
        String[] selectionArgs = { "1" }; // El valor 1 para disponibilidad

        Cursor cursor = db.query(
                Conexion.TABLE_PROPUESTA, // La tabla a consultar
                null, // Todas las columnas
                selection, // La cláusula WHERE
                selectionArgs, // Los argumentos para la cláusula WHERE
                null, // Sin GROUP BY
                null, // Sin HAVING
                null  // Sin ORDER BY
        );

        if (cursor.moveToFirst()) {
            do {
                // Obtiene los valores de cada columna y crea un objeto Propuesta
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
}
