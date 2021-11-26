package com.pedba.gimnasiovalhalla;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MenuPersonal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_personal);
    }

    public void AdministrarClientes(View view) {
        startActivity(new Intent(this, AdministrarCliente.class));
    }

    public void AdministrarClases(View view) {
        startActivity(new Intent(this, AdministrarClases.class));
    }

    public void AdministrarAparatos(View view) {
        startActivity(new Intent(this, AdministrarAparatos.class));
    }

    public void verSugerenciasYQuejas(View view) {
        startActivity(new Intent(this, VerQuejasYSugerencias.class));
    }
}