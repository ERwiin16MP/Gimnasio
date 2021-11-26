package com.pedba.gimnasiovalhalla;

import static com.pedba.gimnasiovalhalla.ContratarClase.ACCION;
import static com.pedba.gimnasiovalhalla.ContratarClase.CLASE;
import static com.pedba.gimnasiovalhalla.FormularioAparato.AREA;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class EligePlanDePago extends AppCompatActivity {

    public static final String LUNES = "Lunes";
    public static final String MARTES = "Martes";
    public static final String MIERCOLES = "Miercoles";
    public static final String JUEVES = "Jueves";
    public static final String VIERNES = "Viernes";
    public static final String SABADO = "Sabado";
    public static final String PLAN = "Plan";
    public static final String VISITA = "Visita";
    public static final String MENSUALIDAD = "Mensualidad";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.elige_plan_de_pago);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void visita(View view) {
        switch (getIntent().getExtras().getString(ACCION, null)) {
            case AREA:
                startActivity(new Intent(this, ProporcionaTarjeta.class)
                        .putExtra(LUNES, getIntent().getExtras().getString(LUNES, "null"))
                        .putExtra(MARTES, getIntent().getExtras().getString(MARTES, "null"))
                        .putExtra(MIERCOLES, getIntent().getExtras().getString(MIERCOLES, "null"))
                        .putExtra(JUEVES, getIntent().getExtras().getString(JUEVES, "null"))
                        .putExtra(VIERNES, getIntent().getExtras().getString(VIERNES, "null"))
                        .putExtra(SABADO, getIntent().getExtras().getString(SABADO, "null"))
                        .putExtra(AREA, getIntent().getExtras().getString(AREA, "null"))
                        .putExtra(PLAN, VISITA)
                        .putExtra(ACCION, AREA));
                break;
            case CLASE:
                startActivity(new Intent(this, ProporcionaTarjeta.class)
                        .putExtra(CLASE, getIntent().getExtras().getString(CLASE, "null"))
                        .putExtra(PLAN, VISITA)
                        .putExtra(ACCION, CLASE));
                break;
        }
    }

    public void mensualidad(View view) {
        switch (getIntent().getExtras().getString(ACCION, null)) {
            case AREA:
                startActivity(new Intent(this, ProporcionaTarjeta.class)
                        .putExtra(LUNES, getIntent().getExtras().getString(LUNES, "null"))
                        .putExtra(MARTES, getIntent().getExtras().getString(MARTES, "null"))
                        .putExtra(MIERCOLES, getIntent().getExtras().getString(MIERCOLES, "null"))
                        .putExtra(JUEVES, getIntent().getExtras().getString(JUEVES, "null"))
                        .putExtra(VIERNES, getIntent().getExtras().getString(VIERNES, "null"))
                        .putExtra(SABADO, getIntent().getExtras().getString(SABADO, "null"))
                        .putExtra(AREA, getIntent().getExtras().getString(AREA, "null"))
                        .putExtra(PLAN, MENSUALIDAD)
                        .putExtra(ACCION, AREA));
                break;
            case CLASE:
                startActivity(new Intent(this, ProporcionaTarjeta.class)
                        .putExtra(CLASE, getIntent().getExtras().getString(CLASE, "null"))
                        .putExtra(PLAN, MENSUALIDAD)
                        .putExtra(ACCION, CLASE));
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}