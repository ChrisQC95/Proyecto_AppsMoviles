package com.example.proyecto_gestortrabajadoresinformales;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyecto_gestortrabajadoresinformales.HistorialActivity;
import com.example.proyecto_gestortrabajadoresinformales.Propuesta;
import com.example.proyecto_gestortrabajadoresinformales.PropuestaDAO;
import com.example.proyecto_gestortrabajadoresinformales.R;

public class PropuestaActivity extends AppCompatActivity {
    private Integer propuestaId;
    private boolean modoEdicion = false;
    private EditText txtTitulo, txtDescripcion, txtPrecio;
    private Spinner spinnerDisponibilidad, spinnerTipoServicio; // Add new spinner
    private Button btnGuardar, btnCancelar;
    private String idTrabajador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creacion_propuesta_trabajo);

        txtTitulo = findViewById(R.id.txtTitulo);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        spinnerDisponibilidad = findViewById(R.id.spinner6);
        spinnerTipoServicio = findViewById(R.id.spinnerTipoServicioPT);
        btnGuardar = findViewById(R.id.button10);
        btnCancelar = findViewById(R.id.button11);
        txtPrecio = findViewById(R.id.txtPrecio);

        idTrabajador = getIntent().getStringExtra("usuarioId");

        ArrayAdapter<String> adapterDisponibilidad = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"No Disponible (0)", "Disponible (1)"}
        );
        adapterDisponibilidad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDisponibilidad.setAdapter(adapterDisponibilidad);

        // Configure new spinner
        ArrayAdapter<String> adapterTipoServicio = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Carpintería", "Plomería", "Electricidad", "Jardinería", "Otros"}
        );
        adapterTipoServicio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoServicio.setAdapter(adapterTipoServicio);

        if (getIntent().hasExtra("modo") && getIntent().getStringExtra("modo").equals("editar")) {
            modoEdicion = true;
            propuestaId = getIntent().getIntExtra("propuestaId", -1);
            cargarDatosPropuesta();
        }
        btnGuardar.setOnClickListener(view -> guardarPropuesta());

        btnCancelar.setOnClickListener(view -> finish());

        Toast.makeText(this, "Complete todos los campos" + idTrabajador, Toast.LENGTH_SHORT).show();
    }

    private void cargarDatosPropuesta() {
        // Cargar datos recibidos en los campos
        txtTitulo.setText(getIntent().getStringExtra("titulo"));
        txtDescripcion.setText(getIntent().getStringExtra("descripcion"));
        txtPrecio.setText(String.valueOf(getIntent().getDoubleExtra("precio", 0.0)));
        spinnerDisponibilidad.setSelection(getIntent().getIntExtra("disponibilidad", 0));
        spinnerTipoServicio.setSelection(getIntent().getIntExtra("tipoServicio", 0));
    }

    private void guardarPropuesta() {
        String titulo = txtTitulo.getText().toString().trim();
        String descripcion = txtDescripcion.getText().toString().trim();
        int disponibilidad = spinnerDisponibilidad.getSelectedItemPosition();
        String precioStr = txtPrecio.getText().toString().trim();
        int tipoServicio = spinnerTipoServicio.getSelectedItemPosition();

        if (titulo.isEmpty() || descripcion.isEmpty() || precioStr.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        double precio;
        try {
            precio = Double.parseDouble(precioStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese un precio válido", Toast.LENGTH_SHORT).show();
            return;
        }
        int usuarioId = Integer.parseInt(idTrabajador);

        Propuesta propuesta = new Propuesta(
                usuarioId,
                titulo,
                precio,
                descripcion,
                tipoServicio,
                disponibilidad,
                0
        );

        PropuestaDAO propuestaDAO = new PropuestaDAO(this);
        long resultado;

        if (modoEdicion) {
            propuesta.setId(propuestaId);
            resultado = propuestaDAO.actualizarPropuesta(propuesta);
        } else {
            resultado = propuestaDAO.insertarPropuesta(propuesta);
        }

        if (resultado != -1) {
            Toast.makeText(this, "Propuesta " +
                    (modoEdicion ? "actualizada" : "guardada") +
                    " correctamente", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, HistorialActivity.class);
            intent.putExtra("usuarioId", idTrabajador);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error al " +
                            (modoEdicion ? "actualizar" : "guardar") +
                            " propuesta: " + resultado + " ID: " + usuarioId,
                    Toast.LENGTH_SHORT).show();
        }
    }
}