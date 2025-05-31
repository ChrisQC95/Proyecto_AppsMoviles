package com.example.proyecto_gestortrabajadoresinformales; // Ajusta el paquete

import static java.lang.String.valueOf;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.proyecto_gestortrabajadoresinformales.Propuesta; // Ajusta el paquete si lo pusiste en 'model'
import com.example.proyecto_gestortrabajadoresinformales.beans.TipoServicio;

public class PropuestaAdapter extends RecyclerView.Adapter<PropuestaAdapter.PropuestaViewHolder> {

    private List<Propuesta> propuestasList;
    private List<TipoServicio> tiposServicioList;
    public PropuestaAdapter(List<Propuesta> propuestasList, List<TipoServicio> tiposServicioList) {
        this.propuestasList = propuestasList;
        this.tiposServicioList = tiposServicioList;
    }

    @NonNull
    @Override
    public PropuestaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.plantilla_listado_propuestas, parent, false);
        return new PropuestaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropuestaViewHolder holder, int position) {
        Propuesta propuesta = propuestasList.get(position);

        holder.textViewTitulo.setText(propuesta.getTitulo());
        holder.textViewPrecio.setText(String.format("Precio estimado: S/ %.2f", propuesta.getPrecio()));
        holder.textViewDescripcion.setText(propuesta.getDescripcion());
        holder.textViewCalificacion.setText(String.format("Calificación: %d", (int) Math.round(propuesta.getCalificacion())));

        // Ahora mostramos el nombre del tipo de servicio
        // Buscar nombre del tipo de servicio
        String nombreTipoServicio = obtenerNombreTipoServicioPorId(propuesta.getTipo_servicio());
        holder.textViewTipoServicio.setText("Tipo de Servicio: " + nombreTipoServicio);
    }

    private String obtenerNombreTipoServicioPorId(int id) {
        String idStr = String.valueOf(id);
        for (TipoServicio tipo : tiposServicioList) {
            if (tipo.getId().equals(idStr)) {
                return tipo.getNombre();
            }
        }
        return "Desconocido";
    }

    @Override
    public int getItemCount() {
        return propuestasList.size();
    }

    public void setPropuestas(List<Propuesta> nuevasPropuestas) {
        this.propuestasList = nuevasPropuestas;
        notifyDataSetChanged();
    }

    public static class PropuestaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitulo;
        TextView textViewPrecio;
        TextView textViewDescripcion;
        TextView textViewCalificacion;
        TextView textViewTipoServicio;

        public PropuestaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitulo = itemView.findViewById(R.id.textViewTituloPropuesta);
            textViewPrecio = itemView.findViewById(R.id.textViewPrecioPropuesta);
            textViewDescripcion = itemView.findViewById(R.id.textViewDescripcionPropuesta);
            textViewCalificacion = itemView.findViewById(R.id.textViewCalificacionPropuesta);
            textViewTipoServicio = itemView.findViewById(R.id.textViewTipoServicio);
        }
    }
}