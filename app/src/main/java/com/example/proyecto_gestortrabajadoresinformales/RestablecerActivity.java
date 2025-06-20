package com.example.proyecto_gestortrabajadoresinformales;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_gestortrabajadoresinformales.consultas.Conexion;
import com.example.proyecto_gestortrabajadoresinformales.consultas.UsuarioDAO;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class RestablecerActivity extends AppCompatActivity {
    private EditText editTextCorreo;
    private Button btnEnviarContrasena;
    private UsuarioDAO usuarioDAO;
    private TextView lblLogin;
    private TextView lblMensaje;

    private final String emailRemitente = "grupoappmovil25@gmail.com";
    private final String passwordRemitente = "udfecoednppbftzf";

    private boolean errorEnvioCorreo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restablecer_contrasena);

        editTextCorreo = findViewById(R.id.txtCorreo);
        btnEnviarContrasena = findViewById(R.id.btnEnviar);
        lblLogin = findViewById(R.id.lblLogin);
        lblMensaje = findViewById(R.id.lblRespuesta);

        Conexion conexion = new Conexion(this);
        usuarioDAO = new UsuarioDAO(conexion);

        btnEnviarContrasena.setOnClickListener(v -> {
            String correoDestinatario = editTextCorreo.getText().toString().trim();

            // Limpiar errores previos
            editTextCorreo.setError(null);
            lblMensaje.setText("");

            if (correoDestinatario.isEmpty()) {
                editTextCorreo.setError("Por favor, ingresa tu correo electrónico.");
                editTextCorreo.requestFocus();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correoDestinatario).matches()) {
                editTextCorreo.setError("Correo inválido. Ej: usuario@dominio.com");
                editTextCorreo.requestFocus();
                return;
            }

            new ObtenerYEnviarContrasenaTask().execute(correoDestinatario);
        });

        lblLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RestablecerActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private String obtenerContrasena(String correo) {
        return usuarioDAO.obtenerContrasenaPorCorreo(correo);
    }

    private void enviarCorreo(String destinatario, String asunto, String cuerpo) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailRemitente, passwordRemitente);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailRemitente));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);
            message.setText(cuerpo);

            Transport.send(message);
            errorEnvioCorreo = false;

        } catch (MessagingException e) {
            errorEnvioCorreo = true;
            Log.e("EnviarCorreo", "Error al enviar el correo", e);
        }
    }

    private class ObtenerYEnviarContrasenaTask extends AsyncTask<String, Void, String> {
        private String correoDestinatario;
        private String contrasena;
        private boolean envioExitoso = false;

        @Override
        protected String doInBackground(String... params) {
            correoDestinatario = params[0];
            contrasena = obtenerContrasena(correoDestinatario);
            if (contrasena != null) {
                String cuerpo = "Hola,\n\n"
                        + "Recibimos una solicitud para recuperar la contraseña de tu cuenta.\n\n"
                        + "Tu contraseña actual es: " + contrasena + "\n\n"
                        + "Si no realizaste esta solicitud, puedes ignorar este mensaje.\n\n"
                        + "Saludos,\nEquipo de soporte de la App";
                enviarCorreo(correoDestinatario, "Recuperación de contraseña", cuerpo);
                if (!errorEnvioCorreo) {
                    envioExitoso = true;
                    return "Hemos enviado tu contraseña al correo ingresado. Revisa tu bandeja de entrada o correo no deseado.";
                } else {
                    return "No se pudo enviar el correo. Verifica tu conexión a internet o intenta más tarde.";
                }
            } else {
                return "No encontramos una cuenta asociada a ese correo. Verifica que esté bien escrito o regístrate si aún no tienes una cuenta.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            lblMensaje.setText(result);
            if (envioExitoso) {
                Toast.makeText(RestablecerActivity.this, "Correo enviado", Toast.LENGTH_SHORT).show();
                editTextCorreo.setText("");
            } else if (errorEnvioCorreo) {
                Toast.makeText(RestablecerActivity.this, "No se pudo enviar", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

