package com.example.proyecto_gestortrabajadoresinformales; // Ajusta el paquete

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.example.proyecto_gestortrabajadoresinformales.Propuesta;
import com.example.proyecto_gestortrabajadoresinformales.PropuestaDAO;
import com.example.proyecto_gestortrabajadoresinformales.TipoServicio;
import com.example.proyecto_gestortrabajadoresinformales.TipoServicioDAO;

public class ListadoPropuestasActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPropuestas;
    private PropuestaAdapter propuestaAdapter;
    private PropuestaDAO propuestaDAO;
    private List<Propuesta> listaPropuestas;

    private Spinner spinnerTipoServicio;
    private TipoServicioDAO tipoServicioDAO;
    private List<TipoServicio> listaTiposServicio;
    private int selectedTipoServicioId = -1; // -1 para "Todos los servicios"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listado_propuestas); // Asegúrate de usar el layout con el Spinner

        propuestaDAO = new PropuestaDAO(this);
        tipoServicioDAO = new TipoServicioDAO(this);

        recyclerViewPropuestas = findViewById(R.id.recyclerViewPropuestas);
        recyclerViewPropuestas.setLayoutManager(new LinearLayoutManager(this));

        listaPropuestas = new ArrayList<>();
        propuestaAdapter = new PropuestaAdapter(listaPropuestas);
        recyclerViewPropuestas.setAdapter(propuestaAdapter);

        spinnerTipoServicio = findViewById(R.id.spinnerTipoServicio);
        cargarTiposDeServicioEnSpinner();

        spinnerTipoServicio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TipoServicio tipoSeleccionado = (TipoServicio) parent.getItemAtPosition(position);
                selectedTipoServicioId = tipoSeleccionado.getId();
                cargarPropuestasDisponiblesFiltradas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });

        // Carga inicial de todas las propuestas disponibles
        cargarPropuestasDisponiblesFiltradas();
    }

    private void cargarTiposDeServicioEnSpinner() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<TipoServicio> tiposDeServicio = tipoServicioDAO.obtenerTodosLosTiposDeServicio();
                listaTiposServicio = new ArrayList<>();
                listaTiposServicio.add(new TipoServicio(-1, "Todos los servicios")); // Opción "Todos"
                if (tiposDeServicio != null) {
                    listaTiposServicio.addAll(tiposDeServicio);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<TipoServicio> adapter = new ArrayAdapter<>(
                                ListadoPropuestasActivity.this,
                                android.R.layout.simple_spinner_item,
                                listaTiposServicio
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerTipoServicio.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }

    private void cargarPropuestasDisponiblesFiltradas() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Propuesta> propuestas;
                if (selectedTipoServicioId == -1) {
                    propuestas = propuestaDAO.obtenerPropuestasDisponibles();
                } else {
                    propuestas = propuestaDAO.obtenerPropuestasDisponiblesPorTipoServicio(selectedTipoServicioId);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (propuestas != null && !propuestas.isEmpty()) {
                            propuestaAdapter.setPropuestas(propuestas);
                        } else {
                            propuestaAdapter.setPropuestas(new ArrayList<>()); // Limpiar la lista si no hay resultados
                            Toast.makeText(ListadoPropuestasActivity.this, "No hay propuestas para este tipo de servicio.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
}