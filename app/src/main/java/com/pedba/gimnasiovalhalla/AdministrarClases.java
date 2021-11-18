package com.pedba.gimnasiovalhalla;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class AdministrarClases extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.administrar_clases);
    }

    public void RegistrarClase(View view) {
        startActivity(new Intent(this, FormularioClase.class).putExtra("Opcion", 0));
    }

    public void Eliminarclase(View view) {
        startActivity(new Intent(this, FormularioClase.class).putExtra("Opcion", 1));
    }

    public void ModificarClase(View view) {
        startActivity(new Intent(this, FormularioClase.class).putExtra("Opcion", 2));
    }

    public void Consultarclase(View view) {
        startActivity(new Intent(this, FormularioClase.class).putExtra("Opcion", 3));
    }
}