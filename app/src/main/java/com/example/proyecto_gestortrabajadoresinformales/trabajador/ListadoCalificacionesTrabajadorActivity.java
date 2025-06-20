package com.example.proyecto_gestortrabajadoresinformales.trabajador;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestortrabajadoresinformales.R;
import com.example.proyecto_gestortrabajadoresinformales.beans.Calificacion;
import com.example.proyecto_gestortrabajadoresinformales.consultas.CalificacionDAO;
import com.example.proyecto_gestortrabajadoresinformales.consultas.Conexion;

import java.util.List;

public class ListadoCalificacionesTrabajadorActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CalificacionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listado_calificaciones_trabajador);

        recyclerView = findViewById(R.id.recyclerViewCalificaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        int idTrabajador = obtenerIdTrabajadorActual();
        CalificacionDAO calificacionDAO = new CalificacionDAO(new Conexion(this));
        List<Calificacion> calificaciones = calificacionDAO.obtenerCalificacionesPorTrabajador(idTrabajador);

        adapter = new CalificacionAdapter(calificaciones);
        recyclerView.setAdapter(adapter);

        // Si no hay calificaciones, la lista queda vacía y no se muestra nada
    }

    private int obtenerIdTrabajadorActual() {
        // Ajusta según cómo guardes el ID del trabajador
        SharedPreferences sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String id = sharedPref.getString("user_id", "-1");
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
