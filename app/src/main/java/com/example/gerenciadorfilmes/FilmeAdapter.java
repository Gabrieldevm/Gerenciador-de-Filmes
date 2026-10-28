package com.example.gerenciadorfilmes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FilmeAdapter extends RecyclerView.Adapter<FilmeAdapter.FilmeViewHolder> {

    private List<Filme> filmesList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Filme filme);
        void onItemLongClick(Filme filme);
    }

    public FilmeAdapter(List<Filme> filmesList, OnItemClickListener listener) {
        this.filmesList = filmesList;
        this.listener = listener;
    }

    public void setFilmesList(List<Filme> filmesList) {
        this.filmesList = filmesList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FilmeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filme, parent, false);
        return new FilmeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmeViewHolder holder, int position) {
        Filme filme = filmesList.get(position);
        holder.txtNome.setText(filme.getNome());
        holder.txtTipoGenero.setText(filme.getTipo() + " - " + filme.getGenero());
        holder.txtAnoNota.setText("Ano: " + filme.getAno() + " | Nota: " + filme.getNota());
        holder.txtAssistido.setText(filme.isAssistido() ? "Assistido: Sim" : "Assistido: Não");

        holder.itemView.setOnClickListener(v -> listener.onItemClick(filme));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(filme);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return filmesList.size();
    }

    public static class FilmeViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome, txtTipoGenero, txtAnoNota, txtAssistido;

        public FilmeViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtItemNome);
            txtTipoGenero = itemView.findViewById(R.id.txtItemTipoGenero);
            txtAnoNota = itemView.findViewById(R.id.txtItemAnoNota);
            txtAssistido = itemView.findViewById(R.id.txtItemAssistido);
        }
    }
}