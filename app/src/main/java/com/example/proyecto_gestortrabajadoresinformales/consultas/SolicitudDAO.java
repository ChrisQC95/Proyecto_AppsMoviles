package com.example.proyecto_gestortrabajadoresinformales.consultas;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.proyecto_gestortrabajadoresinformales.beans.Solicitud;
import com.example.proyecto_gestortrabajadoresinformales.beans.Propuesta; // Importación corregida a beans.Propuesta
import com.example.proyecto_gestortrabajadoresinformales.beans.Usuario;

import java.util.ArrayList;
import java.util.List;

public class SolicitudDAO {
    private Conexion conexion;

    public SolicitudDAO(Conexion conexion) {
        this.conexion = conexion;
    }

    /**
     * Inserta una nueva solicitud en la base de datos.
     * @param solicitud El objeto Solicitud a insertar.
     * @return El ID de la fila recién insertada, o -1 si ocurrió un error.
     */
    public long insertarSolicitud(Solicitud solicitud) {
        SQLiteDatabase db = conexion.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Conexion.SOLICITUD_USUARIO_ID, solicitud.getUsuarioId());
        values.put(Conexion.SOLICITUD_PROPUESTA_ID, solicitud.getPropuestaId());
        //values.put(Conexion.SOLICITUD_ESTADO, solicitud.getEstado()); // Asegurarse de guardar el estado inicial
        if (solicitud.getEstado() != null) {
            values.put(Conexion.SOLICITUD_ESTADO, solicitud.getEstado());
        }
        long idInsertado = -1;
        try {
            idInsertado = db.insert(Conexion.TABLE_SOLICITUD, null, values);

            if (idInsertado != -1) {
                Log.d("SolicitudDAO", "Solicitud insertada con ID: " + idInsertado);
            } else {
                Log.e("SolicitudDAO", "Error al insertar la solicitud en la base de datos (insert devuelve -1).");
            }
        } catch (Exception e) {
            Log.e("SolicitudDAO", "Excepción al insertar solicitud: " + e.getMessage(), e);
        } finally {
            // No cierres la DB aquí, la gestión de la conexión es responsabilidad de Conexion.java
        }
        return idInsertado;
    }

    /**
     * Obtiene una lista de solicitudes de trabajo dirigidas a las propuestas de un trabajador específico.
     * Incluye información del cliente, de la propuesta, del tipo de servicio y del distrito del cliente.
     * @param idTrabajador El ID del usuario TRABAJADOR.
     * @return Una lista de objetos Solicitud, Propuesta, Usuario y String (distrito) combinados.
     */
    public List<Object[]> obtenerSolicitudesPorTrabajador(int idTrabajador) {
        List<Object[]> solicitudesDetalle = new ArrayList<>();
        SQLiteDatabase db = conexion.getReadableDatabase();
        Cursor cursor = null;

        String query = "SELECT " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_ID + ", " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_USUARIO_ID + ", " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_PROPUESTA_ID + ", " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_ESTADO + ", " +
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_TITULO + ", " +
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_PRECIO + ", " +
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_DESCRIPCION + ", " +
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_CALIFICACION_PROMEDIO + ", " +
                "ClienteUsuario." + Conexion.USUARIO_NOMBRES + ", " +
                "ClienteUsuario." + Conexion.USUARIO_APELLIDOS + ", " +
                "ClienteUsuario." + Conexion.USUARIO_TELEFONO + ", " +
                "ClienteUsuario." + Conexion.USUARIO_CORREO + ", " +
                Conexion.TABLE_TIPO_SERVICIO + "." + Conexion.TIPO_SERVICIO_NOMBRE + ", " +
                Conexion.TABLE_DISTRITO + "." + Conexion.DISTRITO_NOMBRE + " AS " + Conexion.DISTRITO_NOMBRE + " " +
                "FROM " + Conexion.TABLE_SOLICITUD + " " +
                "INNER JOIN " + Conexion.TABLE_PROPUESTA + " ON " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_PROPUESTA_ID + " = " +
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_ID + " " +
                "INNER JOIN " + Conexion.TABLE_USUARIO + " AS ClienteUsuario ON " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_USUARIO_ID + " = " +
                "ClienteUsuario." + Conexion.USUARIO_ID + " " +
                "LEFT JOIN " + Conexion.TABLE_PERFIL + " AS ClientePerfil ON " +
                "ClienteUsuario." + Conexion.USUARIO_ID + " = " +
                "ClientePerfil." + Conexion.PERFIL_USUARIO_ID + " " +
                "LEFT JOIN " + Conexion.TABLE_DISTRITO + " ON " +
                "ClientePerfil." + Conexion.PERFIL_DISTRITO_ID + " = " +
                Conexion.TABLE_DISTRITO + "." + Conexion.DISTRITO_ID + " " +
                "INNER JOIN " + Conexion.TABLE_TIPO_SERVICIO + " ON " +
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_TIPO_SERVICIO_ID + " = " +
                Conexion.TABLE_TIPO_SERVICIO + "." + Conexion.TIPO_SERVICIO_ID + " " +
                "WHERE " + Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_USUARIO_ID + " = ?";

        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(idTrabajador)});

            if (cursor.moveToFirst()) {
                do {
                    Solicitud solicitud = new Solicitud(
                            cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.SOLICITUD_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.SOLICITUD_USUARIO_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.SOLICITUD_PROPUESTA_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(Conexion.SOLICITUD_ESTADO))
                    );

                    Propuesta propuesta = new Propuesta(
                            cursor.getString(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_TITULO)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_PRECIO)),
                            cursor.getString(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_DESCRIPCION))
                    );
                    propuesta.setCalificacion(cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_CALIFICACION_PROMEDIO)));
                    propuesta.setTipoServicioNombre(cursor.getString(cursor.getColumnIndexOrThrow(Conexion.TIPO_SERVICIO_NOMBRE)));


                    Usuario cliente = new Usuario();
                    cliente.setNombres(cursor.getString(cursor.getColumnIndexOrThrow(Conexion.USUARIO_NOMBRES)));
                    cliente.setApellidos(cursor.getString(cursor.getColumnIndexOrThrow(Conexion.USUARIO_APELLIDOS)));
                    cliente.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow(Conexion.USUARIO_TELEFONO)));
                    cliente.setCorreo(cursor.getString(cursor.getColumnIndexOrThrow(Conexion.USUARIO_CORREO)));

                    String distritoNombre = cursor.getString(cursor.getColumnIndexOrThrow(Conexion.DISTRITO_NOMBRE));

                    solicitudesDetalle.add(new Object[]{solicitud, propuesta, cliente, distritoNombre});
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("SolicitudDAO", "Error al obtener solicitudes por trabajador: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return solicitudesDetalle;
    }

    /**
     * Actualiza el estado de una solicitud.
     * @param idSolicitud El ID de la solicitud a actualizar.
     * @param nuevoEstado El nuevo estado de la solicitud.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
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

    /**
     * Obtiene una lista de solicitudes aceptadas dirigidas a las propuestas de un trabajador específico.
     * @param idTrabajador El ID del usuario TRABAJADOR.
     * @return Una lista de objetos Solicitud, Propuesta, Usuario (cliente), String (distrito) combinados.
     */
    public List<Object[]> obtenerSolicitudesAceptadasPorTrabajador(int idTrabajador) {
        List<Object[]> solicitudesDetalle = new ArrayList<>();
        SQLiteDatabase db = conexion.getReadableDatabase();
        Cursor cursor = null;

        String query = "SELECT " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_ID + ", " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_USUARIO_ID + ", " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_PROPUESTA_ID + ", " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_ESTADO + ", " +
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_TITULO + ", " +
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_PRECIO + ", " +
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_DESCRIPCION + ", " +
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_CALIFICACION_PROMEDIO + ", " +
                "ClienteUsuario." + Conexion.USUARIO_NOMBRES + ", " +
                "ClienteUsuario." + Conexion.USUARIO_APELLIDOS + ", " +
                "ClienteUsuario." + Conexion.USUARIO_TELEFONO + ", " +
                "ClienteUsuario." + Conexion.USUARIO_CORREO + ", " +
                Conexion.TABLE_TIPO_SERVICIO + "." + Conexion.TIPO_SERVICIO_NOMBRE + ", " +
                Conexion.TABLE_DISTRITO + "." + Conexion.DISTRITO_NOMBRE + " AS " + Conexion.DISTRITO_NOMBRE + " " +
                "FROM " + Conexion.TABLE_SOLICITUD + " " +
                "INNER JOIN " + Conexion.TABLE_PROPUESTA + " ON " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_PROPUESTA_ID + " = " +
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_ID + " " +
                "INNER JOIN " + Conexion.TABLE_USUARIO + " AS ClienteUsuario ON " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_USUARIO_ID + " = " +
                "ClienteUsuario." + Conexion.USUARIO_ID + " " +
                "LEFT JOIN " + Conexion.TABLE_PERFIL + " AS ClientePerfil ON " +
                "ClienteUsuario." + Conexion.USUARIO_ID + " = " +
                "ClientePerfil." + Conexion.PERFIL_USUARIO_ID + " " +
                "LEFT JOIN " + Conexion.TABLE_DISTRITO + " ON " +
                "ClientePerfil." + Conexion.PERFIL_DISTRITO_ID + " = " +
                Conexion.TABLE_DISTRITO + "." + Conexion.DISTRITO_ID + " " +
                "INNER JOIN " + Conexion.TABLE_TIPO_SERVICIO + " ON " +
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_TIPO_SERVICIO_ID + " = " +
                Conexion.TABLE_TIPO_SERVICIO + "." + Conexion.TIPO_SERVICIO_ID + " " +
                "WHERE " + Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_USUARIO_ID + " = ? " +
                "AND " + Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_ESTADO + " = 'ACEPTADA'";

        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(idTrabajador)});

            if (cursor.moveToFirst()) {
                do {
                    Solicitud solicitud = new Solicitud(
                            cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.SOLICITUD_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.SOLICITUD_USUARIO_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.SOLICITUD_PROPUESTA_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(Conexion.SOLICITUD_ESTADO))
                    );

                    Propuesta propuesta = new Propuesta(
                            cursor.getString(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_TITULO)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_PRECIO)),
                            cursor.getString(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_DESCRIPCION))
                    );
                    propuesta.setCalificacion(cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_CALIFICACION_PROMEDIO)));
                    propuesta.setTipoServicioNombre(cursor.getString(cursor.getColumnIndexOrThrow(Conexion.TIPO_SERVICIO_NOMBRE)));

                    Usuario cliente = new Usuario();
                    cliente.setNombres(cursor.getString(cursor.getColumnIndexOrThrow(Conexion.USUARIO_NOMBRES)));
                    cliente.setApellidos(cursor.getString(cursor.getColumnIndexOrThrow(Conexion.USUARIO_APELLIDOS)));
                    cliente.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow(Conexion.USUARIO_TELEFONO)));
                    cliente.setCorreo(cursor.getString(cursor.getColumnIndexOrThrow(Conexion.USUARIO_CORREO)));

                    String distritoNombre = cursor.getString(cursor.getColumnIndexOrThrow(Conexion.DISTRITO_NOMBRE));

                    solicitudesDetalle.add(new Object[]{solicitud, propuesta, cliente, distritoNombre});
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("SolicitudDAO", "Error al obtener solicitudes aceptadas por trabajador: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return solicitudesDetalle;
    }

    /**
     * Obtiene una lista de solicitudes aceptadas realizadas por un cliente específico.
     * Incluye información del trabajador, de la propuesta, del tipo de servicio y del distrito del trabajador.
     * @param idCliente El ID del usuario CLIENTE.
     * @return Una lista de objetos Solicitud, Propuesta, Usuario (trabajador), String (distrito) combinados.
     */
    public List<Object[]> obtenerSolicitudesAceptadasPorCliente(int idCliente) {
        List<Object[]> solicitudesDetalle = new ArrayList<>();
        SQLiteDatabase db = conexion.getReadableDatabase();
        Cursor cursor = null;

        String query = "SELECT " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_ID + ", " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_USUARIO_ID + ", " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_PROPUESTA_ID + ", " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_MENSAJE + ", " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_ESTADO + ", " +
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_TITULO + ", " +
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_PRECIO + ", " +
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_DESCRIPCION + ", " +
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_CALIFICACION_PROMEDIO + ", " +
                "TrabajadorUsuario." + Conexion.USUARIO_NOMBRES + ", " +
                "TrabajadorUsuario." + Conexion.USUARIO_APELLIDOS + ", " +
                "TrabajadorUsuario." + Conexion.USUARIO_TELEFONO + ", " +
                "TrabajadorUsuario." + Conexion.USUARIO_CORREO + ", " +
                Conexion.TABLE_TIPO_SERVICIO + "." + Conexion.TIPO_SERVICIO_NOMBRE + ", " +
                Conexion.TABLE_DISTRITO + "." + Conexion.DISTRITO_NOMBRE + " AS " + Conexion.DISTRITO_NOMBRE + ", " +
                // Agregamos el ID de la calificación para saber si ya ha sido calificada
                Conexion.TABLE_CALIFICACION + "." + Conexion.CALIFICACION_ID + " AS CalificacionExiste " + // Alias para la columna
                "FROM " + Conexion.TABLE_SOLICITUD + " " +
                "INNER JOIN " + Conexion.TABLE_PROPUESTA + " ON " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_PROPUESTA_ID + " = " +
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_ID + " " +
                "INNER JOIN " + Conexion.TABLE_USUARIO + " AS TrabajadorUsuario ON " +
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_USUARIO_ID + " = " +
                "TrabajadorUsuario." + Conexion.USUARIO_ID + " " +
                "LEFT JOIN " + Conexion.TABLE_PERFIL + " AS TrabajadorPerfil ON " +
                "TrabajadorUsuario." + Conexion.USUARIO_ID + " = " +
                "TrabajadorPerfil." + Conexion.PERFIL_USUARIO_ID + " " +
                "LEFT JOIN " + Conexion.TABLE_DISTRITO + " ON " +
                "TrabajadorPerfil." + Conexion.PERFIL_DISTRITO_ID + " = " +
                Conexion.TABLE_DISTRITO + "." + Conexion.DISTRITO_ID + " " +
                "INNER JOIN " + Conexion.TABLE_TIPO_SERVICIO + " ON " +
                Conexion.TABLE_PROPUESTA + "." + Conexion.PROPUESTA_TIPO_SERVICIO_ID + " = " +
                Conexion.TABLE_TIPO_SERVICIO + "." + Conexion.TIPO_SERVICIO_ID + " " +
                // LEFT JOIN a la tabla de calificaciones para verificar si la solicitud ya fue calificada
                "LEFT JOIN " + Conexion.TABLE_CALIFICACION + " ON " +
                Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_ID + " = " +
                Conexion.TABLE_CALIFICACION + "." + Conexion.CALIFICACION_SOLICITUD_ID + " " +
                "WHERE " + Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_USUARIO_ID + " = ? " +
                "AND (" + Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_ESTADO + " = 'ACEPTADA' " +
                "OR " + Conexion.TABLE_SOLICITUD + "." + Conexion.SOLICITUD_ESTADO + " = 'FINALIZADA') " +
                "AND " + Conexion.TABLE_CALIFICACION + "." + Conexion.CALIFICACION_ID + " IS NULL";

        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(idCliente)});

            if (cursor.moveToFirst()) {
                do {
                    Solicitud solicitud = new Solicitud(
                            cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.SOLICITUD_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.SOLICITUD_USUARIO_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.SOLICITUD_PROPUESTA_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(Conexion.SOLICITUD_ESTADO))
                    );

                    Propuesta propuesta = new Propuesta(
                            cursor.getString(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_TITULO)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_PRECIO)),
                            cursor.getString(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_DESCRIPCION))
                    );
                    propuesta.setCalificacion(cursor.getInt(cursor.getColumnIndexOrThrow(Conexion.PROPUESTA_CALIFICACION_PROMEDIO)));
                    propuesta.setTipoServicioNombre(cursor.getString(cursor.getColumnIndexOrThrow(Conexion.TIPO_SERVICIO_NOMBRE)));

                    Usuario trabajador = new Usuario();
                    trabajador.setNombres(cursor.getString(cursor.getColumnIndexOrThrow(Conexion.USUARIO_NOMBRES)));
                    trabajador.setApellidos(cursor.getString(cursor.getColumnIndexOrThrow(Conexion.USUARIO_APELLIDOS)));
                    trabajador.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow(Conexion.USUARIO_TELEFONO)));
                    trabajador.setCorreo(cursor.getString(cursor.getColumnIndexOrThrow(Conexion.USUARIO_CORREO)));

                    String distritoNombre = cursor.getString(cursor.getColumnIndexOrThrow(Conexion.DISTRITO_NOMBRE));

                    // Obtener el ID de la calificación, si existe.
                    Integer calificacionId = null;
                    int calificacionIdColumnIndex = cursor.getColumnIndex("CalificacionExiste");
                    if (calificacionIdColumnIndex != -1 && !cursor.isNull(calificacionIdColumnIndex)) {
                        calificacionId = cursor.getInt(calificacionIdColumnIndex);
                    }

                    solicitudesDetalle.add(new Object[]{solicitud, propuesta, trabajador, distritoNombre, calificacionId}); // Añadir calificacionId
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("SolicitudDAO", "Error al obtener solicitudes aceptadas por cliente: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return solicitudesDetalle;
    }

    /**
     * Inserta una nueva calificación en la base de datos.
     * @param solicitudId El ID de la solicitud a la que se refiere la calificación.
     * @param clienteId El ID del cliente que realiza la calificación.
     * @param puntuacion La puntuación otorgada (1-5).
     * @param comentario El comentario asociado a la calificación (opcional).
     * @return El ID de la fila recién insertada, o -1 si ocurrió un error.
     */
    public long insertarCalificacion(int solicitudId, int clienteId, int puntuacion, String comentario) {
        SQLiteDatabase db = conexion.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Conexion.CALIFICACION_SOLICITUD_ID, solicitudId);
        values.put(Conexion.CALIFICACION_CLIENTE_ID, clienteId);
        values.put(Conexion.CALIFICACION_PUNTUACION, puntuacion);
        values.put(Conexion.CALIFICACION_COMENTARIO, comentario);

        long idInsertado = -1;
        try {
            idInsertado = db.insert(Conexion.TABLE_CALIFICACION, null, values);
            if (idInsertado != -1) {
                Log.d("SolicitudDAO", "Calificación insertada con ID: " + idInsertado + " para solicitud: " + solicitudId);

                // 🔁 NUEVO: Obtener el ID de la propuesta relacionada
                int propuestaId = -1;
                Cursor cursor = db.rawQuery("SELECT propuesta_id FROM solicitud WHERE id = ?",
                        new String[]{String.valueOf(solicitudId)});
                if (cursor.moveToFirst()) {
                    propuestaId = cursor.getInt(0);
                }
                cursor.close();

                // 🔁 NUEVO: Actualizar promedio de calificaciones en la tabla propuesta
                if (propuestaId != -1) {
                    PropuestaDAO propuestaDAO = new PropuestaDAO(conexion);
                    propuestaDAO.actualizarCalificacionPromedio(propuestaId);
                    Log.d("SolicitudDAO", "Promedio de calificación actualizado para propuesta: " + propuestaId);
                }


            } else {
                Log.e("SolicitudDAO", "Error al insertar la calificación en la base de datos.");
            }
        } catch (Exception e) {
            Log.e("SolicitudDAO", "Excepción al insertar calificación: " + e.getMessage(), e);
        } finally {
            // No cierres la DB aquí
        }
        return idInsertado;
    }
}
