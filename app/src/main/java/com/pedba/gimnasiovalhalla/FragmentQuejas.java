package com.pedba.gimnasiovalhalla;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pedba.gimnasiovalhalla.Adaptadores.QuejasAdapter;
import com.pedba.gimnasiovalhalla.Modelos.QuejaSugerenciaModelo;

import java.util.ArrayList;

public class FragmentQuejas extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quejas, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.RecyclerView_Quejas);
        ArrayList<QuejaSugerenciaModelo> list = VerQuejasYSugerencias.Quejas;
        recyclerView.setAdapter(new QuejasAdapter(getContext(), list));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        return view;
    }
}