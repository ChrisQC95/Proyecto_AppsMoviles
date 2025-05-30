package com.example.proyecto_gestortrabajadoresinformales;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_gestortrabajadoresinformales.beans.Usuario;
import com.example.proyecto_gestortrabajadoresinformales.consultas.UsuarioDAO;

public class ClienteInicioActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_exitoso_cliente);

        String usuarioId = getIntent().getStringExtra("usuarioId");

        if (usuarioId != null) {
            Conexion conexion = new Conexion(this);
            UsuarioDAO usuarioDAO = new UsuarioDAO(conexion);
            Usuario usuario = usuarioDAO.obtenerUsuarioPorId(usuarioId);

            TextView txtBienvenida = findViewById(R.id.txtBienvenida);
            txtBienvenida.setText("¡Bienvenido, " + usuario.getNombres() + "!");

            // Botón para continuar editando perfil
            Button btnPerfil = findViewById(R.id.btnPerfil);
            btnPerfil.setOnClickListener(v -> {
                Intent intent = new Intent(ClienteInicioActivity.this, PerfilClienteActivity.class);
                intent.putExtra("usuarioId", usuarioId);
                startActivity(intent);
            });
        }
    }
}
