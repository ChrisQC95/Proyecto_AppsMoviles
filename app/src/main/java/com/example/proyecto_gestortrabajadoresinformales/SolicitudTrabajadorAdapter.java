package com.example.proyecto_gestortrabajadoresinformales; // Ajusta el paquete si es necesario

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestortrabajadoresinformales.beans.Solicitud;
import com.example.proyecto_gestortrabajadoresinformales.Propuesta;
import com.example.proyecto_gestortrabajadoresinformales.beans.Usuario;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import android.util.Log; // Añadido para Log.e

public class SolicitudTrabajadorAdapter extends RecyclerView.Adapter<SolicitudTrabajadorAdapter.SolicitudViewHolder> {

    private List<Object[]> listaSolicitudes; // Almacenará Solicitud, Propuesta, Usuario
    private OnSolicitudActionListener listener; // Interfaz para las acciones de Aceptar/Rechazar

    // Interfaz para manejar las acciones de Aceptar y Rechazar
    public interface OnSolicitudActionListener {
        void onAceptarClick(Solicitud solicitud);
        void onRechazarClick(Solicitud solicitud); // Nuevo método para rechazar
    }

    // Constructor que acepta la nueva interfaz
    public SolicitudTrabajadorAdapter(List<Object[]> listaSolicitudes, OnSolicitudActionListener listener) {
        this.listaSolicitudes = listaSolicitudes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SolicitudViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_solicitud_trabajador, parent, false);
        return new SolicitudViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SolicitudViewHolder holder, int position) {
        Object[] item = listaSolicitudes.get(position);
        Solicitud solicitud = (Solicitud) item[0];
        Propuesta propuesta = (Propuesta) item[1];
        Usuario cliente = (Usuario) item[2];

        // Formatear precio de manera segura con DecimalFormat
        String precioFormateado;
        try {
            DecimalFormat df = new DecimalFormat("'S/. '#0.00"); // Asegúrate de que el patrón es correcto. Escapar 'S/.'
            precioFormateado = df.format(propuesta.getPrecio());
        } catch (IllegalArgumentException e) {
            Log.e("SolicitudTrabajadorAdapter", "Error de formato DecimalFormat: " + e.getMessage());
            precioFormateado = "S/. " + String.format(Locale.getDefault(), "%.2f", propuesta.getPrecio());
        }

        // Asignar datos a las vistas
        holder.tvTituloSolicitudItem.setText(propuesta.getTitulo());
        holder.tvMensajeClienteItem.setText("Mensaje: " + solicitud.getMensaje());
        holder.tvInfoClienteItem.setText("Cliente: " + cliente.getNombres() + " " + cliente.getApellidos());
        holder.tvContactoClienteItem.setText("Contacto: " + cliente.getTelefono() + " - " + cliente.getCorreo());
        // Ajustar el texto de la propuesta, incluyendo el tipo de servicio (si 'tipoServicioNombre' está disponible en Propuesta)
        holder.tvDetallePropuestaItem.setText("Propuesta: " + (propuesta.getTipoServicioNombre() != null ? propuesta.getTipoServicioNombre() + " - " : "") + propuesta.getDescripcion() + " (" + precioFormateado + ")");


        // Establecer el texto y color del estado de la solicitud
        String estado = solicitud.getEstado() != null ? solicitud.getEstado().toUpperCase() : "DESCONOCIDO";
        holder.tvEstadoSolicitudItem.setText("Estado: " + estado);
        // Cambiar color del estado según su valor
        switch (estado) {
            case "ENVIADA":
                holder.tvEstadoSolicitudItem.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_blue_dark));
                break;
            case "ACEPTADA":
                holder.tvEstadoSolicitudItem.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
                break;
            case "RECHAZADA":
                holder.tvEstadoSolicitudItem.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                break;
            case "FINALIZADA":
                holder.tvEstadoSolicitudItem.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray));
                break;
            default:
                holder.tvEstadoSolicitudItem.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.black));
                break;
        }

        // Controlar la visibilidad y funcionalidad de los botones "Aceptar" y "Rechazar"
        if (solicitud.getEstado().equalsIgnoreCase("ENVIADA")) {
            holder.btnAceptarSolicitud.setVisibility(View.VISIBLE);
            holder.btnAceptarSolicitud.setEnabled(true);
            holder.btnRechazarSolicitud.setVisibility(View.VISIBLE); // Hacer visible el botón Rechazar
            holder.btnRechazarSolicitud.setEnabled(true); // Habilitar el botón Rechazar

            // Setear listeners
            holder.btnAceptarSolicitud.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAceptarClick(solicitud);
                }
            });
            holder.btnRechazarSolicitud.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRechazarClick(solicitud);
                }
            });
        } else {
            // Ocultar ambos botones si la solicitud no está en estado "ENVIADA"
            holder.btnAceptarSolicitud.setVisibility(View.GONE);
            holder.btnAceptarSolicitud.setEnabled(false);
            holder.btnAceptarSolicitud.setOnClickListener(null); // Elimina el listener

            holder.btnRechazarSolicitud.setVisibility(View.GONE); // Ocultar también el botón Rechazar
            holder.btnRechazarSolicitud.setEnabled(false);
            holder.btnRechazarSolicitud.setOnClickListener(null); // Elimina el listener
        }
    }

    @Override
    public int getItemCount() {
        return listaSolicitudes.size();
    }

    // Método para actualizar la lista de solicitudes
    public void setListaSolicitudes(List<Object[]> nuevaLista) {
        this.listaSolicitudes = nuevaLista;
        notifyDataSetChanged(); // Notifica al RecyclerView que los datos han cambiado
    }

    public static class SolicitudViewHolder extends RecyclerView.ViewHolder {
        TextView tvTituloSolicitudItem;
        TextView tvEstadoSolicitudItem;
        TextView tvMensajeClienteItem;
        TextView tvInfoClienteItem;
        TextView tvContactoClienteItem;
        TextView tvDetallePropuestaItem;
        Button btnAceptarSolicitud;
        Button btnRechazarSolicitud; // Declarar el nuevo botón de rechazar

        public SolicitudViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTituloSolicitudItem = itemView.findViewById(R.id.tvTituloSolicitudItem);
            tvEstadoSolicitudItem = itemView.findViewById(R.id.tvEstadoSolicitudItem);
            tvMensajeClienteItem = itemView.findViewById(R.id.tvMensajeClienteItem);
            tvInfoClienteItem = itemView.findViewById(R.id.tvInfoClienteItem);
            tvContactoClienteItem = itemView.findViewById(R.id.tvContactoClienteItem);
            tvDetallePropuestaItem = itemView.findViewById(R.id.tvDetallePropuestaItem);
            btnAceptarSolicitud = itemView.findViewById(R.id.btnAceptarSolicitud);
            btnRechazarSolicitud = itemView.findViewById(R.id.btnRechazarSolicitud); // Vincular el nuevo botón
        }
    }
}