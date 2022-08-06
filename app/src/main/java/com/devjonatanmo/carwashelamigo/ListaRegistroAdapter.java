package com.devjonatanmo.carwashelamigo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListaRegistroAdapter extends RecyclerView.Adapter<ListaRegistroAdapter.RegistroViewHolder> {

    ArrayList<Registro> listaRegistro;

    public ListaRegistroAdapter(ArrayList<Registro> listaRegistro) {
        this.listaRegistro = listaRegistro;
    }

    @NonNull
    @Override
    public RegistroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_items_card, null, false);
        return new RegistroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistroViewHolder holder, int position) {
        holder.txtNumPlaca.setText("Numero de placa: " + listaRegistro.get(position).getNumPlaca());
        holder.txtFecha.setText("Fecha de lavado: " + listaRegistro.get(position).getFecha());
        holder.txtPago.setText("Pago: " + listaRegistro.get(position).getPago());
    }

    @Override
    public int getItemCount() {
        return listaRegistro.size();
    }

    public class RegistroViewHolder extends RecyclerView.ViewHolder {

        TextView txtNumPlaca, txtFecha, txtPago;

        public RegistroViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNumPlaca = itemView.findViewById(R.id.txtNumPlaca);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtPago = itemView.findViewById(R.id.txtPago);
        }
    }
}
