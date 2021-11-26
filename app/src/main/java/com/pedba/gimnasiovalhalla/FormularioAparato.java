package com.pedba.gimnasiovalhalla;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pedba.gimnasiovalhalla.Modelos.Aparato;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FormularioAparato extends AppCompatActivity {

    public static final String APARATOS = "Aparatos";
    public static final String ID_APARATO = "Id_aparato";
    public static final String NOM_APARATO = "Nom_aparato";
    public static final String EXISTENCIAS = "Existencias";
    public static final String AREA = "Area";
    public static final String AREAS = "Areas";
    public static final String NOM_AREA = "nom_area";
    private static ArrayList<Aparato> Aparatos;
    private final DatabaseReference TablaAparatos = FirebaseDatabase.getInstance().getReference(APARATOS);
    private final DatabaseReference TablaAreas = FirebaseDatabase.getInstance().getReference(AREAS);
    private Button Boton;
    private Spinner Areas;
    private ListView ListaAparatos;
    private EditText TextBox_Descripcion, TextBox_Cantidad;
    private ProgressDialog Progreso;
    private int Indice = -1, Contador = 0;
    private String Areas_Array[];
    private ArrayAdapter<String> adapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formulario_aparato);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        inicializarVistas();
        setMetodoDelBoton(getIntent().getExtras().getInt("Opcion"));
        setSpinner();
    }

    private void setSpinner() {
        TablaAreas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
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
                    adapter = new ArrayAdapter<String>(FormularioAparato.this, android.R.layout.simple_spinner_dropdown_item, list);
                    Areas.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setMetodoDelBoton(int opcion) {
        switch (opcion) {
            case 0:
                Boton.setBackgroundResource(R.drawable.boton_redondo_amarillo);
                Boton.setText(getString(R.string.RegistrarAparato));
                Boton.setOnClickListener(v -> registrar());
                ListaAparatos.setVisibility(View.GONE);
                break;
            case 1:
                Boton.setBackgroundResource(R.drawable.boton_redondo_rojo);
                Boton.setText(getString(R.string.EliminarAparato));
                Boton.setTextColor(getColor(R.color.white));
                Boton.setOnClickListener(v -> eliminar());
                TextBox_Descripcion.setEnabled(false);
                TextBox_Cantidad.setEnabled(false);
                Areas.setEnabled(false);
                setListView();
                break;
            case 2:
                Boton.setBackgroundResource(R.drawable.boton_redondo_amarillo);
                Boton.setText(getString(R.string.ModificarAparato));
                Boton.setOnClickListener(v -> modificar());
                setListView();
                break;
            case 3:
                Boton.setVisibility(View.GONE);
                TextBox_Descripcion.setEnabled(false);
                TextBox_Cantidad.setEnabled(false);
                Areas.setEnabled(false);
                setListView();
                break;
        }
    }

    private void setListView() {
        if (!verificarInternet())
            Toast.makeText(this, R.string.NoHayInternet, Toast.LENGTH_LONG).show();
        else {
            Progreso = new ProgressDialog(FormularioAparato.this);
            Progreso.setTitle(getString(R.string.DescargandoInformacion));
            Progreso.setMessage(getString(R.string.EspereUnMomento));
            Progreso.setCanceledOnTouchOutside(false);
            Progreso.show();
            descargarInformacion();
        }
    }

    private void descargarInformacion() {
        TablaAparatos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Aparatos = new ArrayList<>();
                    String Id, Descripcion, Cantidad, Area;
                    for (DataSnapshot i : snapshot.getChildren()) {
                        Id = i.child(ID_APARATO).getValue().toString();
                        Descripcion = i.child(NOM_APARATO).getValue().toString();
                        Cantidad = i.child(EXISTENCIAS).getValue().toString();
                        Area = i.child(AREA).getValue().toString();
                        Aparatos.add(new Aparato(Id, Descripcion, Cantidad, Area));
                    }
                    ListaAparatos.setAdapter(new AdaptadorAparatos());
                    ListaAparatos.setOnItemClickListener((parent, view, position, id) -> {
                        TextBox_Descripcion.setText(Aparatos.get(position).getDescripcion());
                        TextBox_Cantidad.setText(Aparatos.get(position).getCantidad());
                        switch (Aparatos.get(position).getArea()) {
                            case "Tren superior":
                                Areas.setSelection(0);
                                break;
                            case "Tren inferior":
                                Areas.setSelection(1);
                                break;
                            case "Cardio":
                                Areas.setSelection(2);
                        }
                        Indice = position;
                    });
                    Progreso.dismiss();
                } else
                    Toast.makeText(FormularioAparato.this, "No hay aparatos registrados", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void modificar() {
        if (!verificarInternet())
            Toast.makeText(this, R.string.NoHayInternet, Toast.LENGTH_SHORT).show();
        else {
            String Descripcion = TextBox_Descripcion.getText().toString(), Cantidad = TextBox_Cantidad.getText().toString();
            if (Descripcion.isEmpty()) {
                Snackbar.make(findViewById(android.R.id.content), "Ingrese la descripción", Snackbar.LENGTH_SHORT).show();
                TextBox_Descripcion.setError(getString(R.string.CampoRequerido));
                TextBox_Descripcion.requestFocus();
            } else if (Cantidad.isEmpty()) {
                Snackbar.make(findViewById(android.R.id.content), "Ingrese la cantidad", Snackbar.LENGTH_SHORT).show();
                TextBox_Cantidad.setError(getString(R.string.CampoRequerido));
                TextBox_Cantidad.requestFocus();
            } else
                modificarAparato(Descripcion, Cantidad);
        }
    }

    private void modificarAparato(String descripcion, String cantidad) {
        Progreso = new ProgressDialog(FormularioAparato.this);
        Progreso.setTitle(getString(R.string.ProcesoDeRegistro));
        Progreso.setMessage(getString(R.string.EspereUnMomento));
        Progreso.setCanceledOnTouchOutside(false);
        Progreso.show();

        String Id = Aparatos.get(Indice).getId();
        Map<String, Object> Datos = new HashMap<>();
        Datos.put(ID_APARATO, Id);
        Datos.put(NOM_APARATO, descripcion);
        Datos.put(EXISTENCIAS, cantidad);
        Datos.put(AREA, Areas.getSelectedItem().toString());

        TablaAparatos.child(Id).setValue(Datos).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(FormularioAparato.this, "Aparato modificado correctamente", Toast.LENGTH_SHORT).show();
                Progreso.dismiss();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(FormularioAparato.this, "Ha ocurrido un error al modificar el aparato", Toast.LENGTH_SHORT).show();
            Progreso.dismiss();
        });
    }

    private void eliminar() {
        if (Indice > -1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(FormularioAparato.this);
            builder.setMessage("¿Estas seguro de eliminar el aparato \"" + Aparatos.get(Indice).getDescripcion() + "\"?")
                    .setTitle("Eliminar Aparato").setPositiveButton("Vale", (dialog, which) -> TablaAparatos.child(Aparatos.get(Indice).getId()).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(FormularioAparato.this, "Se elimino el aparato exitosamente", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }).addOnFailureListener(e -> Toast.makeText(FormularioAparato.this, R.string.HaOcurridoUnError, Toast.LENGTH_SHORT).show())).setNegativeButton("No", null).show();
        } else
            Toast.makeText(this, "Debe de seleccionar una aparato", Toast.LENGTH_SHORT).show();
    }

    private void registrar() {
        if (!verificarInternet())
            Toast.makeText(FormularioAparato.this, R.string.NoHayInternet, Toast.LENGTH_SHORT).show();
        else {
            String Descripcion = TextBox_Descripcion.getText().toString(), Cantidad = TextBox_Cantidad.getText().toString();
            if (Descripcion.isEmpty()) {
                Snackbar.make(findViewById(android.R.id.content), "Ingrese la descripción", Snackbar.LENGTH_SHORT).show();
                TextBox_Descripcion.setError(getString(R.string.CampoRequerido));
                TextBox_Descripcion.requestFocus();
            } else if (Cantidad.isEmpty()) {
                Snackbar.make(findViewById(android.R.id.content), "Ingrese la cantidad", Snackbar.LENGTH_SHORT).show();
                TextBox_Cantidad.setError(getString(R.string.CampoRequerido));
                TextBox_Cantidad.requestFocus();
            } else
                registrarAparato(Descripcion, Cantidad);
        }
    }

    private void registrarAparato(String descripcion, String cantidad) {
        Progreso = new ProgressDialog(this);
        Progreso.setTitle(getString(R.string.ProcesoDeRegistro));
        Progreso.setMessage(getString(R.string.EspereUnMomento));
        Progreso.setCanceledOnTouchOutside(false);
        Progreso.show();

        String Id = TablaAparatos.push().getKey();
        Map<String, Object> Datos = new HashMap<>();
        Datos.put(ID_APARATO, Id);
        Datos.put(NOM_APARATO, descripcion);
        Datos.put(EXISTENCIAS, cantidad);
        Datos.put(AREA, Areas.getSelectedItem().toString());

        TablaAparatos.child(Id).setValue(Datos).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(FormularioAparato.this, "Aparato registrado correctamente", Toast.LENGTH_SHORT).show();
                Progreso.dismiss();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(FormularioAparato.this, "Ha ocurrido un error al registrar el aparato", Toast.LENGTH_SHORT).show();
            Progreso.dismiss();
        });
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
        Boton = findViewById(R.id.Boton_FormularioAparatos);
        ListaAparatos = findViewById(R.id.ListView_ListaAparatos);
        TextBox_Descripcion = findViewById(R.id.EditText_DescripcionAparatoFormulario);
        TextBox_Cantidad = findViewById(R.id.EditText_CantidadAparatosFormulario);
        Areas = findViewById(R.id.Spinner_Areas);
    }

    public class AdaptadorAparatos extends BaseAdapter {

        private final LayoutInflater inflater;

        public AdaptadorAparatos() {
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return Aparatos.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint({"ViewHolder", "InflateParams"})
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflater.inflate(R.layout.item, null);
            TextView label_Nombre = view.findViewById(R.id.TextView_Item);
            label_Nombre.setText(Aparatos.get(i).getDescripcion());
            return view;
        }
    }
}