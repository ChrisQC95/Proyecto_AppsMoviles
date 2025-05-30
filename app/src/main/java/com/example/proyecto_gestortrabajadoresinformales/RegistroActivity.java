package com.example.proyecto_gestortrabajadoresinformales;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_gestortrabajadoresinformales.beans.Perfil;
import com.example.proyecto_gestortrabajadoresinformales.beans.Usuario;
import com.example.proyecto_gestortrabajadoresinformales.consultas.PerfilDAO;
import com.example.proyecto_gestortrabajadoresinformales.consultas.UsuarioDAO;

public class RegistroActivity extends AppCompatActivity {

    private EditText txtNombres, txtApellidos, txtTelefono, txtCorreo, txtContrasena;
    private Spinner spnTipoUsuario;
    private Button btnRegistro;
    private TextView lblLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_sesion);

        // Enlazar vistas
        txtNombres = findViewById(R.id.txtNombres);
        txtApellidos = findViewById(R.id.txtApellidos);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtContrasena = findViewById(R.id.txtContrasena);
        spnTipoUsuario = findViewById(R.id.spnTipoUsuario);
        btnRegistro = findViewById(R.id.btnRegistro);
        lblLogin = findViewById(R.id.lblLogin);

        // Configurar Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.tipos_usuario,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTipoUsuario.setAdapter(adapter);

        // Botón de registro
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });

        // Redirigir a Login
        lblLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registrarUsuario() {
        String nombres = txtNombres.getText().toString().trim();
        String apellidos = txtApellidos.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String correo = txtCorreo.getText().toString().trim();
        String contrasena = txtContrasena.getText().toString().trim();
        String tipoUsuario = spnTipoUsuario.getSelectedItem().toString();

        if (nombres.isEmpty() || apellidos.isEmpty() || telefono.isEmpty() ||
                correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario usuario = new Usuario(nombres, apellidos, telefono, correo, contrasena, tipoUsuario);
        Conexion conexion = new Conexion(this);
        UsuarioDAO usuarioDAO = new UsuarioDAO(conexion);

        long nuevoId = usuarioDAO.insertarUsuario(usuario);
        if (nuevoId != -1) {
            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();

            // Aquí puedes crear perfil vacío con nuevoId
            PerfilDAO perfilDAO = new PerfilDAO(new Conexion(this));
            Perfil perfilNuevo = new Perfil(String.valueOf(nuevoId), "", "", "");
            perfilDAO.insertarPerfil(perfilNuevo);

            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
        }
    }
}