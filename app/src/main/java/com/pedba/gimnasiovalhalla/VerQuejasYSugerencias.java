package com.pedba.gimnasiovalhalla;

import static com.pedba.gimnasiovalhalla.QuejasSugerencias.CLIENTE_ID;
import static com.pedba.gimnasiovalhalla.QuejasSugerencias.DESCP_QUEJA;
import static com.pedba.gimnasiovalhalla.QuejasSugerencias.DESCP_SUGE;
import static com.pedba.gimnasiovalhalla.QuejasSugerencias.FECHA_QUEJA;
import static com.pedba.gimnasiovalhalla.QuejasSugerencias.FECHA_SUG;
import static com.pedba.gimnasiovalhalla.QuejasSugerencias.NUM_QUEJA;
import static com.pedba.gimnasiovalhalla.QuejasSugerencias.NUM_SUGE;
import static com.pedba.gimnasiovalhalla.QuejasSugerencias.QUEJAS;
import static com.pedba.gimnasiovalhalla.QuejasSugerencias.SUGERENCIAS;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pedba.gimnasiovalhalla.Adaptadores.ViewPagerAdapter;
import com.pedba.gimnasiovalhalla.Modelos.QuejaSugerenciaModelo;

import java.util.ArrayList;

public class VerQuejasYSugerencias extends AppCompatActivity {

    private final DatabaseReference TablaQuejas = FirebaseDatabase.getInstance().getReference(QUEJAS);
    private final DatabaseReference TablaSugerencias = FirebaseDatabase.getInstance().getReference(SUGERENCIAS);
    private TabLayout layout;
    private ViewPager vista;
    private ProgressDialog Progreso;
    public static ArrayList<QuejaSugerenciaModelo> Quejas;
    public static ArrayList<QuejaSugerenciaModelo> Sugerencias;
    private String Id, Descripcion, Fecha, Cliente_Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_quejas_y_sugerencias);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (verificarInternet()) descargarInformacion();
        else Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show();
        inicializarVistas();
    }

    private void descargarInformacion() {
        Progreso = new ProgressDialog(this);
        Progreso.setTitle(getString(R.string.DescargandoInformacion));
        Progreso.setMessage(getString(R.string.EspereUnMomento));
        Progreso.setCanceledOnTouchOutside(false);
        Progreso.show();
        TablaSugerencias.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Sugerencias = new ArrayList<>();
                    for (DataSnapshot i : snapshot.getChildren()) {
                        Id = i.child(NUM_SUGE).getValue().toString();
                        Descripcion = i.child(DESCP_SUGE).getValue().toString();
                        Fecha = i.child(FECHA_SUG).getValue().toString();
                        Cliente_Id = i.child(CLIENTE_ID).getValue().toString();
                        Sugerencias.add(new QuejaSugerenciaModelo(Id, Descripcion, Fecha, Cliente_Id));
                    }
                    Progreso.dismiss();
                } else {
                    Progreso.dismiss();
                    Toast.makeText(VerQuejasYSugerencias.this, "No hay sugerencias", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        TablaQuejas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Quejas = new ArrayList<>();
                    for (DataSnapshot i : snapshot.getChildren()) {
                        Id = i.child(NUM_QUEJA).getValue().toString();
                        Descripcion = i.child(DESCP_QUEJA).getValue().toString();
                        Fecha = i.child(FECHA_QUEJA).getValue().toString();
                        Cliente_Id = i.child(CLIENTE_ID).getValue().toString();
                        Quejas.add(new QuejaSugerenciaModelo(Id, Descripcion, Fecha, Cliente_Id));
                    }
                    Progreso.dismiss();
                    setFragments();
                } else {
                    Toast.makeText(VerQuejasYSugerencias.this, "No hay quejas", Toast.LENGTH_SHORT).show();
                    Progreso.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setFragments() {
        layout.setupWithViewPager(vista);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new FragmentQuejas(), "Quejas");
        adapter.addFragment(new FragmentSugerencias(), "Sugerencias");
        vista.setAdapter(adapter);
    }

    private void inicializarVistas() {
        layout = findViewById(R.id.Tab);
        vista = findViewById(R.id.ViewPager_Vista);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Boolean verificarInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}