package com.pedba.gimnasiovalhalla;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class AdministrarAparatos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.administrar_aparatos);
    }

    public void RegistrarAparato(View view) {
        startActivity(new Intent(this, FormularioAparato.class).putExtra("Opcion", 0));
    }

    public void EliminarAparato(View view) {
        startActivity(new Intent(this, FormularioAparato.class).putExtra("Opcion", 1));
    }

    public void ModificarAparato(View view) {
        startActivity(new Intent(this, FormularioAparato.class).putExtra("Opcion", 2));
    }

    public void ConsultarAparato(View view) {
        startActivity(new Intent(this, FormularioAparato.class).putExtra("Opcion", 3));
    }
}