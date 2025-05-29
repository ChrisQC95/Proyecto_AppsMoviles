package com.example.proyecto_gestortrabajadoresinformales;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_gestortrabajadoresinformales.beans.Usuario;
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
            Usuario usuario = usuarioDAO.obtenerUsuarioPorId(usuarioId); // Ahora este método debe aceptar String

            TextView txtBienvenida = findViewById(R.id.txtBienvenida);
            txtBienvenida.setText("¡Bienvenido, " + usuario.getNombres() + "!");
        }
    }
}
