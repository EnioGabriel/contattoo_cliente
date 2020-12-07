package com.example.apptattoo.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apptattoo.R;
import com.example.apptattoo.model.Proposta;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterPropostas extends RecyclerView.Adapter<AdapterPropostas.MyViewHolder> {

    private List<Proposta> listaProposta;
    private Context context;

    public AdapterPropostas(List<Proposta> listaProposta, Context context) {
        this.listaProposta = listaProposta;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_propostas, parent, false);
        return new AdapterPropostas.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final Proposta proposta = listaProposta.get(position);

        //Carrega dados para o recyclerview
        Uri uriFotoUsuario = Uri.parse(proposta.getFotoTatuador());
        if (!proposta.getFotoTatuador().equals("") && proposta.getFotoTatuador() != null){
            Glide.with(context).load(uriFotoUsuario).into(holder.fotoPerfil);
        }
        else
            holder.fotoPerfil.setImageResource(R.drawable.avatar);

        holder.nome.setText("@"+proposta.getNomeTatuador());
        if(proposta.getTipoOrcamento().equals("sessaoUnica")){
            holder.valor.setText(proposta.getValorTotalSessaoUnica());
        }else
            holder.valor.setText(proposta.getValorTotalSessaoMultipla());
    }

    @Override
    public int getItemCount() {
        return listaProposta.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView fotoPerfil;
        TextView nome, valor;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fotoPerfil = itemView.findViewById(R.id.imgFotoTatuadorProposta);
            nome = itemView.findViewById(R.id.lblNomeTatuadorProposta);
            valor = itemView.findViewById(R.id.lblValorProposta);
        }
    }
}

