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
import java.util.Locale; // Para formatear moneda

public class SolicitudTrabajadorAdapter extends RecyclerView.Adapter<SolicitudTrabajadorAdapter.SolicitudViewHolder> {

    private List<Object[]> listaSolicitudes; // Almacenará Solicitud, Propuesta, Usuario
    private OnAceptarClickListener listener;

    public interface OnAceptarClickListener {
        void onAceptarClick(Solicitud solicitud);
    }

    public SolicitudTrabajadorAdapter(List<Object[]> listaSolicitudes, OnAceptarClickListener listener) {
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

        // Formatear precio
        DecimalFormat df = new DecimalFormat("'S/. '#0.00"); // Usamos el formato que ya corregimos
        String precioFormateado = df.format(propuesta.getPrecio());

        // Asignar datos a las vistas
        holder.tvTituloSolicitudItem.setText(propuesta.getTitulo());
        holder.tvMensajeClienteItem.setText("Mensaje: " + solicitud.getMensaje());
        holder.tvInfoClienteItem.setText("Cliente: " + cliente.getNombres() + " " + cliente.getApellidos());
        holder.tvContactoClienteItem.setText("Contacto: " + cliente.getTelefono() + " - " + cliente.getCorreo());
        // Ajustar el texto de la propuesta, incluyendo el tipo de servicio
        holder.tvDetallePropuestaItem.setText("Propuesta: " + propuesta.getTipoServicioNombre() + " - " + propuesta.getDescripcion() + " (" + precioFormateado + ")");

        // Configurar el botón de aceptar
        holder.btnAceptarSolicitud.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAceptarClick(solicitud);
            }
        });
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
        TextView tvMensajeClienteItem;
        TextView tvInfoClienteItem;
        TextView tvContactoClienteItem;
        TextView tvDetallePropuestaItem;
        Button btnAceptarSolicitud;

        public SolicitudViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTituloSolicitudItem = itemView.findViewById(R.id.tvTituloSolicitudItem);
            tvMensajeClienteItem = itemView.findViewById(R.id.tvMensajeClienteItem);
            tvInfoClienteItem = itemView.findViewById(R.id.tvInfoClienteItem);
            tvContactoClienteItem = itemView.findViewById(R.id.tvContactoClienteItem);
            tvDetallePropuestaItem = itemView.findViewById(R.id.tvDetallePropuestaItem);
            btnAceptarSolicitud = itemView.findViewById(R.id.btnAceptarSolicitud);
        }
    }
}