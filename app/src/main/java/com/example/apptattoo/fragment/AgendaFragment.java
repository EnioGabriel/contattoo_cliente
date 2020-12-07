package com.example.apptattoo.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.apptattoo.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;


public class AgendaFragment extends Fragment {

    private SmartTabLayout smartTabLayoutConsulta;
    private ViewPager viewPagerConsulta;
    private FragmentPagerItemAdapter adapter;

    public AgendaFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_agenda, container, false);

        smartTabLayoutConsulta = view.findViewById(R.id.smartTabAgenda);
        viewPagerConsulta = view.findViewById(R.id.viewPagerAgenda);

        //Configurando smartTab
        int position = FragmentPagerItem.getPosition(getArguments());
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), FragmentPagerItems.with(getActivity())
                .add("Em andamento", TatuagemEmAndamentoFragment.class)
                .add("Finalizados", TatuagemFinalizadaFragment.class)
                .create());

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPagerAgenda);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) view.findViewById(R.id.smartTabAgenda);
        viewPagerTab.setViewPager(viewPager);

        return view;
    }
}