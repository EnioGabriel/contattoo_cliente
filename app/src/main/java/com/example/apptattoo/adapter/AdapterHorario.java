package com.example.apptattoo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptattoo.R;
import com.example.apptattoo.helper.MarcadorDiaSessao;
import com.example.apptattoo.helper.RecyclerViewClickInterface;
import com.example.apptattoo.model.Horario;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.threeten.bp.LocalDate;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterHorario extends RecyclerView.Adapter<AdapterHorario.MyViewHolder> {

    private List<Horario> listaHorario;
    private Context context;
    private Calendar horarioTermino;
    private DateFormat horarioFormat;
    private RecyclerViewClickInterface recyclerViewClickInterface;

    public AdapterHorario(List<Horario> listaHorario, Context context, RecyclerViewClickInterface recyclerViewClickInterface) {
        this.listaHorario = listaHorario;
        this.context = context;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_horario, parent, false);
        return new AdapterHorario.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        Horario horario = listaHorario.get(position);

        DecimalFormat df = new DecimalFormat("00");//formatando para sempre ter 2 digitos em hora e minuto
        //Carrega dados para o recyclerview
        holder.horaInicio.setText(df.format(horario.getHora()) + ":" + df.format(horario.getMinuto()));
        horarioTermino = Calendar.getInstance();
        horarioTermino.set(Calendar.HOUR_OF_DAY, horario.getHora());
        horarioTermino.set(Calendar.MINUTE, horario.getMinuto());

        int duracao = horario.getDuracao();
        horarioTermino.add(Calendar.MINUTE, duracao);

        horarioFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        horarioFormat = new SimpleDateFormat("HH:mm");

        holder.horaTermino.setText(horarioFormat.format(horarioTermino.getTime()));
        holder.data.setText(df.format(horario.getDia())+"/"+df.format(horario.getMes())+"/"+df.format(horario.getAno()));
        holder.btnAceitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerViewClickInterface.onItemClick(position);
            }
        });
        LocalDate date = LocalDate.of(horario.getAno(), horario.getMes(), horario.getDia());
        holder.calendarDayAdapter.setCurrentDate(date);
        ArrayList<CalendarDay> dataSelecionada = new ArrayList<>();
        CalendarDay day = CalendarDay.from(date);
        dataSelecionada.add(day);
        holder.calendarDayAdapter.addDecorator(new MarcadorDiaSessao(Color.RED, dataSelecionada));
    }


    @Override
    public int getItemCount() {
        return listaHorario.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        EditText horaInicio, horaTermino, data;
        MaterialCalendarView calendarDayAdapter;
        Button btnAceitar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            calendarDayAdapter = itemView.findViewById(R.id.calendarDayAdapterHorario);
            btnAceitar = itemView.findViewById(R.id.btnAceitarHorarioAdapter);
            horaInicio = itemView.findViewById(R.id.txtHorarioInicioAdapter);
            horaTermino = itemView.findViewById(R.id.txtPrevisaoTerminoAdapter);
            data = itemView.findViewById(R.id.txtDataAdapter);
        }
    }
}
