package com.pedba.gimnasiovalhalla;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class AdministrarCliente extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.administrar_cliente);
    }

    public void RegistarCliente(View view) {
        startActivity(new Intent(this, FormularioCliente.class).putExtra("Opcion", 0));
    }

    public void EliminarCliente(View view) {
        startActivity(new Intent(this, FormularioCliente.class).putExtra("Opcion", 1));
    }

    public void ModificarCliente(View view) {
        startActivity(new Intent(this, FormularioCliente.class).putExtra("Opcion", 2));
    }

    public void ConsultarCliente(View view) {
        startActivity(new Intent(this, FormularioCliente.class).putExtra("Opcion", 3));
    }
}