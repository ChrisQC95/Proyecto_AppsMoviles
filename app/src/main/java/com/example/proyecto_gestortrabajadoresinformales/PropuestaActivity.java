package com.example.proyecto_gestortrabajadoresinformales;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class PropuestaActivity extends AppCompatActivity {

    private EditText txtTitulo, txtDescripcion;
    private Spinner spinnerDisponibilidad;
    private Button btnGuardar, btnCancelar;
    private String idTrabajador; // ID recibido desde Login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creacion_propuesta_trabajo);

        txtTitulo = findViewById(R.id.txtTitulo);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        spinnerDisponibilidad = findViewById(R.id.spinner6);
        btnGuardar = findViewById(R.id.button10);
        btnCancelar = findViewById(R.id.button11); // Botón Cancelar

        idTrabajador = getIntent().getStringExtra("usuarioId");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"No Disponible (0)", "Disponible (1)"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDisponibilidad.setAdapter(adapter);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarPropuesta();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Regresa a la actividad anterior
            }
        });
    }

    private void guardarPropuesta() {
        String titulo = txtTitulo.getText().toString().trim();
        String descripcion = txtDescripcion.getText().toString().trim();
        int disponibilidad = spinnerDisponibilidad.getSelectedItemPosition();

        if (titulo.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int usuarioId = Integer.parseInt(idTrabajador);

        Propuesta propuesta = new Propuesta(usuarioId, titulo, 0.0, descripcion, 0, disponibilidad, 0);
        PropuestaDAO propuestaDAO = new PropuestaDAO(this);
        long resultado = propuestaDAO.insertarPropuesta(propuesta);

        if (resultado != -1) {
            Toast.makeText(this, "Propuesta guardada correctamente", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar propuesta", Toast.LENGTH_SHORT).show();
        }
    }
}
