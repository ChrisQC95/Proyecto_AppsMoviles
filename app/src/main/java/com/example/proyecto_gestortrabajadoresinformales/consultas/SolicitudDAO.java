package com.example.proyecto_gestortrabajadoresinformales.consultas;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.proyecto_gestortrabajadoresinformales.Conexion;
import com.example.proyecto_gestortrabajadoresinformales.beans.Solicitud;

public class SolicitudDAO {
    private Conexion conexion;

    public SolicitudDAO(Conexion conexion) {
        this.conexion = conexion;
    }

    public long insertarSolicitud(Solicitud solicitud) {
        SQLiteDatabase db = conexion.getWritableDatabase(); // Obtener la DB para escritura
        ContentValues values = new ContentValues();

        // Usar las constantes de Conexion para los nombres de las columnas
        values.put(Conexion.SOLICITUD_USUARIO_ID, solicitud.getUsuarioId());
        values.put(Conexion.SOLICITUD_PROPUESTA_ID, solicitud.getPropuestaId());
        values.put(Conexion.SOLICITUD_MENSAJE, solicitud.getMensaje());
        // SOLICITUD_ESTADO tiene un valor por defecto en la tabla, no es necesario pasarlo explícitamente aquí.
        // Si quisieras controlarlo, lo harías aquí: values.put(Conexion.SOLICITUD_ESTADO, solicitud.getEstado());

        long idInsertado = -1;
        try {
            idInsertado = db.insert(Conexion.TABLE_SOLICITUD, null, values);
            if (idInsertado != -1) {
                Log.d("SolicitudDAO", "Solicitud insertada con ID: " + idInsertado); // <-- AÑADE ESTA LÍNEA
            } else {
                Log.e("SolicitudDAO", "Error al insertar la solicitud en la base de datos (insert devuelve -1)."); // <-- AÑADE ESTA LÍNEA
            }
        } catch (Exception e) {
            Log.e("SolicitudDAO", "Excepción al insertar solicitud: " + e.getMessage(), e); // <-- AÑADE ESTA LÍNEA
        } finally {
            // No cierres la DB aquí si 'conexion' la gestiona globalmente o para múltiples operaciones
            // Si Conexion.close() se llama en onDestroy de la actividad, está bien.
        }

        return idInsertado; // Retorna el ID de la nueva fila o -1 si hubo un error
    }

    // Puedes añadir otros métodos como obtenerSolicitudesPorUsuario(), actualizarEstadoSolicitud(), etc.
}