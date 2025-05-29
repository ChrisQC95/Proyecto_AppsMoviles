package com.example.proyecto_gestortrabajadoresinformales;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.*;

import com.example.proyecto_gestortrabajadoresinformales.beans.Usuario;
import com.example.proyecto_gestortrabajadoresinformales.consultas.UsuarioDAO;

public class LoginActivity extends AppCompatActivity {

    private EditText txtCorreo, txtContrasena;
    private Button btnLogin;
    private TextView lblRegistro, lblRestablecer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_sesion);

        // Vincular vistas
        txtCorreo = findViewById(R.id.txtCorreo);
        txtContrasena = findViewById(R.id.txtContrasena);
        btnLogin = findViewById(R.id.btnLogin);
        lblRegistro = findViewById(R.id.lblRegistro);
        lblRestablecer = findViewById(R.id.lblRestablecer);

        // Click en "Iniciar Sesión"
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesion();
            }
        });

        // Click en "Registrate"
        lblRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
            }
        });

        // Click en "Olvidaste contraseña"
        lblRestablecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RestablecerActivity.class));
            }
        });
    }

    private void iniciarSesion() {
        String correo = txtCorreo.getText().toString().trim();
        String contrasena = txtContrasena.getText().toString().trim();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Conexion conexion = new Conexion(this);
        UsuarioDAO usuarioDAO = new UsuarioDAO(conexion);
        Usuario usuario = usuarioDAO.obtenerUsuarioPorCredenciales(correo, contrasena);

        if (usuario != null) {
            Toast.makeText(this, "Bienvenido, " + usuario.getNombres(), Toast.LENGTH_SHORT).show();

            // Redirigir según tipo de usuario
            if (usuario.getTipoUsuario().equalsIgnoreCase("CLIENTE")) {
                Intent intent = new Intent(this, ClienteInicioActivity.class);
                intent.putExtra("usuarioId", usuario.getId());
                startActivity(intent);
            } else if (usuario.getTipoUsuario().equalsIgnoreCase("TRABAJADOR")) {
                Intent intent = new Intent(this, TrabajadorInicioActivity.class);
                intent.putExtra("usuarioId", usuario.getId());
                startActivity(intent);
            }

            finish(); // cerrar LoginActivity
        } else {
            Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }
}

