package com.example.proyecto_gestortrabajadoresinformales;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_gestortrabajadoresinformales.beans.Distrito;
import com.example.proyecto_gestortrabajadoresinformales.beans.Perfil;
import com.example.proyecto_gestortrabajadoresinformales.beans.Usuario;
import com.example.proyecto_gestortrabajadoresinformales.consultas.DistritoDAO;
import com.example.proyecto_gestortrabajadoresinformales.consultas.PerfilDAO;
import com.example.proyecto_gestortrabajadoresinformales.consultas.UsuarioDAO;
import com.bumptech.glide.Glide;
import android.widget.ImageView;

import java.util.List;
public class PerfilTrabajadorActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datos_perfil_trabajador);

        String usuarioId = getIntent().getStringExtra("usuarioId");

        if (usuarioId != null) {
            Conexion conexion = new Conexion(this);
            UsuarioDAO usuarioDAO = new UsuarioDAO(conexion);
            Usuario usuario = usuarioDAO.obtenerUsuarioPorId(usuarioId);

            EditText txtNombres = findViewById(R.id.txtNombres);
            EditText txtApellidos = findViewById(R.id.txtApellidos);
            EditText txtTelefono = findViewById(R.id.txtTelefono);
            EditText txtCorreo = findViewById(R.id.txtCorreo);
            Spinner spnDistrito = findViewById(R.id.spnDistrito);
            EditText txtImagen = findViewById(R.id.txtImagen);
            EditText txtEspecialidad = findViewById(R.id.txtEspecialidad);

            DistritoDAO distritoDAO = new DistritoDAO(this);
            List<Distrito> listaDistritos = distritoDAO.obtenerTodosLosDistritos();

            ArrayAdapter<Distrito> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaDistritos);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spnDistrito.setAdapter(adapter);

            // Cargar datos usuario
            txtNombres.setText(usuario.getNombres());
            txtApellidos.setText(usuario.getApellidos());
            txtTelefono.setText(usuario.getTelefono());
            txtCorreo.setText(usuario.getCorreo());

            PerfilDAO perfilDAO = new PerfilDAO(conexion);

            // Asumimos que el perfil siempre existe
            Perfil perfil = perfilDAO.obtenerPerfilPorUsuarioId(usuarioId);

            // Cargar datos perfil
            ImageView imgFoto = findViewById(R.id.imgFoto);

            if (perfil != null) {
                txtImagen.setText(perfil.getFotoPerfil());
                txtEspecialidad.setText(perfil.getEspecialidad());

                // Mostrar imagen con Glide
                String urlImagen = perfil.getFotoPerfil();
                if (urlImagen != null && !urlImagen.trim().isEmpty()) {
                    Glide.with(this)
                            .load(urlImagen)
                            .placeholder(R.drawable.usuario) // imagen mientras carga
                            .error(R.drawable.usuario)       // imagen si falla carga
                            .into(imgFoto);
                } else {
                    // URL vacía o nula, mostrar imagen por defecto
                    imgFoto.setImageResource(R.drawable.usuario);
                }

                // Seleccionar distrito solo si distritoId no está vacío o nulo
                if (perfil.getDistritoId() != null && !perfil.getDistritoId().trim().isEmpty()) {
                    for (int i = 0; i < listaDistritos.size(); i++) {
                        if (listaDistritos.get(i).getId().equals(perfil.getDistritoId())) {
                            spnDistrito.setSelection(i);
                            break;
                        }
                    }
                } else {
                    // No seleccionar ningún distrito si id vacío
                    spnDistrito.setSelection(-1);
                }
            } else {
                // Perfil null: poner imagen por defecto y no seleccionar distrito
                imgFoto.setImageResource(R.drawable.usuario);
                spnDistrito.setSelection(-1);
            }

            Button btnGuardar = findViewById(R.id.btnGuardar);
            btnGuardar.setOnClickListener(v -> {
                // Obtener valores del formulario
                usuario.setNombres(txtNombres.getText().toString());
                usuario.setApellidos(txtApellidos.getText().toString());
                usuario.setTelefono(txtTelefono.getText().toString());
                usuario.setCorreo(txtCorreo.getText().toString());

                usuarioDAO.actualizarUsuario(usuario);

                String distritoId = obtenerIdDelSpinner(spnDistrito);
                String imagen = txtImagen.getText().toString();
                String especialidad = txtEspecialidad.getText().toString();

                // Actualizamos perfil sin verificaciones
                perfil.setDistritoId(distritoId);
                perfil.setFotoPerfil(imagen);
                perfil.setEspecialidad(especialidad);

                perfilDAO.actualizarPerfil(perfil);

                String nuevaUrl = txtImagen.getText().toString();
                if (nuevaUrl != null && !nuevaUrl.trim().isEmpty()) {
                    Glide.with(this)
                            .load(nuevaUrl)
                            .placeholder(R.drawable.usuario)
                            .error(R.drawable.usuario)
                            .into(imgFoto);
                } else {
                    imgFoto.setImageResource(R.drawable.usuario);
                }

                Toast.makeText(this, "Perfil guardado correctamente", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(PerfilTrabajadorActivity.this, TrabajadorInicioActivity.class);
                intent.putExtra("usuarioId", usuarioId);
                startActivity(intent);
            });

        }

    }

    private String obtenerIdDelSpinner(Spinner spnDistrito) {
        Distrito distritoSeleccionado = (Distrito) spnDistrito.getSelectedItem();
        return distritoSeleccionado != null ? distritoSeleccionado.getId() : null;
    }
}
