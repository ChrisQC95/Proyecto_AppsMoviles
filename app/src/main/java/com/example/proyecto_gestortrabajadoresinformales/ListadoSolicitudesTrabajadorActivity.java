package com.example.proyecto_gestortrabajadoresinformales; // Ajusta el paquete si es necesario

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestortrabajadoresinformales.beans.Solicitud; // Importación corregida a beans.Solicitud
import com.example.proyecto_gestortrabajadoresinformales.Propuesta; // Importación de Propuesta si es usada
import com.example.proyecto_gestortrabajadoresinformales.beans.Usuario; // Importación de Usuario si es usada
import com.example.proyecto_gestortrabajadoresinformales.consultas.SolicitudDAO;

import java.util.ArrayList;
import java.util.List;

// Implementa la nueva interfaz OnSolicitudActionListener del adaptador
public class ListadoSolicitudesTrabajadorActivity extends AppCompatActivity implements SolicitudTrabajadorAdapter.OnSolicitudActionListener {

    private RecyclerView recyclerView;
    private SolicitudTrabajadorAdapter adapter;
    private List<Object[]> listaSolicitudes;
    private TextView tvNoSolicitudes;

    private SolicitudDAO solicitudDAO;
    private String idUsuarioTrabajadorActual; // ID del trabajador logueado
    private Conexion conexion; // Tu instancia de la conexión a la BD

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listado_solicitudes_trabajador); // Tu layout principal para esta actividad

        tvNoSolicitudes = findViewById(R.id.tvNoSolicitudes);
        recyclerView = findViewById(R.id.recyclerViewSolicitudesTrabajador);

        // Inicializar la conexión y el DAO
        conexion = new Conexion(this); // O Conexion.getInstance(this) si usas el Singleton
        solicitudDAO = new SolicitudDAO(conexion);

        // Obtener el ID del trabajador logueado
        idUsuarioTrabajadorActual = obtenerIdUsuarioTrabajadorActual();

        if (idUsuarioTrabajadorActual == null || idUsuarioTrabajadorActual.isEmpty() || idUsuarioTrabajadorActual.equals("-1")) {
            Toast.makeText(this, "No se pudo obtener el ID del trabajador. Por favor, inicie sesión.", Toast.LENGTH_LONG).show();
            tvNoSolicitudes.setText("Error: No se pudo obtener su ID de trabajador.");
            tvNoSolicitudes.setVisibility(View.VISIBLE);
            return;
        }

        listaSolicitudes = new ArrayList<>();
        // Pasa 'this' como listener de la nueva interfaz OnSolicitudActionListener
        adapter = new SolicitudTrabajadorAdapter(listaSolicitudes, this);
        recyclerView.setAdapter(adapter);

        cargarSolicitudes(); // Cargar las solicitudes al iniciar la actividad
    }

    private String obtenerIdUsuarioTrabajadorActual() {
        SharedPreferences sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        return sharedPref.getString("user_id", "-1");
    }

    private void cargarSolicitudes() {
        Log.d("ListadoSolicitudes", "Cargando solicitudes para trabajador ID: " + idUsuarioTrabajadorActual);
        new Thread(() -> {
            try {
                int idTrabajador = Integer.parseInt(idUsuarioTrabajadorActual);
                // Ahora obtiene TODAS las solicitudes del trabajador, no solo las ENVIADAS
                final List<Object[]> nuevasSolicitudes = solicitudDAO.obtenerSolicitudesPorTrabajador(idTrabajador);

                runOnUiThread(() -> {
                    if (nuevasSolicitudes != null && !nuevasSolicitudes.isEmpty()) {
                        listaSolicitudes.clear(); // Limpia la lista existente
                        listaSolicitudes.addAll(nuevasSolicitudes); // Añade las nuevas solicitudes
                        adapter.notifyDataSetChanged(); // Notifica al adapter
                        tvNoSolicitudes.setVisibility(View.GONE);
                        Log.d("ListadoSolicitudes", "Solicitudes cargadas: " + nuevasSolicitudes.size());
                    } else {
                        tvNoSolicitudes.setVisibility(View.VISIBLE);
                        Log.d("ListadoSolicitudes", "No hay solicitudes para este trabajador.");
                    }
                });
            } catch (NumberFormatException e) {
                Log.e("ListadoSolicitudes", "Error al convertir ID de trabajador a Integer: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(ListadoSolicitudesTrabajadorActivity.this, "Error de formato de ID de trabajador.", Toast.LENGTH_LONG).show();
                    tvNoSolicitudes.setText("Error: ID de trabajador inválido.");
                    tvNoSolicitudes.setVisibility(View.VISIBLE);
                });
            } catch (Exception e) {
                Log.e("ListadoSolicitudes", "Error al cargar solicitudes: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    Toast.makeText(ListadoSolicitudesTrabajadorActivity.this, "Error al cargar solicitudes.", Toast.LENGTH_LONG).show();
                    tvNoSolicitudes.setText("Error al cargar solicitudes.");
                    tvNoSolicitudes.setVisibility(View.VISIBLE);
                });
            }
        }).start();
    }

    @Override
    public void onAceptarClick(Solicitud solicitud) {
        // Usa el método general de confirmación para aceptar
        mostrarConfirmacion("Aceptar Solicitud", "¿Estás seguro de que quieres aceptar esta solicitud?", solicitud, "ACEPTADA");
    }

    @Override
    public void onRechazarClick(Solicitud solicitud) {
        // Implementación del nuevo método de la interfaz para rechazar
        mostrarConfirmacion("Rechazar Solicitud", "¿Estás seguro de que quieres rechazar esta solicitud?", solicitud, "RECHAZADA");
    }

    // Método general para mostrar la confirmación y actualizar el estado
    private void mostrarConfirmacion(String titulo, String mensaje, Solicitud solicitud, String nuevoEstado) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo);
        builder.setMessage(mensaje + "\n\nPropuesta: " + solicitud.getMensaje()); // Mostrar mensaje de la solicitud para contexto

        builder.setPositiveButton("Sí", (dialog, which) -> {
            actualizarEstadoSolicitud(solicitud, nuevoEstado);
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void actualizarEstadoSolicitud(Solicitud solicitud, String nuevoEstado) {
        new Thread(() -> {
            boolean exito = solicitudDAO.actualizarEstadoSolicitud(solicitud.getId(), nuevoEstado);
            runOnUiThread(() -> {
                if (exito) {
                    Toast.makeText(ListadoSolicitudesTrabajadorActivity.this, "Solicitud " + nuevoEstado.toLowerCase() + " con éxito.", Toast.LENGTH_SHORT).show();
                    // Recargar la lista para que el estado se actualice en la UI
                    cargarSolicitudes();
                } else {
                    Toast.makeText(ListadoSolicitudesTrabajadorActivity.this, "Error al " + nuevoEstado.toLowerCase() + " la solicitud.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cierra la conexión cuando la actividad se destruye.
        // Si usas el patrón Singleton global, puedes quitar esto.
        if (conexion != null) {
            conexion.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar solicitudes cada vez que la actividad se vuelve visible
        // Esto es útil si el estado de las solicitudes puede cambiar desde otras partes de la app
        cargarSolicitudes();
    }
}
