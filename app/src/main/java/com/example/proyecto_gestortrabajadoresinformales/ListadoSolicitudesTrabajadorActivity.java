package com.example.proyecto_gestortrabajadoresinformales; // Ajusta el paquete si es necesario

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestortrabajadoresinformales.Propuesta;
import com.example.proyecto_gestortrabajadoresinformales.beans.Solicitud;
import com.example.proyecto_gestortrabajadoresinformales.beans.Usuario;
import com.example.proyecto_gestortrabajadoresinformales.consultas.SolicitudDAO;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

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
        setContentView(R.layout.listado_solicitudes_trabajador);

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
        adapter = new SolicitudTrabajadorAdapter(
                listaSolicitudes,
                this // OnSolicitudActionListener (implementado por la Activity)
        );
        recyclerView.setAdapter(adapter);

        cargarSolicitudes(); // Cargar las solicitudes al iniciar la actividad

        FloatingActionButton fabCalificacion = findViewById(R.id.fabCalificacion);
        fabCalificacion.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListadoCalificacionesTrabajadorActivity.class);
            startActivity(intent);
        });
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
                final List<Object[]> nuevasSolicitudes = solicitudDAO.obtenerSolicitudesPorTrabajador(idTrabajador);

                runOnUiThread(() -> {
                    if (nuevasSolicitudes != null && !nuevasSolicitudes.isEmpty()) {
                        listaSolicitudes.clear(); // Limpia la lista existente
                        for (Object[] solicitud : nuevasSolicitudes) {
                            Solicitud solicitudObj = (Solicitud) solicitud[0];
                            // Filtrar solicitudes con estado ENVIADA o ACEPTADA
                            if ("ENVIADA".equals(solicitudObj.getEstado()) || "ACEPTADA".equals(solicitudObj.getEstado())) {
                                listaSolicitudes.add(solicitud);
                            }
                        }
                        adapter.notifyDataSetChanged(); // Notifica al adapter
                        tvNoSolicitudes.setVisibility(View.GONE);
                        Log.d("ListadoSolicitudes", "Solicitudes cargadas: " + listaSolicitudes.size());
                    } else {
                        tvNoSolicitudes.setVisibility(View.VISIBLE);
                        Log.d("ListadoSolicitudes", "No hay solicitudes pendientes para este trabajador.");
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
        // Lógica para aceptar la solicitud
        mostrarConfirmacionAceptar(solicitud);
    }

    private void mostrarConfirmacionAceptar(Solicitud solicitud) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar Aceptar Solicitud");
        builder.setMessage("¿Estás seguro de que quieres aceptar esta solicitud de " + solicitud.getMensaje() + "?");

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            aceptarSolicitud(solicitud);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void aceptarSolicitud(Solicitud solicitud) {
        new Thread(() -> {
            boolean exito = solicitudDAO.actualizarEstadoSolicitud(solicitud.getId(), "ACEPTADA");
            runOnUiThread(() -> {
                if (exito) {
                    Toast.makeText(ListadoSolicitudesTrabajadorActivity.this, "Solicitud aceptada con éxito.", Toast.LENGTH_SHORT).show();
                    // Recargar la lista para que la solicitud desaparezca (ya que filtra por 'ENVIADA')
                    cargarSolicitudes();
                } else {
                    Toast.makeText(ListadoSolicitudesTrabajadorActivity.this, "Error al aceptar la solicitud.", Toast.LENGTH_SHORT).show();
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

    private void mostrarConfirmacionFinalizar(Solicitud solicitud) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar Finalizar Solicitud");
        builder.setMessage("¿Estás seguro de que quieres finalizar esta solicitud?");

        builder.setPositiveButton("Finalizar", (dialog, which) -> {
            finalizarSolicitud(solicitud);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void finalizarSolicitud(Solicitud solicitud) {
        new Thread(() -> {
            boolean exito = solicitudDAO.actualizarEstadoSolicitud(solicitud.getId(), "FINALIZADA");
            runOnUiThread(() -> {
                if (exito) {
                    Toast.makeText(ListadoSolicitudesTrabajadorActivity.this, "Solicitud finalizada con éxito.", Toast.LENGTH_SHORT).show();
                    cargarSolicitudes();
                } else {
                    Toast.makeText(ListadoSolicitudesTrabajadorActivity.this, "Error al finalizar la solicitud.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    // Ejemplo de método para obtener el id del trabajador (ajusta según tu lógica real)
    private int obtenerIdTrabajadorActual() {
        SharedPreferences sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String id = sharedPref.getString("user_id", "-1");
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public void onFinalizarClick(Solicitud solicitud) {
        mostrarConfirmacionFinalizar(solicitud);
    }

    @Override
    public void onRechazarClick(Solicitud solicitud) {
        // Puedes dejarlo vacío si no lo usas, pero debe estar implementado
    }
}
