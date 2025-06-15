package com.example.proyecto_gestortrabajadoresinformales; // Asegúrate de que el paquete sea correcto

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_gestortrabajadoresinformales.Propuesta;
import com.example.proyecto_gestortrabajadoresinformales.beans.Distrito;
import com.example.proyecto_gestortrabajadoresinformales.beans.Perfil;
import com.example.proyecto_gestortrabajadoresinformales.beans.Solicitud; // Necesitarás crear esta clase
import com.example.proyecto_gestortrabajadoresinformales.consultas.DistritoDAO;
import com.example.proyecto_gestortrabajadoresinformales.consultas.PerfilDAO;
import com.example.proyecto_gestortrabajadoresinformales.consultas.SolicitudDAO; // Necesitarás crear esta clase

import java.text.DecimalFormat;

public class DetallePropuestaActivity extends AppCompatActivity {

    public static final String EXTRA_PROPUESTA = "extra_propuesta"; // Clave para pasar la propuesta en el Intent

    private Propuesta propuestaSeleccionada;
    private TextView tvTituloPropuesta, tvCalificacionPropuesta, tvDescripcionPropuesta,
            tvUbicacionPropuesta, tvOficioPropuesta, tvPrecioPropuesta;
    private Button btnHacerSolicitud;

    private Conexion conexion;
    private PerfilDAO perfilDAO;
    private DistritoDAO distritoDAO;
    private SolicitudDAO solicitudDAO; // Lo usaremos en el siguiente paso

    private String idUsuarioClienteActual; // CAMBIADO a String

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.propuesta_detalle);

        // Inicializar vistas
        tvTituloPropuesta = findViewById(R.id.tvTituloPropuesta);
        tvCalificacionPropuesta = findViewById(R.id.tvCalificacionPropuesta);
        tvDescripcionPropuesta = findViewById(R.id.tvDescripcionPropuesta);
        tvUbicacionPropuesta = findViewById(R.id.tvUbicacionPropuesta);
        tvOficioPropuesta = findViewById(R.id.tvOficioPropuesta);
        tvPrecioPropuesta = findViewById(R.id.tvPrecioPropuesta);
        btnHacerSolicitud = findViewById(R.id.btnHacerSolicitud);

        // Inicializar la instancia de Conexion
        conexion = new Conexion(this);

        // Inicializar DAOs pasándoles la instancia de Conexion
        perfilDAO = new PerfilDAO(conexion);
        distritoDAO = new DistritoDAO(conexion);
        // SolicitudDAO todavía no existe, lo crearemos en el siguiente paso.
        // Lo inicializamos aquí para que esté disponible en hacerSolicitudDeTrabajo()
        solicitudDAO = new SolicitudDAO(conexion);


        // 1. Obtener la propuesta del Intent
        if (getIntent().hasExtra(EXTRA_PROPUESTA)) {
            propuestaSeleccionada = getIntent().getParcelableExtra(EXTRA_PROPUESTA);
            if (propuestaSeleccionada != null) {
                cargarDatosPropuesta();
            } else {
                Toast.makeText(this, "Error al cargar los detalles de la propuesta.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "No se recibió información de la propuesta.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 2. Obtener el ID del usuario cliente actual
        idUsuarioClienteActual = obtenerIdUsuarioClienteActual(); // Obtiene String
        if (idUsuarioClienteActual == null || idUsuarioClienteActual.isEmpty() || idUsuarioClienteActual.equals("-1")) { // Ajuste para String
            Toast.makeText(this, "No se pudo obtener el ID del usuario cliente. Por favor, inicie sesión.", Toast.LENGTH_LONG).show();
            btnHacerSolicitud.setEnabled(false);
        }

        // 3. Configurar el Listener para el botón de solicitud
        btnHacerSolicitud.setOnClickListener(v -> hacerSolicitudDeTrabajo());
    }

    private void cargarDatosPropuesta() {
        tvTituloPropuesta.setText(propuestaSeleccionada.getTitulo());
        tvDescripcionPropuesta.setText(propuestaSeleccionada.getDescripcion());
        // La calificación es Integer, lo convertimos a String
        tvCalificacionPropuesta.setText(String.valueOf(propuestaSeleccionada.getCalificacion()));

        DecimalFormat df = new DecimalFormat("S/. ");
        tvPrecioPropuesta.setText(df.format(propuestaSeleccionada.getPrecio()));

        // Cargar el nombre del tipo de servicio (ya lo tienes en Propuesta bean)
        if (propuestaSeleccionada.getTipoServicioNombre() != null && !propuestaSeleccionada.getTipoServicioNombre().isEmpty()) {
            tvOficioPropuesta.setText(propuestaSeleccionada.getTipoServicioNombre());
        } else {
            // Fallback si por alguna razón el nombre del servicio no está en la propuesta
            tvOficioPropuesta.setText("Servicio Desconocido");
        }

        // Cargar el distrito del trabajador en un hilo secundario
        new Thread(new Runnable() {
            @Override
            public void run() {
                String nombreDistrito = "Ubicación Desconocida";
                // Asegúrate de que usuarioId de Propuesta es String (se asumió Integer antes, revisa Propuesta.java)
                // Si Propuesta.usuarioId es Integer, conviértelo a String aquí: String.valueOf(propuestaSeleccionada.getUsuarioId())
                String idTrabajador = String.valueOf(propuestaSeleccionada.getUsuarioId()); // Convertir a String si Propuesta.usuarioId es Integer

                // Si Perfil.usuarioId en tu bean es String, entonces este es el tipo correcto
                if (idTrabajador != null && !idTrabajador.isEmpty()) {
                    Perfil perfilTrabajador = perfilDAO.obtenerPerfilPorUsuarioId(idTrabajador);
                    if (perfilTrabajador != null && perfilTrabajador.getDistritoId() != null && !perfilTrabajador.getDistritoId().isEmpty()) {
                        Distrito distrito = distritoDAO.obtenerDistritoPorId(perfilTrabajador.getDistritoId()); // distritoId ya es String
                        if (distrito != null) {
                            nombreDistrito = distrito.getNombre();
                        }
                    }
                }

                final String finalNombreDistrito = nombreDistrito;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvUbicacionPropuesta.setText(finalNombreDistrito);
                    }
                });
            }
        }).start();
    }

    // Método para obtener el ID del usuario cliente actual
    private String obtenerIdUsuarioClienteActual() {
        SharedPreferences sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        // Usamos getString y un valor por defecto que indique que no se encontró (ej., "-1")
        return sharedPref.getString("user_id", "-1");
    }

    private void hacerSolicitudDeTrabajo() {
        if (idUsuarioClienteActual == null || idUsuarioClienteActual.isEmpty() || idUsuarioClienteActual.equals("-1")) {
            Toast.makeText(this, "No se pudo realizar la solicitud: ID de cliente no disponible.", Toast.LENGTH_LONG).show();
            return;
        }
        // Asegúrate de que Propuesta.id es Integer en tu bean Propuesta.java
        if (propuestaSeleccionada == null || propuestaSeleccionada.getId() == null) {
            Toast.makeText(this, "No se pudo realizar la solicitud: Información de la propuesta no disponible.", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Crear una nueva Solicitud
                // Asegúrate de que tu constructor de Solicitud acepte Integer para IDs si así es en la DB.
                // Si SOLICITUD_USUARIO_ID y SOLICITUD_PROPUESTA_ID son INTEGER en la DB,
                // deberás convertir idUsuarioClienteActual y propuestaSeleccionada.getId() a Integer.
                // Asumiendo que los IDs en la DB son INTEGER:
                Solicitud solicitud = new Solicitud(
                        Integer.parseInt(idUsuarioClienteActual), // Convertir a Integer
                        propuestaSeleccionada.getId(), // Ya es Integer
                        "" // Mensaje vacío por ahora
                );

                long idSolicitudInsertada = solicitudDAO.insertarSolicitud(solicitud);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (idSolicitudInsertada != -1) {
                            mostrarAlertaSolicitudExitosa();
                        } else {
                            Toast.makeText(DetallePropuestaActivity.this, "Error al enviar la solicitud. Intente de nuevo.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void mostrarAlertaSolicitudExitosa() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Solicitud enviada con éxito");
        builder.setMessage("Tu solicitud de trabajo ha sido enviada al trabajador. Mantente atento a las notificaciones.");

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            finish();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conexion != null) {
            conexion.close();
        }
    }
}