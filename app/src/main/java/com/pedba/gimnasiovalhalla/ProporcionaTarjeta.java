package com.pedba.gimnasiovalhalla;

import static com.pedba.gimnasiovalhalla.ContratarClase.ACCION;
import static com.pedba.gimnasiovalhalla.ContratarClase.CLASE;
import static com.pedba.gimnasiovalhalla.EligePlanDePago.JUEVES;
import static com.pedba.gimnasiovalhalla.EligePlanDePago.LUNES;
import static com.pedba.gimnasiovalhalla.EligePlanDePago.MARTES;
import static com.pedba.gimnasiovalhalla.EligePlanDePago.MENSUALIDAD;
import static com.pedba.gimnasiovalhalla.EligePlanDePago.MIERCOLES;
import static com.pedba.gimnasiovalhalla.EligePlanDePago.PLAN;
import static com.pedba.gimnasiovalhalla.EligePlanDePago.SABADO;
import static com.pedba.gimnasiovalhalla.EligePlanDePago.VIERNES;
import static com.pedba.gimnasiovalhalla.EligePlanDePago.VISITA;
import static com.pedba.gimnasiovalhalla.FormularioAparato.AREA;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ProporcionaTarjeta extends AppCompatActivity {
    public static final String RENTA = "Renta_aparatos";
    public static final String CONTRATA_CLASE = "Contrata_clase";
    private static final String ID_AREA = "Id_area";
    private static final String CLIENTE = "Id_Cliente";
    private static final String LUNES_A = "Lunes_aparato";
    private static final String MARTES_A = "Martes_aparato";
    private static final String MIERCOLES_A = "Miercoles_aparato";
    private static final String JUEVES_A = "Jueves_aparato";
    private static final String VIERNES_A = "Viernes_aparato";
    private static final String SABADO_A = "Sabado_aparato";
    private static final String ID_CONTRATA_CLASE = "Id_contrata_clase";
    private static final String CLASE_ = "Id_Clase";
    String Lunes, Martes, Miercoles, Jueves, Viernes, Sabado, Area, Clase, NombreCliente;
    ProgressDialog Progreso;
    private DatabaseReference TablaRentaAparatos = FirebaseDatabase.getInstance().getReference(RENTA);
    private DatabaseReference TablaContrataClase = FirebaseDatabase.getInstance().getReference(CONTRATA_CLASE);
    private EditText TextBox_NumeroDeLaTarjeta, TextBox_MM, TextBox_AA, TextBox_CVC, TextBox_Titular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proporciona_tarjeta);
        inicializarVistas();
        NombreCliente = Login.Id;
    }

    private void inicializarVistas() {
        TextBox_NumeroDeLaTarjeta = findViewById(R.id.EditText_NumeroDeTarjeta);
        TextBox_MM = findViewById(R.id.EditText_MM);
        TextBox_AA = findViewById(R.id.EditText_AA);
        TextBox_CVC = findViewById(R.id.EditText_CVC);
        TextBox_Titular = findViewById(R.id.EditText_TitularDeLaTarjeta);
    }

    public void cancelar(View view) {
        startActivity(new Intent(this, MenuCliente.class));
        finish();
    }

    public void pagar(View view) {
        if (!verificarInternet())
            Toast.makeText(this, R.string.NoHayInternet, Toast.LENGTH_SHORT).show();
        else {
            String NumeroDeTarjeta = TextBox_NumeroDeLaTarjeta.getText().toString(), MM = TextBox_MM.getText().toString(),
                    AA = TextBox_AA.getText().toString(), CVC = TextBox_CVC.getText().toString(), Titular = TextBox_Titular.getText().toString();
            if (NumeroDeTarjeta.isEmpty()) {
                Snackbar.make(view, "Ingrese el número de la tarjeta", Snackbar.LENGTH_SHORT).show();
                TextBox_NumeroDeLaTarjeta.setError(getString(R.string.CampoRequerido));
                TextBox_NumeroDeLaTarjeta.requestFocus();
            } else if (MM.isEmpty()) {
                Snackbar.make(view, "Ingrese el MM de la tarjeta", Snackbar.LENGTH_SHORT).show();
                TextBox_MM.setError(getString(R.string.CampoRequerido));
                TextBox_MM.requestFocus();
            } else if (AA.isEmpty()) {
                Snackbar.make(view, "Ingrese el AA de la tarjeta", Snackbar.LENGTH_SHORT).show();
                TextBox_AA.setError(getString(R.string.CampoRequerido));
                TextBox_AA.requestFocus();
            } else if (CVC.isEmpty()) {
                Snackbar.make(view, "Ingrese el CVC de la tarjeta", Snackbar.LENGTH_SHORT).show();
                TextBox_CVC.setError(getString(R.string.CampoRequerido));
                TextBox_CVC.requestFocus();
            } else if (Titular.isEmpty()) {
                Snackbar.make(view, "Ingrese el tutular de la tarjeta", Snackbar.LENGTH_SHORT).show();
                TextBox_Titular.setError(getString(R.string.CampoRequerido));
                TextBox_Titular.requestFocus();
            } else {
                registarCompra();
            }
        }
    }

    private void registarCompra() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProporcionaTarjeta.this);
        builder.setTitle("Realizar pago")
                .setMessage("¿Confirmar?")
                .setPositiveButton("Vale", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (getIntent().getExtras().getString(ACCION, null)) {
                            case AREA:
                                Lunes = getIntent().getExtras().getString(LUNES, "null");
                                Martes = getIntent().getExtras().getString(MARTES, "null");
                                Miercoles = getIntent().getExtras().getString(MIERCOLES, "null");
                                Jueves = getIntent().getExtras().getString(JUEVES, "null");
                                Viernes = getIntent().getExtras().getString(VIERNES, "null");
                                Sabado = getIntent().getExtras().getString(SABADO, "null");
                                Area = getIntent().getExtras().getString(AREA, "null");

                                Progreso = new ProgressDialog(ProporcionaTarjeta.this);
                                Progreso.setTitle(getString(R.string.ProcesoDeRegistro));
                                Progreso.setMessage(getString(R.string.EspereUnMomento));
                                Progreso.setCanceledOnTouchOutside(false);
                                Progreso.show();

                                String Id = TablaRentaAparatos.push().getKey();
                                Map<String, Object> Datos = new HashMap<>();
                                Datos.put(ID_AREA, Id);
                                Datos.put(AREA, Area);
                                Datos.put(CLIENTE, NombreCliente);
                                Datos.put(LUNES_A, Lunes);
                                Datos.put(MARTES_A, Martes);
                                Datos.put(MIERCOLES_A, Miercoles);
                                Datos.put(JUEVES_A, Jueves);
                                Datos.put(VIERNES_A, Viernes);
                                Datos.put(SABADO_A, Sabado);
                                switch (getIntent().getExtras().getString(PLAN, null)) {
                                    case VISITA:
                                        Datos.put(PLAN, VISITA);
                                        break;
                                    case MENSUALIDAD:
                                        Datos.put(PLAN, MENSUALIDAD);
                                        break;
                                }
                                TablaRentaAparatos.child(Id).setValue(Datos).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Progreso.dismiss();
                                            Toast.makeText(ProporcionaTarjeta.this, "Se completo el pago", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(ProporcionaTarjeta.this, MenuCliente.class));
                                            finish();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Progreso.dismiss();
                                        Toast.makeText(ProporcionaTarjeta.this, "Ha ocurrido un error inesperado", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            case CLASE:
                                Clase = getIntent().getExtras().getString(CLASE, "null");

                                Progreso = new ProgressDialog(ProporcionaTarjeta.this);
                                Progreso.setTitle(getString(R.string.ProcesoDeRegistro));
                                Progreso.setMessage(getString(R.string.EspereUnMomento));
                                Progreso.setCanceledOnTouchOutside(false);
                                Progreso.show();

                                String Id_ = TablaContrataClase.push().getKey();
                                Map<String, Object> Dato = new HashMap<>();
                                Dato.put(ID_CONTRATA_CLASE, Id_);
                                Dato.put(CLIENTE, NombreCliente);
                                Dato.put(CLASE_, Clase);
                                switch (getIntent().getExtras().getString(PLAN, null)) {
                                    case VISITA:
                                        Dato.put(PLAN, VISITA);
                                        break;
                                    case MENSUALIDAD:
                                        Dato.put(PLAN, MENSUALIDAD);
                                        break;
                                }

                                TablaContrataClase.child(Id_).setValue(Dato).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Progreso.dismiss();
                                            Toast.makeText(ProporcionaTarjeta.this, "Se completo el pago", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(ProporcionaTarjeta.this, MenuCliente.class));
                                            finish();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Progreso.dismiss();
                                        Toast.makeText(ProporcionaTarjeta.this, "Ha ocurrido un error inesperado", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                        }
                    }
                }).setNegativeButton("Cancelar", null)
                .create().show();
    }

    private Boolean verificarInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
}