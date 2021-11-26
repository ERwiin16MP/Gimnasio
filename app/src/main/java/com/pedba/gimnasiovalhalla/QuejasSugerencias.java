package com.pedba.gimnasiovalhalla;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class QuejasSugerencias extends AppCompatActivity {

    public static final String QUEJAS = "Quejas";
    public static final String SUGERENCIAS = "Sugerencias";
    public static final String NUM_QUEJA = "Num_Queja";
    public static final String DESCP_QUEJA = "Descp_Queja";
    public static final String NUM_SUGE = "Num_Suge";
    public static final String DESCP_SUGE = "Descp_Suge";
    public static final String FECHA_QUEJA = "Fecha_Queja";
    public static final String FECHA_SUG = "Fecha_sugerencia";
    public static final String CLIENTE_ID = "cliente_id";
    String Fecha;
    private RadioButton RB_Queja;
    private EditText TextBox_Texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quejas_sugerencias);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        inicializarVistas();
        Fecha = obtenerFechaYHoraActual();
    }

    public String obtenerFechaYHoraActual() {
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") DateFormat hourdateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return hourdateFormat.format(date);
    }

    public void enviarQS(View view) {
        String Texto = TextBox_Texto.getText().toString();
        String QS = QuejaOSugerencia();

        if (!verificarInternet())
            Toast.makeText(this, R.string.NoHayInternet, Toast.LENGTH_SHORT).show();
        else if (Texto.isEmpty())
            Snackbar.make(view, getString(R.string.CampoRequerido), Snackbar.LENGTH_LONG).show();
        else
            registarQuejaOSugerencia(Texto, QS);
    }

    private void registarQuejaOSugerencia(String Texto, String QS) {
        ProgressDialog Progreso = new ProgressDialog(this);
        if (QS.equals(QUEJAS))
            Progreso.setTitle(getString(R.string.EnviandoQueja));
        else Progreso.setTitle(getString(R.string.EnviandoSugerencia));
        Progreso.setMessage(getString(R.string.EspereUnMomento));
        Progreso.setCanceledOnTouchOutside(false);
        Progreso.show();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference BaseDeDatosQuejasOSugerencias = database.getReference(QS);
        Map<String, Object> Datos = new HashMap<>();
        String Id = BaseDeDatosQuejasOSugerencias.push().getKey();
        if (QS.equals(QUEJAS)) {
            Datos.put(NUM_QUEJA, Id);
            Datos.put(DESCP_QUEJA, Texto);
            Datos.put(FECHA_QUEJA, Fecha);
        } else {
            Datos.put(NUM_SUGE, Id);
            Datos.put(DESCP_SUGE, Texto);
            Datos.put(FECHA_SUG, Fecha);
        }
        Datos.put(CLIENTE_ID, Login.Id);
        BaseDeDatosQuejasOSugerencias.child(Id).setValue(Datos).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                Progreso.dismiss();
            if (QS.equals(QUEJAS))
                Toast.makeText(QuejasSugerencias.this, R.string.QuejaRegistrada, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(QuejasSugerencias.this, R.string.SugerenciaRegistrada, Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(QuejasSugerencias.this, R.string.HaOcurridoUnError, Toast.LENGTH_SHORT).show();
            Progreso.dismiss();
        });
    }

    private String QuejaOSugerencia() {
        if (RB_Queja.isChecked()) {
            return QUEJAS;
        } else return SUGERENCIAS;
    }

    @Override
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

    private void inicializarVistas() {
        RB_Queja = findViewById(R.id.RadioButton_Queja);
        TextBox_Texto = findViewById(R.id.EditText_QuejaOSugerencia);
    }
}