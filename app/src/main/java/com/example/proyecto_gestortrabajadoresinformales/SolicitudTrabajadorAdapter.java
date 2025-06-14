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
import java.util.List;
import java.util.Locale;
import android.util.Log;

public class SolicitudTrabajadorAdapter extends RecyclerView.Adapter<SolicitudTrabajadorAdapter.SolicitudViewHolder> {

    private List<Object[]> listaSolicitudes;
    private OnSolicitudActionListener listener;

    public interface OnSolicitudActionListener {
        void onAceptarClick(Solicitud solicitud);
        void onRechazarClick(Solicitud solicitud);
        void onFinalizarClick(Solicitud solicitud); // Nuevo método para finalizar
    }

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
        String distritoCliente = (String) item[3]; // Obtener el distrito del cliente

        DecimalFormat df = new DecimalFormat("'S/. '#0.00");
        String precioFormateado;
        try {
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
        holder.tvDistritoClienteItem.setText("Distrito: " + (distritoCliente != null ? distritoCliente : "N/A")); // Asignar el distrito
        holder.tvDetallePropuestaItem.setText("Propuesta: " + (propuesta.getTipoServicioNombre() != null ? propuesta.getTipoServicioNombre() + " - " : "") + propuesta.getDescripcion() + " (" + precioFormateado + ")");

        // Establecer el texto y color del estado de la solicitud
        String estado = solicitud.getEstado() != null ? solicitud.getEstado().toUpperCase() : "DESCONOCIDO";
        holder.tvEstadoSolicitudItem.setText("Estado: " + estado);
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

        // Controlar la visibilidad y funcionalidad de los botones
        if (estado.equals("ENVIADA")) {
            holder.btnAceptarSolicitud.setVisibility(View.VISIBLE);
            holder.btnAceptarSolicitud.setEnabled(true);
            holder.btnAceptarSolicitud.setText("Aceptar"); // Asegura el texto "Aceptar"
            // Se usa getColorStateList para compatibilidad con versiones antiguas
            holder.btnAceptarSolicitud.setBackgroundTintList(holder.itemView.getContext().getResources().getColorStateList(R.color.purple_500));

            holder.btnRechazarSolicitud.setVisibility(View.VISIBLE);
            holder.btnRechazarSolicitud.setEnabled(true);
            holder.btnRechazarSolicitud.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRechazarClick(solicitud);
                }
            });
            holder.btnAceptarSolicitud.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAceptarClick(solicitud);
                }
            });
        } else if (estado.equals("ACEPTADA")) {
            holder.btnAceptarSolicitud.setVisibility(View.VISIBLE);
            holder.btnAceptarSolicitud.setEnabled(true);
            holder.btnAceptarSolicitud.setText("Finalizar"); // Cambiar a "Finalizar"
            // Nuevo color para el botón "Finalizar"
            holder.btnAceptarSolicitud.setBackgroundTintList(holder.itemView.getContext().getResources().getColorStateList(android.R.color.holo_orange_dark));

            holder.btnRechazarSolicitud.setVisibility(View.GONE); // Ocultar Rechazar
            holder.btnRechazarSolicitud.setEnabled(false);
            holder.btnRechazarSolicitud.setOnClickListener(null);

            holder.btnAceptarSolicitud.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFinalizarClick(solicitud); // Asignar el listener de Finalizar
                }
            });
        } else { // RECHAZADA o FINALIZADA
            holder.btnAceptarSolicitud.setVisibility(View.GONE);
            holder.btnAceptarSolicitud.setEnabled(false);
            holder.btnAceptarSolicitud.setOnClickListener(null);

            holder.btnRechazarSolicitud.setVisibility(View.GONE);
            holder.btnRechazarSolicitud.setEnabled(false);
            holder.btnRechazarSolicitud.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return listaSolicitudes.size();
    }

    public void setListaSolicitudes(List<Object[]> nuevaLista) {
        this.listaSolicitudes = nuevaLista;
        notifyDataSetChanged();
    }

    public static class SolicitudViewHolder extends RecyclerView.ViewHolder {
        TextView tvTituloSolicitudItem;
        TextView tvEstadoSolicitudItem;
        TextView tvMensajeClienteItem;
        TextView tvInfoClienteItem;
        TextView tvContactoClienteItem;
        TextView tvDetallePropuestaItem;
        TextView tvDistritoClienteItem; // Declarar el nuevo TextView para el distrito
        Button btnAceptarSolicitud;
        Button btnRechazarSolicitud;

        public SolicitudViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTituloSolicitudItem = itemView.findViewById(R.id.tvTituloSolicitudItem);
            tvEstadoSolicitudItem = itemView.findViewById(R.id.tvEstadoSolicitudItem);
            tvMensajeClienteItem = itemView.findViewById(R.id.tvMensajeClienteItem);
            tvInfoClienteItem = itemView.findViewById(R.id.tvInfoClienteItem);
            tvContactoClienteItem = itemView.findViewById(R.id.tvContactoClienteItem);
            tvDetallePropuestaItem = itemView.findViewById(R.id.tvDetallePropuestaItem);
            tvDistritoClienteItem = itemView.findViewById(R.id.tvDistritoClienteItem); // Vincular el nuevo TextView
            btnAceptarSolicitud = itemView.findViewById(R.id.btnAceptarSolicitud);
            btnRechazarSolicitud = itemView.findViewById(R.id.btnRechazarSolicitud);
        }
    }
}
