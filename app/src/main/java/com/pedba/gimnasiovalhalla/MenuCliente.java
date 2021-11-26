package com.pedba.gimnasiovalhalla;

import static com.pedba.gimnasiovalhalla.Login.Id;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MenuCliente extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_cliente);
    }

    public void ContratarUnaClase(View view) {
        startActivity(new Intent(this, ContratarClase.class));
    }

    public void RentarAparatos(View view) {
        startActivity(new Intent(this, RentarAparatos.class));
    }

    public void DejarQuejasYSugerencias(View view) {
        startActivity(new Intent(this, QuejasSugerencias.class));
    }
}