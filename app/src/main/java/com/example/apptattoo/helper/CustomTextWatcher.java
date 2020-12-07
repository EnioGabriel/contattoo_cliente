package com.example.apptattoo.helper;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Timer;
import java.util.TimerTask;

public abstract class CustomTextWatcher implements TextWatcher { //Notice abstract class so we leave abstract method textWasChanged() for implementing class to define it

    private Timer timer;
    private long tempoEspera;
    private final TextInputEditText meuTxt;

    protected CustomTextWatcher(TextInputEditText tView, long delay) {
        meuTxt = tView;
        tempoEspera = delay;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // resetando tempo enquanto usuario digita
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        // ao digitar inicia o timer
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                textWasChanged();
            }
        }, tempoEspera);
    }

    public abstract void textWasChanged(); //metodo para ser implementado na classe que chamar

}