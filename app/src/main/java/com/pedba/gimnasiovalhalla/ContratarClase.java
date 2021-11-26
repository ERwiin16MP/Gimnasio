package com.pedba.gimnasiovalhalla;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pedba.gimnasiovalhalla.Modelos.Clase;

import java.util.ArrayList;

public class ContratarClase extends AppCompatActivity {

    public static final String CLASES = "Clases";
    public static final String ID_CLASS = "Id_class";
    public static final String NOM_CLASS = "Nom_class";
    public static final String ENTRENADOR = "Entrenador";
    public static final String HORA = "Hora";
    public static final String LUNES_CLASS = "Lun_Class";
    public static final String MARTES_CLASS = "Martes_Class";
    public static final String MIERCOLES_CLASS = "Mierc_Class";
    public static final String JUEVES_CLASS = "Jueves_Class";
    public static final String VIERNES_CLASS = "Viernes_Class";
    public static final String SABADO_CLASS = "Sabado_Class";
    public static final String CLASE = "Clase";
    public static final String ACCION = "Accion";
    public static ArrayList<Clase> Clases;
    private ProgressDialog Progreso;
    private ListView ListaClases;
    private DatabaseReference TablaClases = FirebaseDatabase.getInstance().getReference(CLASES);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contratar_clase);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ListaClases = findViewById(R.id.ListView_ListaClasesContratar);

        if (!verificarInternet())
            Toast.makeText(this, R.string.NoHayInternet, Toast.LENGTH_SHORT).show();
        else llenarLista();
    }

    private void llenarLista() {
        Progreso = new ProgressDialog(ContratarClase.this);
        Progreso.setTitle(getString(R.string.DescargandoInformacion));
        Progreso.setMessage(getString(R.string.EspereUnMomento));
        Progreso.setCanceledOnTouchOutside(false);
        Progreso.show();
        TablaClases.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Clases = new ArrayList<>();
                    String Id, NombreDeLaClase, Entrenador, Hora, Lunes_, Martes_, Miercoles_, Jueves_, Viernes_, Sabado_;
                    for (DataSnapshot i : snapshot.getChildren()) {
                        Id = i.child(ID_CLASS).getValue().toString();
                        NombreDeLaClase = i.child(NOM_CLASS).getValue().toString();
                        Entrenador = i.child(ENTRENADOR).getValue().toString();
                        Hora = i.child(HORA).getValue().toString();
                        Lunes_ = i.child(LUNES_CLASS).getValue().toString();
                        Martes_ = i.child(MARTES_CLASS).getValue().toString();
                        Miercoles_ = i.child(MIERCOLES_CLASS).getValue().toString();
                        Jueves_ = i.child(JUEVES_CLASS).getValue().toString();
                        Viernes_ = i.child(VIERNES_CLASS).getValue().toString();
                        Sabado_ = i.child(SABADO_CLASS).getValue().toString();
                        Clases.add(new Clase(Id, NombreDeLaClase, Entrenador, Hora, Lunes_, Martes_, Miercoles_, Jueves_, Viernes_, Sabado_));
                    }
                    ListaClases.setAdapter(new AdaptadorClases());
                    ListaClases.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ContratarClase.this);
                            builder.setTitle("Contratar clase")
                                    .setMessage("Usted contratara la siguiente clase: \n" + Clases.get(position).getNombreDeLaClase())
                                    .setPositiveButton("Vale", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startActivity(new Intent(ContratarClase.this, EligePlanDePago.class)
                                                    .putExtra(ACCION, CLASE)
                                                    .putExtra(CLASE, Clases.get(position).getNombreDeLaClase()));
                                            finish();
                                        }
                                    }).setNegativeButton("No", null)
                                    .create().show();
                        }
                    });
                    Progreso.dismiss();
                } else {
                    Toast.makeText(ContratarClase.this, "No hay clases :/ \nRegrese pronto :)", Toast.LENGTH_LONG).show();
                    Progreso.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    public class AdaptadorClases extends BaseAdapter {

        private LayoutInflater inflater;
        private TextView Label_Nombre, Label_Entrenador, Label_Dias, Label_Hora;
        private RelativeLayout Layout;

        public AdaptadorClases() {
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return Clases.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflater.inflate(R.layout.item_clase, null);
            Label_Nombre = view.findViewById(R.id.TextView_NombreDeLaClase);
            Label_Entrenador = view.findViewById(R.id.TextView_Entrenador);
            Label_Dias = view.findViewById(R.id.TextView_DiasDisponibles);
            Label_Hora = view.findViewById(R.id.TextView_Hora);
            Layout = view.findViewById(R.id.Referencia_Clase);

            Label_Nombre.setText("Nombre de la clase: " + Clases.get(i).getNombreDeLaClase());
            Label_Entrenador.setText("Entrenador asignado: " + Clases.get(i).getEntrenador());
            String Dias = "Días: ";

            if (Clases.get(i).getLunes().equals("true"))
                Dias += "Lunes, ";
            if (Clases.get(i).getMartes().equals("true"))
                Dias += "Martes, ";
            if (Clases.get(i).getMiercoles().equals("true"))
                Dias += "Miercoles, ";
            if (Clases.get(i).getJueves().equals("true"))
                Dias += "Jueves, ";
            if (Clases.get(i).getViernes().equals("true"))
                Dias += "Viernes, ";
            if (Clases.get(i).getSabado().equals("true"))
                Dias += "Sabado, ";

            Dias = Dias.substring(0, Dias.length() - 1);
            Dias = Dias.substring(0, Dias.length() - 1);

            Label_Dias.setText(Dias);
            Label_Hora.setText("Hora: " + Clases.get(i).getHora());
            return view;
        }
    }
}