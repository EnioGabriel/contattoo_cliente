package com.example.apptattoo.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apptattoo.R;

public class AdapterPartesCorpo extends RecyclerView.Adapter<AdapterPartesCorpo.MyViewHolder> {
    private String[] listaCorpo;
    private Context context;
    private int layoutResource;

    // Armazena o ultimo item selecionado (usado para limpar o item selecionado anterior)
    private int ultimoItemCLicado = -1;

    public AdapterPartesCorpo(Context context, int resource, String[] listaCorpo) {
        this.listaCorpo = listaCorpo;
        this.layoutResource = resource;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(layoutResource, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.parteCorpo.setText(listaCorpo[position]);
        holder.imagem.setImageResource(R.drawable.ic_baseline_check_circle_outline_24);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ultimoItemCLicado = holder.getAdapterPosition();
                recuperaItemCLicado();
                notifyDataSetChanged();
            }
        });
        if (ultimoItemCLicado==position){
            holder.imagem.setVisibility(View.VISIBLE);
        }
        else {
            holder.imagem.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return listaCorpo.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imagem;
        TextView parteCorpo;
        RecyclerView recyclerViewPartesCorpo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerViewPartesCorpo = itemView.findViewById(R.id.RecyclerViewPartesCorpo);
            imagem = itemView.findViewById(R.id.imgParteCorpoOk);
            parteCorpo = itemView.findViewById(R.id.lblPartesCorpo);
        }
    }
    public String recuperaItemCLicado(){
        String parteCorpo = null;
        if (ultimoItemCLicado!=-1){
            parteCorpo = listaCorpo[ultimoItemCLicado];
        }
        return parteCorpo;
    }
}