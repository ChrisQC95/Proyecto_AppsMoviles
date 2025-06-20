package com.example.proyecto_gestortrabajadoresinformales.trabajador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestortrabajadoresinformales.R;
import com.example.proyecto_gestortrabajadoresinformales.beans.Calificacion;

import java.util.List;

public class CalificacionAdapter extends RecyclerView.Adapter<CalificacionAdapter.ViewHolder> {
    private List<Calificacion> calificaciones;

    public CalificacionAdapter(List<Calificacion> calificaciones) {
        this.calificaciones = calificaciones;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calificacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Calificacion calificacion = calificaciones.get(position);
        holder.tvNombreCliente.setText("Cliente: " + calificacion.getNombreCliente());
        holder.tvTipoTrabajo.setText("Tipo de trabajo: " + calificacion.getTipoTrabajo());
        holder.ratingBar.setRating(calificacion.getPuntuacion());
        holder.tvComentario.setText("Comentario: " + calificacion.getComentario());
    }

    @Override
    public int getItemCount() {
        return calificaciones.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreCliente, tvTipoTrabajo, tvComentario;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreCliente = itemView.findViewById(R.id.tvNombreCliente);
            tvTipoTrabajo = itemView.findViewById(R.id.tvTipoTrabajo);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvComentario = itemView.findViewById(R.id.tvComentario);
        }
    }
}
