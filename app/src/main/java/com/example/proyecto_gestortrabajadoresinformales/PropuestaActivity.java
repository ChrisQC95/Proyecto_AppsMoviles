package com.example.proyecto_gestortrabajadoresinformales;

import android.content.Intent;
import android.content.SharedPreferences; // Importar SharedPreferences para obtener el ID del usuario
import android.os.Bundle;
import android.util.Log; // Importar Log para depuración
import android.view.View;
import android.widget.*; // Importar todos los widgets necesarios

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_gestortrabajadoresinformales.beans.TipoServicio;
import com.example.proyecto_gestortrabajadoresinformales.consultas.TipoServicioDAO;

import java.util.ArrayList;
import java.util.List;

public class PropuestaActivity extends AppCompatActivity {
    private Integer propuestaId;
    private boolean modoEdicion = false;
    private EditText txtTitulo, txtDescripcion, txtPrecio;
    private Spinner spinnerDisponibilidad, spinnerTipoServicio;
    private Button btnGuardar, btnCancelar;
    private ImageButton btnVerSolicitudes; // Declaración del ImageButton de notificaciones

    private String idTrabajador; // ID del trabajador logueado, obtenido de SharedPreferences

    private List<TipoServicio> listaTipoServicios;
    private Conexion conexion;
    private PropuestaDAO propuestaDAO;
    private TipoServicioDAO tipoServicioDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creacion_propuesta_trabajo); // Tu layout XML para crear/editar propuestas

        // Inicializar la conexión a la base de datos (usando tu método actual de instanciación)
        conexion = new Conexion(this);
        propuestaDAO = new PropuestaDAO(conexion);
        tipoServicioDAO = new TipoServicioDAO(conexion);

        // Vincular vistas de la propuesta
        txtTitulo = findViewById(R.id.txtTitulo);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        spinnerDisponibilidad = findViewById(R.id.spinner6);
        spinnerTipoServicio = findViewById(R.id.spinnerTipoServicioPT);
        btnGuardar = findViewById(R.id.button10);
        btnCancelar = findViewById(R.id.button11);
        txtPrecio = findViewById(R.id.txtPrecio);

        // Vincular el ImageButton de notificaciones
        btnVerSolicitudes = findViewById(R.id.btnVerSolicitudes); // Asegúrate de que este ID sea correcto en tu XML

        // Obtener el ID del trabajador logueado de SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE);
        idTrabajador = sharedPref.getString("user_id", "-1"); // Obtener el ID del trabajador de SharedPreferences

        // Validar si el ID del trabajador es válido (para evitar errores en la creación de propuestas y solicitudes)
        if (idTrabajador.equals("-1")) {
            Toast.makeText(this, "Error: No se pudo obtener el ID del trabajador. Por favor, inicie sesión.", Toast.LENGTH_LONG).show();
            // Considerar deshabilitar funcionalidades o redirigir si el ID del trabajador es esencial.
            btnGuardar.setEnabled(false); // Deshabilitar guardar si no hay ID de trabajador
            btnVerSolicitudes.setEnabled(false); // Deshabilitar botón de solicitudes
            Log.e("PropuestaActivity", "ID de trabajador no disponible. Algunas funcionalidades deshabilitadas.");
        }


        // Configurar Spinner de Disponibilidad
        ArrayAdapter<String> adapterDisponibilidad = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"No Disponible (0)", "Disponible (1)"}
        );
        adapterDisponibilidad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDisponibilidad.setAdapter(adapterDisponibilidad);

        // Configurar Spinner de Tipo de Servicio
        cargarTipoServiciosDesdeBD();

        // Verificar modo de edición o creación
        if (getIntent().hasExtra("modo") && getIntent().getStringExtra("modo").equals("editar")) {
            modoEdicion = true;
            propuestaId = getIntent().getIntExtra("propuestaId", -1);
            if (propuestaId != -1) {
                cargarDatosPropuesta(); // Cargar datos si estamos en modo edición
            } else {
                Toast.makeText(this, "Error: ID de propuesta para edición no válido.", Toast.LENGTH_SHORT).show();
                modoEdicion = false; // Forzar a modo creación si ID inválido
            }
        }

        // Configurar listeners de botones
        btnGuardar.setOnClickListener(view -> guardarPropuesta());
        btnCancelar.setOnClickListener(view -> finish());

        // Configurar el ClickListener para el botón de "Ver Solicitudes"
        btnVerSolicitudes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PropuestaActivity", "Botón 'Ver Solicitudes' clickeado.");
                if (!idTrabajador.equals("-1")) { // Solo si el ID del trabajador es válido
                    Intent intent = new Intent(PropuestaActivity.this, ListadoSolicitudesTrabajadorActivity.class);
                    // No necesitamos pasar el ID del trabajador por Intent si ListadoSolicitudesTrabajadorActivity
                    // ya lo obtiene de SharedPreferences.
                    startActivity(intent);
                } else {
                    Toast.makeText(PropuestaActivity.this, "Inicie sesión como trabajador para ver las solicitudes.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Toast de prueba inicial (puedes quitarlo una vez que todo funcione)
        Toast.makeText(this, "Actividad de Propuesta cargada. ID Trabajador: " + idTrabajador, Toast.LENGTH_SHORT).show();
    }

    private void cargarDatosPropuesta() {
        // Asumo que los datos de la propuesta se pasan por Intent al modo edición
        txtTitulo.setText(getIntent().getStringExtra("titulo"));
        txtDescripcion.setText(getIntent().getStringExtra("descripcion"));
        txtPrecio.setText(String.valueOf(getIntent().getDoubleExtra("precio", 0.0)));
        spinnerDisponibilidad.setSelection(getIntent().getIntExtra("disponibilidad", 0));

        // Para spinnerTipoServicio, necesitas encontrar la posición correcta en la lista
        int tipoServicioId = getIntent().getIntExtra("tipoServicio", -1);
        if (tipoServicioId != -1 && listaTipoServicios != null) {
            for (int i = 0; i < listaTipoServicios.size(); i++) {
                // Asumiendo que TipoServicio.getId() retorna un String y debe ser Integer
                if (Integer.parseInt(listaTipoServicios.get(i).getId()) == tipoServicioId) {
                    spinnerTipoServicio.setSelection(i);
                    break;
                }
            }
        }
    }

    private void guardarPropuesta() {
        String titulo = txtTitulo.getText().toString().trim();
        String descripcion = txtDescripcion.getText().toString().trim();
        int disponibilidad = spinnerDisponibilidad.getSelectedItemPosition();
        String precioStr = txtPrecio.getText().toString().trim();
        int tipoServicio = -1; // Valor por defecto
        if (spinnerTipoServicio.getSelectedItem() != null && listaTipoServicios != null && !listaTipoServicios.isEmpty()) {
            // Asegurarse de que el ID del tipo de servicio se obtenga correctamente
            tipoServicio = Integer.parseInt(listaTipoServicios.get(spinnerTipoServicio.getSelectedItemPosition()).getId());
        } else {
            Toast.makeText(this, "Seleccione un tipo de servicio válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (titulo.isEmpty() || descripcion.isEmpty() || precioStr.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos obligatorios (Título, Descripción, Precio).", Toast.LENGTH_SHORT).show();
            return;
        }
        double precio;
        try {
            precio = Double.parseDouble(precioStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese un precio válido.", Toast.LENGTH_SHORT).show();
            Log.e("PropuestaActivity", "Error al parsear precio: " + e.getMessage());
            return;
        }
        int usuarioId;
        try {
            usuarioId = Integer.parseInt(idTrabajador);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Error: ID de trabajador inválido para guardar propuesta.", Toast.LENGTH_LONG).show();
            Log.e("PropuestaActivity", "Error al parsear ID de trabajador: " + e.getMessage());
            return;
        }


        Propuesta propuesta = new Propuesta(
                usuarioId,
                titulo,
                precio,
                descripcion,
                tipoServicio, // ID del tipo de servicio
                disponibilidad,
                0 // Calificación promedio inicial
        );

        long resultado;

        if (modoEdicion) {
            propuesta.setId(propuestaId); // Asignar el ID de la propuesta para actualizar
            resultado = propuestaDAO.actualizarPropuesta(propuesta);
        } else {
            resultado = propuestaDAO.insertarPropuesta(propuesta);
        }

        if (resultado != -1) {
            Toast.makeText(this, "Propuesta " +
                    (modoEdicion ? "actualizada" : "guardada") +
                    " correctamente", Toast.LENGTH_SHORT).show();

            // Después de guardar, puedes redirigir al historial o a la pantalla principal del trabajador
            Intent intent = new Intent(this, HistorialActivity.class); // O TrabajadorInicioActivity.class
            intent.putExtra("usuarioId", idTrabajador); // Pasar el ID del trabajador para que HistorialActivity lo filtre
            startActivity(intent);
            finish(); // Cierra la actividad de creación/edición
        } else {
            Toast.makeText(this, "Error al " +
                            (modoEdicion ? "actualizar" : "guardar") +
                            " propuesta. Por favor, intente de nuevo.",
                    Toast.LENGTH_LONG).show();
            Log.e("PropuestaActivity", "Error de BD al guardar/actualizar propuesta. Resultado: " + resultado + " ID Trabajador: " + usuarioId);
        }
    }

    private void cargarTipoServiciosDesdeBD() {
        // Las operaciones de base de datos deben hacerse en un hilo secundario
        new Thread(() -> {
            listaTipoServicios = tipoServicioDAO.obtenerTodosLosTipoServicios();
            // Vuelve al hilo de UI para actualizar el Spinner
            runOnUiThread(() -> {
                if (listaTipoServicios != null && !listaTipoServicios.isEmpty()) {
                    List<String> nombresServicios = new ArrayList<>();
                    for (TipoServicio tipo : listaTipoServicios) {
                        nombresServicios.add(tipo.getNombre());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            PropuestaActivity.this, // Usar PropuestaActivity.this como contexto
                            android.R.layout.simple_spinner_item,
                            nombresServicios
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTipoServicio.setAdapter(adapter);

                    // Si estás en modo edición y ya cargaste la propuesta, selecciona el tipo de servicio
                    if (modoEdicion && getIntent().hasExtra("tipoServicio")) {
                        cargarDatosPropuesta(); // Esto re-selecciona el spinner si el ID está presente
                    }
                } else {
                    Toast.makeText(PropuestaActivity.this, "No se encontraron tipos de servicio.", Toast.LENGTH_SHORT).show();
                    Log.w("PropuestaActivity", "Lista de tipos de servicio vacía o nula.");
                }
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cierra la conexión cuando la actividad se destruye.
        // Si usas el patrón Singleton completo para Conexion, este cierre debe ser manejado globalmente
        // y podrías no necesitar esta línea aquí.
        if (conexion != null) {
            conexion.close();
        }
    }
}