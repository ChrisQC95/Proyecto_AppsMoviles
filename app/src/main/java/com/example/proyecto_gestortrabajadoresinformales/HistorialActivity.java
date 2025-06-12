package com.example.proyecto_gestortrabajadoresinformales;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_gestortrabajadoresinformales.Propuesta;
import com.example.proyecto_gestortrabajadoresinformales.PropuestaDAO;
import com.example.proyecto_gestortrabajadoresinformales.R;
import com.example.proyecto_gestortrabajadoresinformales.PropuestaActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class HistorialActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private String usuarioId;
    private FloatingActionButton fabVerListadoPropuestas;
    private Conexion conexion;

    // Declara el DAO a nivel de clase
    private PropuestaDAO propuestaDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historial_propuestas_trabajador);
        conexion = new Conexion(this);

        // CAMBIO AQUI: Inicializa PropuestaDAO pasándole la instancia de 'conexion'
        propuestaDAO = new PropuestaDAO(conexion);
        tableLayout = findViewById(R.id.dynamic_table);
        usuarioId = getIntent().getStringExtra("usuarioId");
        fabVerListadoPropuestas = findViewById(R.id.fabVerListadoPropuestas);

        cargarPropuestas();
        fabVerListadoPropuestas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistorialActivity.this, ListadoPropuestasActivity.class);
                intent.putExtra("ocultar_btn_aceptadas", true); // <-- Nombre correcto del extra
                startActivity(intent);
            }
        });
    }

    private void cargarPropuestas() {
        PropuestaDAO propuestaDAO = new PropuestaDAO(conexion);
        List<Propuesta> propuestas = propuestaDAO.obtenerPropuestasPorUsuarioId(Integer.parseInt(usuarioId));

        for (Propuesta propuesta : propuestas) {
            TableRow row = new TableRow(this);
            row.setPadding(8, 8, 8, 8);

            TextView servicio = crearTextView(this, propuesta.getTitulo());
            TextView precio = crearTextView(this, String.valueOf(propuesta.getPrecio()));
            Button btnEditar = new Button(this);
            btnEditar.setText("Editar");

            // Modificar la funcionalidad del botón editar
            btnEditar.setOnClickListener(v -> {
                Intent intent = new Intent(this, PropuestaActivity.class);
                // Pasar todos los datos de la propuesta
                intent.putExtra("usuarioId", usuarioId);
                intent.putExtra("propuestaId", propuesta.getId());
                intent.putExtra("titulo", propuesta.getTitulo());
                intent.putExtra("descripcion", propuesta.getDescripcion());
                intent.putExtra("precio", propuesta.getPrecio());
                intent.putExtra("tipoServicio", propuesta.getTipo_servicio());
                intent.putExtra("disponibilidad", propuesta.getDisponibilidad());
                intent.putExtra("modo", "editar"); // Para indicar que es modo edición
                startActivity(intent);
            });

            row.addView(servicio);
            row.addView(precio);
            row.addView(btnEditar);

            tableLayout.addView(row);
        }
    }

    private TextView crearTextView(Context context, String texto) {
        TextView textView = new TextView(context);
        textView.setText(texto);
        textView.setPadding(8, 8, 8, 8);
        return textView;
    }
}