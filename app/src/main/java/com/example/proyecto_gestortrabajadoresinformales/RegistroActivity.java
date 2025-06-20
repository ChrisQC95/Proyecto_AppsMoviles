package com.example.proyecto_gestortrabajadoresinformales;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_gestortrabajadoresinformales.beans.Perfil;
import com.example.proyecto_gestortrabajadoresinformales.beans.Usuario;
import com.example.proyecto_gestortrabajadoresinformales.cliente.PerfilClienteActivity;
import com.example.proyecto_gestortrabajadoresinformales.consultas.Conexion;
import com.example.proyecto_gestortrabajadoresinformales.consultas.PerfilDAO;
import com.example.proyecto_gestortrabajadoresinformales.consultas.UsuarioDAO;
import com.example.proyecto_gestortrabajadoresinformales.trabajador.PerfilTrabajadorActivity;

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
        btnRegistro.setOnClickListener(v -> registrarUsuario());

        // Redirigir a Login
        lblLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegistroActivity.this, LoginActivity.class));
            finish();
        });
    }

    private boolean validarCampos() {

        //  Nombres y apellidos – mínimo 2 caracteres
        if (txtNombres.getText().toString().trim().length() < 3) {
            txtNombres.setError("Ingrese sus nombres (mín. 3 caracteres)");
            txtNombres.requestFocus();
            return false;
        }

        if (txtApellidos.getText().toString().trim().length() < 3) {
            txtApellidos.setError("Ingrese sus apellidos (mín. 3 caracteres)");
            txtApellidos.requestFocus();
            return false;
        }

        // Teléfono – solo dígitos y 9 caracteres (Perú)
        String telefono = txtTelefono.getText().toString().trim();
        if (!telefono.matches("\\d{9}")) {
            txtTelefono.setError("Teléfono debe tener 9 dígitos");
            txtTelefono.requestFocus();
            return false;
        }

        // Correo – patrón estándar de Android
        String correo = txtCorreo.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            txtCorreo.setError("Correo electrónico inválido");
            txtCorreo.requestFocus();
            return false;
        }

        // Contraseña – mínimo 6 caracteres con al menos 1 letra y 1 número
        String contrasena = txtContrasena.getText().toString().trim();
        if (!contrasena.matches("^(?=.*[A-Za-z])(?=.*\\d).{6,}$")) {
            txtContrasena.setError("Al menos 6 caracteres, 1 letra y 1 número");
            txtContrasena.requestFocus();
            return false;
        }

        // Tipo de usuario – no permitir la opción por defecto “Seleccione…”
        if (spnTipoUsuario.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione el tipo de usuario", Toast.LENGTH_SHORT).show();
            spnTipoUsuario.performClick();
            return false;
        }
        return true;
    }

    private void registrarUsuario() {
        if (!validarCampos()) return;

        String nombres = txtNombres.getText().toString().trim();
        String apellidos = txtApellidos.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String correo = txtCorreo.getText().toString().trim();
        String contrasena = txtContrasena.getText().toString().trim();
        String tipoUsuario = spnTipoUsuario.getSelectedItem().toString();

        Usuario usuario = new Usuario(nombres, apellidos, telefono, correo, contrasena, tipoUsuario);
        Conexion conexion = new Conexion(this);
        UsuarioDAO usuarioDAO = new UsuarioDAO(conexion);

        long nuevoId = usuarioDAO.insertarUsuario(usuario);
        if (nuevoId != -1) {
            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();

            // Crear perfil vacío con nuevoId
            PerfilDAO perfilDAO = new PerfilDAO(new Conexion(this));
            Perfil perfilNuevo = new Perfil(String.valueOf(nuevoId), "", "", "");
            perfilDAO.insertarPerfil(perfilNuevo);

            // Redirigir según tipo de usuario
            Intent intent;
            if (usuario.getTipoUsuario().equalsIgnoreCase("CLIENTE")) {
                intent = new Intent(this, PerfilClienteActivity.class);
            } else {
                intent = new Intent(this, PerfilTrabajadorActivity.class);
            }
            intent.putExtra("usuarioId", String.valueOf(nuevoId));
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
        }
    }
}