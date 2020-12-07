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
import com.example.apptattoo.model.LocalEstudio;
import com.example.apptattoo.model.Proposta;

import java.text.DecimalFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterTatuagemAndamento extends RecyclerView.Adapter<AdapterTatuagemAndamento.MyViewHolder> {

    private List<Proposta> listaTatuagemAndamento;
    private Context context;

    public AdapterTatuagemAndamento(List<Proposta> listaTatuagemAndamento, Context context) {
        this.listaTatuagemAndamento = listaTatuagemAndamento;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_tatuagem_andamento, parent, false);
        return new AdapterTatuagemAndamento.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final Proposta proposta = listaTatuagemAndamento.get(position);

        //Carrega dados para o recyclerview
        Uri uriFotoUsuario = Uri.parse(proposta.getFotoTatuador());
        if (!proposta.getFotoTatuador().equals("") && proposta.getFotoTatuador() != null){
            Glide.with(context).load(uriFotoUsuario).into(holder.fotoPerfil);
        }
        else
            holder.fotoPerfil.setImageResource(R.drawable.avatar);

        holder.nome.setText("@"+proposta.getNomeTatuador());

        if (proposta.getTipoOrcamento().equals("sessaoUnica")){
            holder.valor.setText(proposta.getValorTotalSessaoUnica());
        }else {
            holder.valor.setText(proposta.getValorPorSessao());
        }

        String[] separandoData = proposta.getData().split("-");
        holder.data.setText(separandoData[2]+"/"+separandoData[1]+"/"+separandoData[0]);
        DecimalFormat df = new DecimalFormat("00");
        String[] separandoHora = proposta.getHora().split(":");
        holder.hora.setText(df.format(Integer.parseInt(separandoHora[0]))+ "h" + ":" + df.format(Integer.parseInt(separandoHora[1])) + "m");
    }

    @Override
    public int getItemCount() {
        return listaTatuagemAndamento.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView fotoPerfil;
        TextView nome, data, hora, valor;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fotoPerfil = itemView.findViewById(R.id.imgFotoTatuadorAndamento);
            nome = itemView.findViewById(R.id.lblNomeTatuadorAndamento);
            data = itemView.findViewById(R.id.lblDataAndamento);
            hora = itemView.findViewById(R.id.lblHoraInicioAndamento);
            valor = itemView.findViewById(R.id.lblValorAndamento);
        }
    }
}
