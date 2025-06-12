package com.example.proyecto_gestortrabajadoresinformales;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestortrabajadoresinformales.consultas.SolicitudDAO;

import java.util.List;

public class ListadoSolicitudesAceptadasActivity extends AppCompatActivity {

    private RecyclerView recyclerViewSolicitudesAceptadas;
    private TextView tvNoSolicitudesAceptadas;
    private SolicitudAceptadaAdapter adapter;
    private List<Object[]> listaSolicitudesAceptadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listado_solicitudes_aceptadas);

        recyclerViewSolicitudesAceptadas = findViewById(R.id.recyclerViewSolicitudesAceptadas);
        tvNoSolicitudesAceptadas = findViewById(R.id.tvNoSolicitudesAceptadas);

        recyclerViewSolicitudesAceptadas.setLayoutManager(new LinearLayoutManager(this));
        cargarSolicitudesAceptadas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarSolicitudesAceptadas();
    }

    private void cargarSolicitudesAceptadas() {
        SolicitudDAO solicitudDAO = new SolicitudDAO(new Conexion(this));
        // Obtén el id y tipo de usuario desde SharedPreferences
        String idUsuarioStr = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("user_id", "-1");
        String tipoUsuario = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("user_type", "TRABAJADOR");
        int idUsuario;
        try {
            idUsuario = Integer.parseInt(idUsuarioStr);
        } catch (NumberFormatException e) {
            idUsuario = -1;
        }

        // Llama al método correcto según el tipo de usuario
        if ("CLIENTE".equalsIgnoreCase(tipoUsuario)) {
            listaSolicitudesAceptadas = solicitudDAO.obtenerSolicitudesAceptadasPorCliente(idUsuario);
        } else {
            listaSolicitudesAceptadas = solicitudDAO.obtenerSolicitudesAceptadasPorTrabajador(idUsuario);
        }

        if (listaSolicitudesAceptadas == null || listaSolicitudesAceptadas.isEmpty()) {
            tvNoSolicitudesAceptadas.setVisibility(View.VISIBLE);
            recyclerViewSolicitudesAceptadas.setVisibility(View.GONE);
        } else {
            tvNoSolicitudesAceptadas.setVisibility(View.GONE);
            recyclerViewSolicitudesAceptadas.setVisibility(View.VISIBLE);

            if (adapter == null) {
                adapter = new SolicitudAceptadaAdapter(listaSolicitudesAceptadas);
                recyclerViewSolicitudesAceptadas.setAdapter(adapter);
            } else {
                adapter.setListaSolicitudes(listaSolicitudesAceptadas);
            }
        }
    }
}
