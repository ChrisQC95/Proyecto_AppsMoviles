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

import com.example.proyecto_gestortrabajadoresinformales.beans.Distrito; // Importar la clase Distrito
import com.example.proyecto_gestortrabajadoresinformales.beans.TipoServicio;
import com.example.proyecto_gestortrabajadoresinformales.consultas.DistritoDAO; // Importar la clase DistritoDAO
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

    // Nuevas variables para el filtro por Distrito
    private Spinner spinnerDistrito;
    private DistritoDAO distritoDAO;
    private List<Distrito> listaDistritos;
    private String selectedDistritoId = "-1"; // "-1" para "Todos los distritos"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listado_propuestas); // Asegúrate de usar el layout con los Spinners

        propuestaDAO = new PropuestaDAO(this);
        tipoServicioDAO = new TipoServicioDAO(this);
        distritoDAO = new DistritoDAO(this); // Inicializar DistritoDAO

        recyclerViewPropuestas = findViewById(R.id.recyclerViewPropuestas);
        recyclerViewPropuestas.setLayoutManager(new LinearLayoutManager(this));

        listaPropuestas = new ArrayList<>();

        spinnerTipoServicio = findViewById(R.id.spinnerTipoServicio);
        spinnerDistrito = findViewById(R.id.spinnerDistrito); // Enlazar el nuevo Spinner

        cargarTiposDeServicioEnSpinner();
        cargarDistritosEnSpinner(); // Cargar los distritos

        // Listener para el Spinner de Tipo de Servicio
        spinnerTipoServicio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TipoServicio tipoSeleccionado = (TipoServicio) parent.getItemAtPosition(position);
                selectedTipoServicioId = tipoSeleccionado.getId();
                cargarPropuestasDisponiblesFiltradas(); // Volver a cargar las propuestas con el nuevo filtro
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
                cargarPropuestasDisponiblesFiltradas(); // Volver a cargar las propuestas con el nuevo filtro
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });

        // NOTA: El adapter para el RecyclerView se inicializa después de cargar los Spinners
        // para asegurar que tengamos las listas de Tipos de Servicio/Distritos si son necesarios en el adapter.
        // Aquí no es estrictamente necesario que PropuestaAdapter tenga listaTiposServicio o listaDistritos,
        // pero lo mantengo como lo tenías si es que lo usas para mostrar nombres en la lista.
    }

    private void cargarTiposDeServicioEnSpinner() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<TipoServicio> tiposDeServicio = tipoServicioDAO.obtenerTodosLosTipoServicios();
                listaTiposServicio = new ArrayList<>();
                listaTiposServicio.add(0, new TipoServicio("-1", "Todos los servicios")); // Opción "Todos" al inicio
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

                        // Inicializar el adapter del RecyclerView aquí si aún no se ha hecho
                        if (propuestaAdapter == null) {
                            propuestaAdapter = new PropuestaAdapter(listaPropuestas, listaTiposServicio); // Puedes pasar listaTiposServicio y listaDistritos si tu adapter los necesita para mostrar información
                            recyclerViewPropuestas.setAdapter(propuestaAdapter);
                        }
                        // La carga inicial de propuestas se hará después de que ambos spinners estén listos
                        // para que los filtros iniciales "-1" se apliquen correctamente.
                    }
                });
            }
        }).start();
    }

    // Nuevo método: Cargar distritos en el Spinner
    private void cargarDistritosEnSpinner() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Distrito> distritos = distritoDAO.obtenerTodosLosDistritos();
                listaDistritos = new ArrayList<>();
                listaDistritos.add(0, new Distrito("-1", "Todos los distritos")); // Opción "Todos" al inicio
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

                        // Cargar propuestas inicialmente después de que ambos spinners estén configurados.
                        // Esto asegura que la primera carga se haga con los filtros por defecto ("-1").
                        cargarPropuestasDisponiblesFiltradas();
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
                // Usar el nuevo método en PropuestaDAO que filtra por ambos criterios
                propuestas = propuestaDAO.obtenerPropuestasDisponiblesFiltradas(selectedTipoServicioId, selectedDistritoId);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (propuestas != null && !propuestas.isEmpty()) {
                            propuestaAdapter.setPropuestas(propuestas);
                        } else {
                            propuestaAdapter.setPropuestas(new ArrayList<>()); // Limpiar la lista si no hay resultados
                            Toast.makeText(ListadoPropuestasActivity.this, "No hay propuestas con los filtros seleccionados.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
}