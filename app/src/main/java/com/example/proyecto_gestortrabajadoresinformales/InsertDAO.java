package com.example.proyecto_gestortrabajadoresinformales;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class InsertDAO {
    private SQLiteDatabase db;

    public InsertDAO(Context context) {
        Conexion conexion = new Conexion(context);
        db = conexion.getWritableDatabase();
    }

    public long insertarPropuesta(int usuarioId, String titulo, double precio, String descripcion,
                                  int tipoServicioId, int disponibilidad, int calificacionPromedio) {
        ContentValues valores = new ContentValues();
        valores.put(Conexion.PROPUESTA_USUARIO_ID, usuarioId);
        valores.put(Conexion.PROPUESTA_TITULO, titulo);
        valores.put(Conexion.PROPUESTA_PRECIO, precio);
        valores.put(Conexion.PROPUESTA_DESCRIPCION, descripcion);
        valores.put(Conexion.PROPUESTA_TIPO_SERVICIO_ID, tipoServicioId);
        valores.put(Conexion.PROPUESTA_DISPONIBILIDAD, disponibilidad);
        valores.put(Conexion.PROPUESTA_CALIFICACION_PROMEDIO, calificacionPromedio);

        return db.insert(Conexion.TABLE_PROPUESTA, null, valores);
    }
}
