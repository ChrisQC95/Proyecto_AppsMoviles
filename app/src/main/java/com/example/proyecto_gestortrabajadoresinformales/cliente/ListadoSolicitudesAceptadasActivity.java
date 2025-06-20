package com.example.proyecto_gestortrabajadoresinformales.cliente;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestortrabajadoresinformales.R;
import com.example.proyecto_gestortrabajadoresinformales.consultas.Conexion;
import com.example.proyecto_gestortrabajadoresinformales.consultas.SolicitudDAO;

import java.util.ArrayList;
import java.util.List;

// Ahora esta actividad implementa la interfaz OnSolicitudInteractionListener
public class ListadoSolicitudesAceptadasActivity extends AppCompatActivity implements SolicitudAceptadaAdapter.OnSolicitudInteractionListener {

    private RecyclerView recyclerViewSolicitudesAceptadas;
    private TextView tvNoSolicitudesAceptadas;
    private SolicitudAceptadaAdapter adapter;
    private List<Object[]> listaSolicitudesAceptadas;

    private SolicitudDAO solicitudDAO;
    private Conexion conexion;
    private String idUsuarioActual; // Almacena el ID del usuario logueado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listado_solicitudes_aceptadas); // Asegúrate que este es tu layout XML para esta actividad

        recyclerViewSolicitudesAceptadas = findViewById(R.id.recyclerViewSolicitudesAceptadas);
        tvNoSolicitudesAceptadas = findViewById(R.id.tvNoSolicitudesAceptadas);

        recyclerViewSolicitudesAceptadas.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar Conexion y SolicitudDAO
        conexion = new Conexion(this);
        solicitudDAO = new SolicitudDAO(conexion);

        // Obtener el ID del usuario actual (cliente o trabajador)
        SharedPreferences sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        idUsuarioActual = sharedPref.getString("user_id", "-1");

        cargarSolicitudesAceptadas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarSolicitudesAceptadas();
    }

    private void cargarSolicitudesAceptadas() {
        String tipoUsuario = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("user_type", "TRABAJADOR"); // Valor por defecto "TRABAJADOR"
        int idUsuario;
        try {
            idUsuario = Integer.parseInt(idUsuarioActual); // Usar el idUsuarioActual ya obtenido
        } catch (NumberFormatException e) {
            Log.e("ListadoAceptadas", "Error al convertir ID de usuario a Integer: " + e.getMessage());
            Toast.makeText(this, "Error: ID de usuario inválido.", Toast.LENGTH_LONG).show();
            idUsuario = -1; // Asignar un valor inválido si hay error
        }

        if (idUsuario == -1) {
            tvNoSolicitudesAceptadas.setText("Error: No se pudo obtener su ID de usuario.");
            tvNoSolicitudesAceptadas.setVisibility(View.VISIBLE);
            recyclerViewSolicitudesAceptadas.setVisibility(View.GONE);
            return;
        }

        // Ejecutar la carga en un hilo separado para no bloquear la UI
        int finalIdUsuario = idUsuario;
        new Thread(() -> {
            List<Object[]> tempLista = new ArrayList<>();
            if ("CLIENTE".equalsIgnoreCase(tipoUsuario)) {
                // Si es CLIENTE, se esperan 5 elementos en el Object[] de SolicitudDAO
                tempLista = solicitudDAO.obtenerSolicitudesAceptadasPorCliente(finalIdUsuario);
            } else {
                // Si es TRABAJADOR, se esperan 4 elementos en el Object[] de SolicitudDAO
                tempLista = solicitudDAO.obtenerSolicitudesAceptadasPorTrabajador(finalIdUsuario);
            }

            // Actualizar la UI en el hilo principal
            final List<Object[]> finalTempLista = tempLista;
            runOnUiThread(() -> {
                if (finalTempLista != null && !finalTempLista.isEmpty()) {
                    listaSolicitudesAceptadas = finalTempLista;
                    tvNoSolicitudesAceptadas.setVisibility(View.GONE);
                    recyclerViewSolicitudesAceptadas.setVisibility(View.VISIBLE);

                    if (adapter == null) {
                        // Pasa 'this' como listener para OnSolicitudInteractionListener
                        adapter = new SolicitudAceptadaAdapter(listaSolicitudesAceptadas, this);
                        recyclerViewSolicitudesAceptadas.setAdapter(adapter);
                    } else {
                        adapter.setListaSolicitudes(listaSolicitudesAceptadas);
                    }
                    Log.d("ListadoAceptadas", "Solicitudes aceptadas cargadas: " + listaSolicitudesAceptadas.size());
                } else {
                    tvNoSolicitudesAceptadas.setText("No tienes solicitudes de trabajo aceptadas en este momento.");
                    tvNoSolicitudesAceptadas.setVisibility(View.VISIBLE);
                    recyclerViewSolicitudesAceptadas.setVisibility(View.GONE);
                    Log.d("ListadoAceptadas", "No hay solicitudes aceptadas para este usuario.");
                }
            });
        }).start();
    }

    @Override
    public void onWhatsappClick(String phoneNumber) {
        // Implementación del método de la interfaz para abrir WhatsApp en el navegador
        Log.d("WhatsappClick", "Intentando abrir WhatsApp en el navegador con número: " + phoneNumber);
        try {
            String cleanedPhoneNumber = phoneNumber.replaceAll("[^0-9]", "");
            String url = "https://wa.me/" + cleanedPhoneNumber;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error al abrir WhatsApp en el navegador.", Toast.LENGTH_SHORT).show();
            Log.e("WhatsappClick", "Excepción al intentar abrir WhatsApp en el navegador: " + e.getMessage(), e);
        }
    }

    @Override
    public void onGuardarCalificacionClick(int solicitudId, int puntuacion, String comentario) {
        // Este método se llama desde el adaptador cuando el cliente guarda una calificación
        Log.d("Calificacion", "Guardando calificación para Solicitud ID: " + solicitudId +
                ", Puntuación: " + puntuacion + ", Comentario: " + comentario);

        // Obtener el ID del cliente actual para guardarlo con la calificación
        int idCliente;
        try {
            idCliente = Integer.parseInt(idUsuarioActual);
        } catch (NumberFormatException e) {
            Log.e("Calificacion", "Error al obtener ID de cliente para calificación: " + e.getMessage());
            Toast.makeText(this, "Error: No se pudo obtener su ID para guardar la calificación.", Toast.LENGTH_LONG).show();
            return;
        }

        // Ejecutar la operación de base de datos en un hilo secundario
        new Thread(() -> {
            long resultado = solicitudDAO.insertarCalificacion(solicitudId, idCliente, puntuacion, comentario);

            runOnUiThread(() -> {
                if (resultado != -1) { // La calificación se insertó correctamente si el resultado no es -1
                    Toast.makeText(ListadoSolicitudesAceptadasActivity.this,
                            "Calificación guardada correctamente.",
                            Toast.LENGTH_SHORT).show();
                    // Recargar la lista para que la tarjeta de la solicitud calificada
                    // ya no muestre el RatingBar (o deshabilitarlo si es la lógica deseada)
                    cargarSolicitudesAceptadas();
                } else {
                    Toast.makeText(ListadoSolicitudesAceptadasActivity.this,
                            "Error al guardar la calificación. Intente de nuevo.",
                            Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conexion != null) {
            conexion.close();
        }
    }
}
