package com.example.proyecto_gestortrabajadoresinformales;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // Importar Button
import android.widget.EditText; // Importar EditText
import android.widget.ImageView;
import android.widget.LinearLayout; // Importar LinearLayout
import android.widget.RatingBar; // Importar RatingBar
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestortrabajadoresinformales.beans.Solicitud;
import com.example.proyecto_gestortrabajadoresinformales.Propuesta;
import com.example.proyecto_gestortrabajadoresinformales.beans.Usuario;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import android.util.Log;

public class SolicitudAceptadaAdapter extends RecyclerView.Adapter<SolicitudAceptadaAdapter.ViewHolder> {

    private List<Object[]> listaSolicitudes; // Solicitud, Propuesta, Usuario (trabajador), String (distrito)
    private OnSolicitudInteractionListener interactionListener; // Listener unificado

    // Interfaz unificada para manejar clics y acciones
    public interface OnSolicitudInteractionListener {
        void onWhatsappClick(String phoneNumber);
        void onGuardarCalificacionClick(int solicitudId, int puntuacion, String comentario);
    }

    // Constructor actualizado para aceptar el listener unificado
    public SolicitudAceptadaAdapter(List<Object[]> listaSolicitudes, OnSolicitudInteractionListener interactionListener) {
        this.listaSolicitudes = listaSolicitudes;
        this.interactionListener = interactionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_solicitud_aceptada, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object[] item = listaSolicitudes.get(position);
        Solicitud solicitud = (Solicitud) item[0];
        Propuesta propuesta = (Propuesta) item[1];
        Usuario trabajador = (Usuario) item[2]; // En la vista del cliente, el Usuario es el trabajador
        String distritoTrabajador = (String) item[3]; // El distrito del trabajador, si está en el Object[]

        DecimalFormat df = new DecimalFormat("'S/. '#0.00");
        String precioFormateado;
        try {
            precioFormateado = df.format(propuesta.getPrecio());
        } catch (IllegalArgumentException e) {
            Log.e("SolicitudAceptadaAdapter", "Error de formato DecimalFormat: " + e.getMessage());
            precioFormateado = "S/. " + String.format(Locale.getDefault(), "%.2f", propuesta.getPrecio());
        }

        // Asignar datos a las vistas
        holder.tvTituloSolicitudItemCliente.setText(propuesta.getTitulo());
        holder.tvMensajeSolicitudCliente.setText("Mensaje: " + solicitud.getMensaje());
        holder.tvInfoTrabajadorCliente.setText("Trabajador: " + trabajador.getNombres() + " " + trabajador.getApellidos());
        holder.tvContactoTrabajadorCliente.setText("Contacto: " + trabajador.getTelefono() + " - " + trabajador.getCorreo());
        holder.tvDetallePropuestaCliente.setText("Propuesta: " + (propuesta.getTipoServicioNombre() != null ? propuesta.getTipoServicioNombre() + " - " : "") + propuesta.getDescripcion() + " (" + precioFormateado + ")");

        // Lógica para mostrar/ocultar secciones basada en el estado de la solicitud
        if ("FINALIZADA".equalsIgnoreCase(solicitud.getEstado())) {
            holder.layoutWhatsapp.setVisibility(View.GONE);
            holder.layoutCalificacion.setVisibility(View.VISIBLE);

            // Reiniciar el RatingBar y EditText cada vez que se enlaza
            // Esto es importante para que no se mantengan valores de ítems reciclados
            holder.ratingBarServicio.setRating(0);
            holder.etComentarioCalificacion.setText("");

            // Configurar el clic para el botón Guardar Calificación
            holder.btnGuardarCalificacion.setOnClickListener(v -> {
                if (interactionListener != null) {
                    int puntuacion = (int) holder.ratingBarServicio.getRating();
                    String comentario = holder.etComentarioCalificacion.getText().toString().trim();

                    if (puntuacion > 0) { // La calificación debe ser al menos 1 estrella
                        interactionListener.onGuardarCalificacionClick(solicitud.getId(), puntuacion, comentario);
                        // Después de guardar, puedes deshabilitar los campos o el botón
                        holder.ratingBarServicio.setEnabled(false);
                        holder.etComentarioCalificacion.setEnabled(false);
                        holder.btnGuardarCalificacion.setEnabled(false);
                        // Opcional: Ocultar completamente la sección de calificación si ya se guardó
                        // holder.layoutCalificacion.setVisibility(View.GONE);
                    } else {
                        // Puedes mostrar un Toast o un mensaje de error si la calificación es 0
                        Log.w("SolicitudAdapter", "Calificación debe ser mayor a 0.");
                        // Toast.makeText(holder.itemView.getContext(), "Por favor, califica el servicio con al menos una estrella.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            // Si el estado no es FINALIZADA, mostrar WhatsApp y ocultar calificación
            holder.layoutWhatsapp.setVisibility(View.VISIBLE);
            holder.layoutCalificacion.setVisibility(View.GONE);

            // Configurar el clic para el icono de WhatsApp
            final String workerPhoneNumber = trabajador.getTelefono();
            holder.ivWhatsappClienteContacto.setOnClickListener(v -> {
                if (interactionListener != null && workerPhoneNumber != null && !workerPhoneNumber.isEmpty()) {
                    interactionListener.onWhatsappClick(workerPhoneNumber);
                } else {
                    Log.w("SolicitudAceptadaAdapter", "Número de teléfono del trabajador no disponible para WhatsApp o listener nulo.");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listaSolicitudes != null ? listaSolicitudes.size() : 0;
    }

    public void setListaSolicitudes(List<Object[]> nuevaLista) {
        this.listaSolicitudes = nuevaLista;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // TextViews principales
        TextView tvTituloSolicitudItemCliente, tvMensajeSolicitudCliente,
                tvInfoTrabajadorCliente, tvContactoTrabajadorCliente, tvDetallePropuestaCliente;

        // Elementos de la sección de WhatsApp
        LinearLayout layoutWhatsapp;
        ImageView ivWhatsappClienteContacto;

        // Elementos de la sección de Calificación
        LinearLayout layoutCalificacion;
        RatingBar ratingBarServicio;
        EditText etComentarioCalificacion;
        Button btnGuardarCalificacion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Vinculación de TextViews principales
            tvTituloSolicitudItemCliente = itemView.findViewById(R.id.tvTituloSolicitudItemCliente);
            tvMensajeSolicitudCliente = itemView.findViewById(R.id.tvMensajeSolicitudCliente);
            tvInfoTrabajadorCliente = itemView.findViewById(R.id.tvInfoTrabajadorCliente);
            tvContactoTrabajadorCliente = itemView.findViewById(R.id.tvContactoTrabajadorCliente);
            tvDetallePropuestaCliente = itemView.findViewById(R.id.tvDetallePropuestaCliente);

            // Vinculación de elementos de WhatsApp
            layoutWhatsapp = itemView.findViewById(R.id.layoutWhatsapp);
            ivWhatsappClienteContacto = itemView.findViewById(R.id.ivWhatsappClienteContacto);

            // Vinculación de elementos de Calificación
            layoutCalificacion = itemView.findViewById(R.id.layoutCalificacion);
            ratingBarServicio = itemView.findViewById(R.id.ratingBarServicio);
            etComentarioCalificacion = itemView.findViewById(R.id.etComentarioCalificacion);
            btnGuardarCalificacion = itemView.findViewById(R.id.btnGuardarCalificacion);
        }
    }
}
