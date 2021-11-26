package com.pedba.gimnasiovalhalla;

import static com.pedba.gimnasiovalhalla.ContratarClase.ACCION;
import static com.pedba.gimnasiovalhalla.EligePlanDePago.JUEVES;
import static com.pedba.gimnasiovalhalla.EligePlanDePago.LUNES;
import static com.pedba.gimnasiovalhalla.EligePlanDePago.MARTES;
import static com.pedba.gimnasiovalhalla.EligePlanDePago.MIERCOLES;
import static com.pedba.gimnasiovalhalla.EligePlanDePago.SABADO;
import static com.pedba.gimnasiovalhalla.EligePlanDePago.VIERNES;
import static com.pedba.gimnasiovalhalla.FormularioAparato.AREA;
import static com.pedba.gimnasiovalhalla.FormularioAparato.AREAS;
import static com.pedba.gimnasiovalhalla.FormularioAparato.NOM_AREA;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RentarAparatos extends AppCompatActivity {

    private final DatabaseReference TablaAreas = FirebaseDatabase.getInstance().getReference(AREAS);
    private CheckBox Lunes, Martes, Miercoles, Jueves, Viernes, Sabado;
    private Spinner Areas;
    private String Areas_Array[];
    private ArrayAdapter<String> adapter;
    private int Contador = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rentar_aparatos);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        inicializarVistas();
        setSpinner();
    }

    private void setSpinner() {
        TablaAreas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Areas_Array = new String[(int) snapshot.getChildrenCount()];
                    for (DataSnapshot i : snapshot.getChildren()) {
                        Areas_Array[Contador] = i.child(NOM_AREA).getValue().toString();
                        Contador++;
                    }
                    List list = new LinkedList();
                    for (int i = 0; i < Areas_Array.length; i++) {
                        list.add(Areas_Array[i]);
                    }
                    Collections.sort(list);
                    adapter = new ArrayAdapter<String>(RentarAparatos.this, android.R.layout.simple_spinner_dropdown_item, list);
                    Areas.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarVistas() {
        Lunes = findViewById(R.id.Lunes);
        Martes = findViewById(R.id.Martes);
        Miercoles = findViewById(R.id.Miercoles);
        Jueves = findViewById(R.id.Jueves);
        Viernes = findViewById(R.id.Viernes);
        Sabado = findViewById(R.id.Sabado);
        Areas = findViewById(R.id.Spinner_RentarAparato);
    }

    public void regresar(View view) {
        Intent Regresar_MenuC = new Intent(this, MenuCliente.class);
        startActivity(Regresar_MenuC);
    }


    public void rentar(View view) {
        if (!verificarInternet())
            Toast.makeText(this, R.string.NoHayInternet, Toast.LENGTH_SHORT).show();
        else if (Lunes.isChecked() == false && Martes.isChecked() == false && Miercoles.isChecked() == false
                && Jueves.isChecked() == false && Viernes.isChecked() == false && Sabado.isChecked() == false)
            Snackbar.make(view, R.string.SeleccionePorLoMenosUnDia, Snackbar.LENGTH_SHORT).show();
        else {
            startActivity(new Intent(this, EligePlanDePago.class)
                    .putExtra(LUNES, String.valueOf(Lunes.isChecked()))
                    .putExtra(MARTES, String.valueOf(Martes.isChecked()))
                    .putExtra(MIERCOLES, String.valueOf(Miercoles.isChecked()))
                    .putExtra(JUEVES, String.valueOf(Jueves.isChecked()))
                    .putExtra(VIERNES, String.valueOf(Viernes.isChecked()))
                    .putExtra(SABADO, String.valueOf(Sabado.isChecked()))
                    .putExtra(AREA, Areas.getSelectedItem().toString())
                    .putExtra(ACCION, AREA));
        }
    }

    private Boolean verificarInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
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