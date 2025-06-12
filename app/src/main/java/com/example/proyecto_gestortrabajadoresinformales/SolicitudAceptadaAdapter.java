package com.example.proyecto_gestortrabajadoresinformales;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestortrabajadoresinformales.beans.Solicitud;
import com.example.proyecto_gestortrabajadoresinformales.Propuesta;
import com.example.proyecto_gestortrabajadoresinformales.beans.Usuario;

import java.text.DecimalFormat;
import java.util.List;

public class SolicitudAceptadaAdapter extends RecyclerView.Adapter<SolicitudAceptadaAdapter.ViewHolder> {

    private List<Object[]> listaSolicitudes; // Solicitud, Propuesta, Usuario

    public SolicitudAceptadaAdapter(List<Object[]> listaSolicitudes) {
        this.listaSolicitudes = listaSolicitudes;
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
        Usuario cliente = (Usuario) item[2];

        DecimalFormat df = new DecimalFormat("'S/. '#0.00");
        String precioFormateado = df.format(propuesta.getPrecio());

        holder.tvTituloSolicitudItem.setText(propuesta.getTitulo());
        holder.tvMensajeClienteItem.setText("Mensaje: " + solicitud.getMensaje());
        holder.tvInfoClienteItem.setText("Cliente: " + cliente.getNombres() + " " + cliente.getApellidos());
        holder.tvContactoClienteItem.setText("Contacto: " + cliente.getTelefono() + " - " + cliente.getCorreo());
        holder.tvDetallePropuestaItem.setText("Propuesta: " + propuesta.getTipoServicioNombre() + " - " + propuesta.getDescripcion() + " (" + precioFormateado + ")");
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
        TextView tvTituloSolicitudItem, tvMensajeClienteItem, tvInfoClienteItem, tvContactoClienteItem, tvDetallePropuestaItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTituloSolicitudItem = itemView.findViewById(R.id.tvTituloSolicitudItem);
            tvMensajeClienteItem = itemView.findViewById(R.id.tvMensajeClienteItem);
            tvInfoClienteItem = itemView.findViewById(R.id.tvInfoClienteItem);
            tvContactoClienteItem = itemView.findViewById(R.id.tvContactoClienteItem);
            tvDetallePropuestaItem = itemView.findViewById(R.id.tvDetallePropuestaItem);
        }
    }
}
