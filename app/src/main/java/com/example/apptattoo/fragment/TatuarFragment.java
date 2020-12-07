package com.example.apptattoo.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.example.apptattoo.R;
import com.example.apptattoo.activity.SelecionaParteCorpoActivity;
import com.example.apptattoo.activity.SelecionaTatuadorConsultaActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class TatuarFragment extends Fragment {
    private FloatingActionButton fabConsultarTattoo;
    final CharSequence[] opcoes = {"Consultar com tatuador específico", "Consulta aberta para todos tatuadores", "Cancelar"};

    public TatuarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tatuar, container, false);

        fabConsultarTattoo = view.findViewById(R.id.fabConsultarTattoo);
        fabConsultarTattoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDialog();
            }
        });
        return view;
    }

    private void abrirDialog(){
        new MaterialDialog.Builder(getContext())
                .theme(Theme.DARK)
                .items(opcoes)
                .backgroundColor(getResources().getColor(R.color.cor_fundo))
                .itemsColor(getResources().getColor(R.color.cor_botoes))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        if (opcoes[position].equals("Consultar com tatuador específico")) {
                            startActivity(new Intent(getActivity(), SelecionaTatuadorConsultaActivity.class));
                        } else if (opcoes[position].equals("Consulta aberta para todos tatuadores")) {
                            startActivity(new Intent(getActivity(), SelecionaParteCorpoActivity.class));
                        } else if (opcoes[position].equals("Cancelar")) {
                            dialog.dismiss();
                        }
                    }
                })
                .titleGravity(GravityEnum.CENTER)
                .itemsGravity(GravityEnum.CENTER)
                .show();
    }

}