package com.example.proyecto_gestortrabajadoresinformales.consultas;

import android.database.Cursor;

import com.example.proyecto_gestortrabajadoresinformales.beans.Calificacion;

import java.util.ArrayList;
import java.util.List;

public class CalificacionDAO {
    private Conexion conexion;

    public CalificacionDAO(Conexion conexion) {
        this.conexion = conexion;
    }

    public List<Calificacion> obtenerCalificacionesPorTrabajador(int idTrabajador) {
        List<Calificacion> lista = new ArrayList<>();
        String sql = "SELECT u.nombres || ' ' || u.apellidos AS nombre_cliente, " +
                     "p.titulo AS tipo_trabajo, " +
                     "c.puntuacion, c.comentario " +
                     "FROM calificacion c " +
                     "JOIN usuario u ON c.cliente_id = u.id " +
                     "JOIN solicitud s ON c.solicitud_id = s.id " +
                     "JOIN propuesta p ON s.propuesta_id = p.id " +
                     "WHERE p.usuario_id = ?";
        Cursor cursor = conexion.getReadableDatabase().rawQuery(sql, new String[]{String.valueOf(idTrabajador)});
        if (cursor.moveToFirst()) {
            do {
                String nombreCliente = cursor.getString(0);
                String tipoTrabajo = cursor.getString(1);
                float puntuacion = cursor.getFloat(2);
                String comentario = cursor.getString(3);
                lista.add(new Calificacion(nombreCliente, tipoTrabajo, puntuacion, comentario));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }
}
