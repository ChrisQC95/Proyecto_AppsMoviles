package com.example.proyecto_gestortrabajadoresinformales.trabajador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_gestortrabajadoresinformales.R;
import com.example.proyecto_gestortrabajadoresinformales.beans.Usuario;
import com.example.proyecto_gestortrabajadoresinformales.consultas.Conexion;
import com.example.proyecto_gestortrabajadoresinformales.consultas.UsuarioDAO;

public class TrabajadorInicioActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_exitoso_trabajador);

        String usuarioId = getIntent().getStringExtra("usuarioId");

        if (usuarioId != null) {
            Conexion conexion = new Conexion(this);
            UsuarioDAO usuarioDAO = new UsuarioDAO(conexion);
            Usuario usuario = usuarioDAO.obtenerUsuarioPorId(usuarioId);

            TextView txtBienvenida = findViewById(R.id.txtBienvenida);
            txtBienvenida.setText("¡Bienvenido, " + usuario.getNombres() + "!");
        }

        Button btnCrearPropuesta = findViewById(R.id.btnCrearPropuesta);
        btnCrearPropuesta.setOnClickListener(v -> {
            Intent intent = new Intent(TrabajadorInicioActivity.this, HistorialActivity.class);
            intent.putExtra("usuarioId", usuarioId);
            startActivity(intent);
        });

        // Botón para continuar editando perfil
        Button btnPerfil = findViewById(R.id.btnPerfil);
        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(TrabajadorInicioActivity.this, PerfilTrabajadorActivity.class);
            intent.putExtra("usuarioId", usuarioId);
            startActivity(intent);
        });
    }
}
