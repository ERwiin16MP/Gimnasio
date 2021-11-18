package com.pedba.gimnasiovalhalla;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AdministrarCliente extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.administrar_cliente);
    }

    public void RegistarCliente(View view) {
        startActivity(new Intent(this, FormularioCliente.class));
    }

    public void ModificarCliente(View view) {
        startActivity(new Intent(this, FormularioCliente.class));
    }

    public void ConsultarCliente(View view) {
        startActivity(new Intent(this, FormularioCliente.class));
    }

    public void EliminarCliente(View view) {
        startActivity(new Intent(this, FormularioCliente.class));
    }
}