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

import com.example.proyecto_gestortrabajadoresinformales.beans.Distrito;
import com.example.proyecto_gestortrabajadoresinformales.beans.TipoServicio;
import com.example.proyecto_gestortrabajadoresinformales.consultas.DistritoDAO;
import com.example.proyecto_gestortrabajadoresinformales.consultas.TipoServicioDAO;

import java.util.ArrayList;
import java.util.List;

public class ListadoPropuestasActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPropuestas;
    private PropuestaAdapter propuestaAdapter;
    private PropuestaDAO propuestaDAO;
    private List<Propuesta> listaPropuestas;

    private Spinner spinnerTipoServicio;
    private TipoServicioDAO tipoServicioDAO;
    private List<TipoServicio> listaTiposServicio;
    private String selectedTipoServicioId = "-1"; // "-1" para "Todos los servicios"

    private Spinner spinnerDistrito;
    private DistritoDAO distritoDAO;
    private List<Distrito> listaDistritos;
    private String selectedDistritoId = "-1"; // "-1" para "Todos los distritos"

    // Nuevas variables para el filtro por Calificación
    private Spinner spinnerCalificacion;
    private int selectedCalificacion = 0; // 0 para "Todas las calificaciones"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listado_propuestas);

        propuestaDAO = new PropuestaDAO(this);
        tipoServicioDAO = new TipoServicioDAO(this);
        distritoDAO = new DistritoDAO(this);

        recyclerViewPropuestas = findViewById(R.id.recyclerViewPropuestas);
        recyclerViewPropuestas.setLayoutManager(new LinearLayoutManager(this));

        listaPropuestas = new ArrayList<>();

        spinnerTipoServicio = findViewById(R.id.spinnerTipoServicio);
        spinnerDistrito = findViewById(R.id.spinnerDistrito);
        spinnerCalificacion = findViewById(R.id.spinnerCalificacion); // Enlazar el nuevo Spinner

        cargarTiposDeServicioEnSpinner();
        cargarDistritosEnSpinner();
        cargarCalificacionesEnSpinner(); // Nuevo método para cargar calificaciones

        // Listener para el Spinner de Tipo de Servicio
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

        // Listener para el Spinner de Distrito
        spinnerDistrito.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Distrito distritoSeleccionado = (Distrito) parent.getItemAtPosition(position);
                selectedDistritoId = distritoSeleccionado.getId();
                cargarPropuestasDisponiblesFiltradas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });

        // Listener para el Spinner de Calificación
        spinnerCalificacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String calificacionString = (String) parent.getItemAtPosition(position);
                if (calificacionString.equals("Todas las calificaciones")) {
                    selectedCalificacion = 0; // Representa no aplicar filtro de calificación
                } else {
                    selectedCalificacion = Integer.parseInt(calificacionString);
                }
                cargarPropuestasDisponiblesFiltradas(); // Volver a cargar las propuestas con el nuevo filtro
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cierra la base de datos cuando la actividad es destruida
        if (propuestaDAO != null) {
            // Necesitas un método para cerrar la base de datos en Conexion o PropuestaDAO
            // Por ejemplo, puedes añadir un método en Conexion como adminDB.closeDatabase();
            // o si prefieres, directamente adminDB.getWritableDatabase().close();
            // O mejor aún, haz que adminDB sea un Singleton si tu app lo usa en muchos lugares.
             // Si Conexion tiene un método close() público que cierra la DB.
        }
    }
    private void cargarTiposDeServicioEnSpinner() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<TipoServicio> tiposDeServicio = tipoServicioDAO.obtenerTodosLosTipoServicios();
                listaTiposServicio = new ArrayList<>();
                listaTiposServicio.add(0, new TipoServicio("-1", "Todos los servicios"));
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

                        if (propuestaAdapter == null) {
                            propuestaAdapter = new PropuestaAdapter(listaPropuestas, listaTiposServicio);
                            recyclerViewPropuestas.setAdapter(propuestaAdapter);
                        }
                    }
                });
            }
        }).start();
    }

    private void cargarDistritosEnSpinner() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Distrito> distritos = distritoDAO.obtenerTodosLosDistritos();
                listaDistritos = new ArrayList<>();
                listaDistritos.add(0, new Distrito("-1", "Todos los distritos"));
                if (distritos != null) {
                    listaDistritos.addAll(distritos);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<Distrito> adapter = new ArrayAdapter<>(
                                ListadoPropuestasActivity.this,
                                android.R.layout.simple_spinner_item,
                                listaDistritos
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerDistrito.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }

    // Nuevo método: Cargar calificaciones en el Spinner
    private void cargarCalificacionesEnSpinner() {
        List<String> calificaciones = new ArrayList<>();
        calificaciones.add("Todas las calificaciones");
        for (int i = 1; i <= 5; i++) {
            calificaciones.add(String.valueOf(i));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                calificaciones
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCalificacion.setAdapter(adapter);

        // Ya que todos los spinners se cargan asíncronamente y el adapter de RecyclerView
        // se inicializa en cargarTiposDeServicioEnSpinner, llamaremos a cargarPropuestasDisponiblesFiltradas
        // después de que todos los spinners estén potencialmente configurados (en cargarDistritosEnSpinner).
    }

    private void cargarPropuestasDisponiblesFiltradas() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Propuesta> propuestas;
                // Llamar al método actualizado de PropuestaDAO con el filtro de calificación
                propuestas = propuestaDAO.obtenerPropuestasDisponiblesFiltradas(selectedTipoServicioId, selectedDistritoId, selectedCalificacion);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (propuestas != null && !propuestas.isEmpty()) {
                            propuestaAdapter.setPropuestas(propuestas);
                        } else {
                            propuestaAdapter.setPropuestas(new ArrayList<>());
                            Toast.makeText(ListadoPropuestasActivity.this, "No hay propuestas con los filtros seleccionados.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
}