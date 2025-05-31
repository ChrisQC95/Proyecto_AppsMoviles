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

import java.util.List;

public class HistorialActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private String usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historial_propuestas_trabajador);

        tableLayout = findViewById(R.id.dynamic_table);
        usuarioId = getIntent().getStringExtra("usuarioId");

        cargarPropuestas();
    }

    private void cargarPropuestas() {
        PropuestaDAO propuestaDAO = new PropuestaDAO(this);
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