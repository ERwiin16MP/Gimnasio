package com.pedba.gimnasiovalhalla;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
}