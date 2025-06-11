package com.example.proyecto_gestortrabajadoresinformales.consultas;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.proyecto_gestortrabajadoresinformales.Conexion;
import com.example.proyecto_gestortrabajadoresinformales.beans.Solicitud;
import com.example.proyecto_gestortrabajadoresinformales.Propuesta; // Importar Propuesta para el JOIN
import com.example.proyecto_gestortrabajadoresinformales.beans.Usuario;
import java.util.ArrayList;
import java.util.List;

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
    public List<Object[]> obtenerSolicitudesPorTrabajador(int idTrabajador) {
        List<Object[]> solicitudesDetalle = new ArrayList<>();
        SQLiteDatabase db = conexion.getReadableDatabase();
        Cursor cursor = null;

        // Consulta SQL para unir solicitudes con propuestas y usuarios (clientes)
        // Solo obtener solicitudes con estado 'ENVIADA'
        String query = "SELECT " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_ID + ", " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_USUARIO_ID + ", " + // ID del cliente
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_PROPUESTA_ID + ", " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_MENSAJE + ", " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_ESTADO + ", " +
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_TITULO + ", " +
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_PRECIO + ", " +
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_DESCRIPCION + ", " +
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_CALIFICACION_PROMEDIO + ", " + // Calificación de la propuesta
                Conexion.TABLE_USUARIO + "." + Conexion.USUARIO_NOMBRES + ", " +
                Conexion.TABLE_USUARIO + "." + Conexion.USUARIO_APELLIDOS + ", " +
                Conexion.TABLE_USUARIO + "." + Conexion.USUARIO_TELEFONO + ", " +
                Conexion.TABLE_USUARIO + "." + Conexion.USUARIO_CORREO + ", " +
                Conexion.TABLE_TIPO_SERVICIO + "." + Conexion.TIPO_SERVICIO_NOMBRE + " " + // Nombre del tipo de servicio
                "FROM " + Conexion.TABLE_SOLICITUD + " " +
                "INNER JOIN " + Conexion.TABLE_PROPUESTA + " ON " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_PROPUESTA_ID + " = " +
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_ID + " " +
                "INNER JOIN " + Conexion.TABLE_USUARIO + " ON " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_USUARIO_ID + " = " +
                Conexion.TABLE_USUARIO + "." + Conexion.USUARIO_ID + " " +
                "INNER JOIN " + Conexion.TABLE_TIPO_SERVICIO + " ON " + // JOIN con tipo_servicio
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_TIPO_SERVICIO_ID + " = " +
                Conexion.TABLE_TIPO_SERVICIO + "." + Conexion.TIPO_SERVICIO_ID + " " +
                "WHERE " + Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_USUARIO_ID + " = ? " +
                "AND " + Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_ESTADO + " = 'ENVIADA'"; // Solo solicitudes enviadas

        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(idTrabajador)});

            if (cursor.moveToFirst()) {
                do {
                    // Mapear los datos a objetos o directamente a un array de Object
                    Solicitud solicitud = new Solicitud(
                            cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.SOLICITUD_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.SOLICITUD_USUARIO_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.SOLICITUD_PROPUESTA_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(Conexion.SOLICITUD_MENSAJE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(Conexion.SOLICITUD_ESTADO))
                    );

                    Propuesta propuesta = new Propuesta(
                            cursor.getString(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_TITULO)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_PRECIO)),
                            cursor.getString(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_DESCRIPCION))
                            // Otros campos de Propuesta si son necesarios, pero solo los relevantes para la vista
                    );
                    // Asignar la calificación de la propuesta
                    propuesta.setCalificacion(cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_CALIFICACION_PROMEDIO)));
                    // Asignar el nombre del tipo de servicio a la propuesta
                    propuesta.setTipoServicioNombre(cursor.getString(cursor.getColumnIndexOrThrow(Conexion.TIPO_SERVICIO_NOMBRE)));


                    Usuario cliente = new Usuario();
                    cliente.setNombres(cursor.getString(cursor.getColumnIndexOrThrow(Conexion.USUARIO_NOMBRES)));
                    cliente.setApellidos(cursor.getString(cursor.getColumnIndexOrThrow(Conexion.USUARIO_APELLIDOS)));
                    cliente.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow(Conexion.USUARIO_TELEFONO)));
                    cliente.setCorreo(cursor.getString(cursor.getColumnIndexOrThrow(Conexion.USUARIO_CORREO)));
                    // Aquí asumimos que los campos del usuario ya están en el bean Usuario.java

                    solicitudesDetalle.add(new Object[]{solicitud, propuesta, cliente});

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("SolicitudDAO", "Error al obtener solicitudes por trabajador: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // No cierres la DB aquí si usas una conexión gestionada globalmente por Conexion.java
        }
        return solicitudesDetalle;
    }
    public boolean actualizarEstadoSolicitud(int idSolicitud, String nuevoEstado) {
        SQLiteDatabase db = conexion.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Conexion.SOLICITUD_ESTADO, nuevoEstado);

        String selection = Conexion.SOLICITUD_ID + " = ?";
        String[] selectionArgs = { String.valueOf(idSolicitud) };

        int count = 0;
        try {
            count = db.update(
                    Conexion.TABLE_SOLICITUD,
                    values,
                    selection,
                    selectionArgs);
            if (count > 0) {
                Log.d("SolicitudDAO", "Estado de solicitud " + idSolicitud + " actualizado a: " + nuevoEstado);
            } else {
                Log.w("SolicitudDAO", "No se encontró la solicitud " + idSolicitud + " para actualizar o el estado ya era el mismo.");
            }
        } catch (Exception e) {
            Log.e("SolicitudDAO", "Error al actualizar estado de solicitud " + idSolicitud + ": " + e.getMessage(), e);
        } finally {
            // No cierres la DB aquí
        }
        return count > 0;
    }

    // Puedes añadir otros métodos como obtenerSolicitudesPorUsuario(), actualizarEstadoSolicitud(), etc.
}