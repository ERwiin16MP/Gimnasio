package com.pedba.gimnasiovalhalla;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
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
import com.pedba.gimnasiovalhalla.Modelos.Clase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FormularioClase extends AppCompatActivity {

    private static final String CLASES = "Clases";
    private static final String ID_CLASS = "Id_class";
    private static final String NOM_CLASS = "Nom_class";
    private static final String ENTRENADOR = "Entrenador";
    private static final String HORA = "Hora";
    private static final String LUNES_CLASS = "Lun_Class";
    private static final String MARTES_CLASS = "Martes_Class";
    private static final String MIERCOLES_CLASS = "Mierc_Class";
    private static final String JUEVES_CLASS = "Jueves_Class";
    private static final String VIERNES_CLASS = "Viernes_Class";
    private static final String SABADO_CLASS = "Sabado_Class";
    private final DatabaseReference TablaClases = FirebaseDatabase.getInstance().getReference(CLASES);
    int Hora, Minutos;
    String HoraSeleccionada = "";
    ProgressDialog Progreso;
    ArrayList<Clase> Clases;
    private ListView ListaDeClases;
    private EditText TextBox_NombreDeLaClase, TextBox_EntrenadorAsignado;
    private CheckBox Lunes, Martes, Miercoles, Jueves, Viernes, Sabado;
    private Button Boton, Boton_Horario;
    private int Indice = -1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formulario_clase);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        inicializarVistas();
        setMetodoDelBoton(getIntent().getExtras().getInt("Opcion"));

        Boton_Horario.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    FormularioClase.this,
                    (view, hourOfDay, minute) -> {
                        Hora = hourOfDay;
                        Minutos = minute;
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(0, 0, 0, Hora, Minutos);
                        HoraSeleccionada = String.valueOf(DateFormat.format("hh:mm aa", calendar));
                        Boton_Horario.setText(HoraSeleccionada);
                    }, 12, 0, false);
            timePickerDialog.updateTime(Hora, Minutos);
            timePickerDialog.show();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setMetodoDelBoton(int opcion) {
        switch (opcion) {
            case 0:
                Boton.setBackgroundResource(R.drawable.boton_redondo_amarillo);
                Boton.setText(getString(R.string.RegistrarClase));
                Boton.setOnClickListener(v -> registrar());
                ListaDeClases.setVisibility(View.GONE);
                break;
            case 1:
                Boton.setBackgroundResource(R.drawable.boton_redondo_rojo);
                Boton.setText(getString(R.string.EliminarClase));
                Boton.setTextColor(getColor(R.color.white));
                Boton.setOnClickListener(v -> eliminar());
                setListView();
                ocultarLayouts();
                break;
            case 2:
                Boton.setBackgroundResource(R.drawable.boton_redondo_amarillo);
                Boton.setText(getString(R.string.ModificarClase));
                Boton.setOnClickListener(v -> modificar());
                setListView();
                break;
            case 3:
                Boton.setVisibility(View.GONE);
                setListView();
                ocultarLayouts();
                break;
        }
    }

    private void ocultarLayouts() {
        TextBox_EntrenadorAsignado.setEnabled(false);
        TextBox_NombreDeLaClase.setEnabled(false);
        Lunes.setEnabled(false);
        Martes.setEnabled(false);
        Miercoles.setEnabled(false);
        Jueves.setEnabled(false);
        Viernes.setEnabled(false);
        Sabado.setEnabled(false);
        Boton_Horario.setEnabled(false);
    }

    private void modificar() {
        if (!verificarInternet())
            Toast.makeText(this, R.string.NoHayInternet, Toast.LENGTH_SHORT).show();
        else {
            String NombreDeLaClase = TextBox_NombreDeLaClase.getText().toString(),
                    EntrenadorAsignado = TextBox_EntrenadorAsignado.getText().toString();
            if (NombreDeLaClase.isEmpty()) {
                Snackbar.make(findViewById(android.R.id.content), "Ingrese el nombre de la clase", Snackbar.LENGTH_SHORT).show();
                TextBox_NombreDeLaClase.setError(getString(R.string.CampoRequerido));
                TextBox_NombreDeLaClase.requestFocus();
            } else if (EntrenadorAsignado.isEmpty()) {
                Snackbar.make(findViewById(android.R.id.content), "Ingrese el nombre del entrenador", Snackbar.LENGTH_SHORT).show();
                TextBox_EntrenadorAsignado.setError(getString(R.string.CampoRequerido));
                TextBox_EntrenadorAsignado.requestFocus();
            } else if (!Lunes.isChecked() && !Martes.isChecked() && !Miercoles.isChecked()
                    && !Jueves.isChecked() && !Viernes.isChecked() && !Sabado.isChecked())
                Snackbar.make(findViewById(android.R.id.content), R.string.SeleccionePorLoMenosUnDia, Snackbar.LENGTH_SHORT).show();
            else if (HoraSeleccionada.equals(""))
                Snackbar.make(findViewById(android.R.id.content), R.string.DebeDeSeleccionarUnaHora, Snackbar.LENGTH_SHORT).show();
            else modificarClase(NombreDeLaClase, EntrenadorAsignado);
        }
    }

    private void modificarClase(String nombreDeLaClase, String entrenadorAsignado) {
        Progreso = new ProgressDialog(FormularioClase.this);
        Progreso.setTitle(getString(R.string.ProcesoDeRegistro));
        Progreso.setMessage(getString(R.string.EspereUnMomento));
        Progreso.setCanceledOnTouchOutside(false);
        Progreso.show();

        String Id = Clases.get(Indice).getId();
        Map<String, Object> Datos = new HashMap<>();
        Datos.put(ID_CLASS, Id);
        Datos.put(NOM_CLASS, nombreDeLaClase);
        Datos.put(ENTRENADOR, entrenadorAsignado);
        Datos.put(HORA, HoraSeleccionada);
        Datos.put(LUNES_CLASS, Lunes.isChecked());
        Datos.put(MARTES_CLASS, Martes.isChecked());
        Datos.put(MIERCOLES_CLASS, Miercoles.isChecked());
        Datos.put(JUEVES_CLASS, Jueves.isChecked());
        Datos.put(VIERNES_CLASS, Viernes.isChecked());
        Datos.put(SABADO_CLASS, Sabado.isChecked());

        TablaClases.child(Id).setValue(Datos).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(FormularioClase.this, "Clase modificada exitosamente", Toast.LENGTH_SHORT).show();
                Progreso.dismiss();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(FormularioClase.this, "Ha ocurrido un error al modificar la clase", Toast.LENGTH_SHORT).show();
            Progreso.dismiss();
        });
    }

    private void setListView() {
        Progreso = new ProgressDialog(FormularioClase.this);
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
                    ListaDeClases.setAdapter(new AdaptadorClases());
                    ListaDeClases.setOnItemClickListener((parent, view, position, id) -> {
                        TextBox_NombreDeLaClase.setText(Clases.get(position).getNombreDeLaClase());
                        TextBox_EntrenadorAsignado.setText(Clases.get(position).getEntrenador());
                        Boton_Horario.setText(Clases.get(position).getHora());
                        Lunes.setChecked(Boolean.parseBoolean(Clases.get(position).getLunes()));
                        Martes.setChecked(Boolean.parseBoolean(Clases.get(position).getMartes()));
                        Miercoles.setChecked(Boolean.parseBoolean(Clases.get(position).getMiercoles()));
                        Jueves.setChecked(Boolean.parseBoolean(Clases.get(position).getJueves()));
                        Viernes.setChecked(Boolean.parseBoolean(Clases.get(position).getViernes()));
                        Sabado.setChecked(Boolean.parseBoolean(Clases.get(position).getSabado()));
                        HoraSeleccionada = Clases.get(position).getHora();
                        Indice = position;
                    });
                    Progreso.dismiss();
                } else
                    Toast.makeText(FormularioClase.this, "No hay clases :/", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void eliminar() {
        if (Indice > -1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(FormularioClase.this);
            builder.setMessage("¿Estas seguro de eliminar la clase \"" + Clases.get(Indice).getNombreDeLaClase() + "\"?")
                    .setTitle("Eliminar clase").setPositiveButton("Vale", (dialog, which) -> TablaClases.child(Clases.get(Indice).getId()).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(FormularioClase.this, "Se elimino la clase exitosamente", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }).addOnFailureListener(e -> Toast.makeText(FormularioClase.this, R.string.HaOcurridoUnError, Toast.LENGTH_SHORT).show())).setNegativeButton("No", null).show();
        } else
            Toast.makeText(this, "Debe de seleccionar una clase", Toast.LENGTH_SHORT).show();
    }

    private void registrar() {
        if (!verificarInternet())
            Toast.makeText(this, R.string.NoHayInternet, Toast.LENGTH_SHORT).show();
        else {
            String NombreDeLaClase = TextBox_NombreDeLaClase.getText().toString(),
                    EntrenadorAsignado = TextBox_EntrenadorAsignado.getText().toString();
            if (NombreDeLaClase.isEmpty()) {
                Snackbar.make(findViewById(android.R.id.content), "Ingrese el nombre de la clase", Snackbar.LENGTH_SHORT).show();
                TextBox_NombreDeLaClase.setError(getString(R.string.CampoRequerido));
                TextBox_NombreDeLaClase.requestFocus();
            } else if (EntrenadorAsignado.isEmpty()) {
                Snackbar.make(findViewById(android.R.id.content), "Ingrese el nombre del entrenador", Snackbar.LENGTH_SHORT).show();
                TextBox_EntrenadorAsignado.setError(getString(R.string.CampoRequerido));
                TextBox_EntrenadorAsignado.requestFocus();
            } else if (!Lunes.isChecked() && !Martes.isChecked() && !Miercoles.isChecked()
                    && !Jueves.isChecked() && !Viernes.isChecked() && !Sabado.isChecked())
                Snackbar.make(findViewById(android.R.id.content), R.string.SeleccionePorLoMenosUnDia, Snackbar.LENGTH_SHORT).show();
            else if (HoraSeleccionada.equals(""))
                Snackbar.make(findViewById(android.R.id.content), R.string.DebeDeSeleccionarUnaHora, Snackbar.LENGTH_SHORT).show();
            else registrarClase(NombreDeLaClase, EntrenadorAsignado);
        }
    }

    private void registrarClase(String nombreDeLaClase, String entrenadorAsignado) {
        Progreso = new ProgressDialog(FormularioClase.this);
        Progreso.setTitle(getString(R.string.ProcesoDeRegistro));
        Progreso.setMessage(getString(R.string.EspereUnMomento));
        Progreso.setCanceledOnTouchOutside(false);
        Progreso.show();

        String Id = TablaClases.push().getKey();
        Map<String, Object> Datos = new HashMap<>();
        Datos.put(ID_CLASS, Id);
        Datos.put(NOM_CLASS, nombreDeLaClase);
        Datos.put(ENTRENADOR, entrenadorAsignado);
        Datos.put(HORA, HoraSeleccionada);
        Datos.put(LUNES_CLASS, Lunes.isChecked());
        Datos.put(MARTES_CLASS, Martes.isChecked());
        Datos.put(MIERCOLES_CLASS, Miercoles.isChecked());
        Datos.put(JUEVES_CLASS, Jueves.isChecked());
        Datos.put(VIERNES_CLASS, Viernes.isChecked());
        Datos.put(SABADO_CLASS, Sabado.isChecked());

        TablaClases.child(Id).setValue(Datos).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(FormularioClase.this, R.string.ClaseRegistrada, Toast.LENGTH_SHORT).show();
                Progreso.dismiss();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(FormularioClase.this, R.string.HaOcurridoUnErrorAlRegistrarLaClase, Toast.LENGTH_SHORT).show();
            Progreso.dismiss();
        });
    }

    private void inicializarVistas() {
        Boton_Horario = findViewById(R.id.Button_Hora);
        Boton = findViewById(R.id.Boton_FormularioClases);
        ListaDeClases = findViewById(R.id.ListView_ListaClases);
        TextBox_NombreDeLaClase = findViewById(R.id.EditText_NombreDeLaClaseFormulario);
        TextBox_EntrenadorAsignado = findViewById(R.id.EditText_EntrenadorAsignadoFormulario);
        Lunes = findViewById(R.id.CheckBox_Lunes);
        Martes = findViewById(R.id.CheckBox_Martes);
        Miercoles = findViewById(R.id.CheckBox_Miercoles);
        Jueves = findViewById(R.id.CheckBox_Jueves);
        Viernes = findViewById(R.id.CheckBox_Viernes);
        Sabado = findViewById(R.id.CheckBox_Sabado);
    }

    private Boolean verificarInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class AdaptadorClases extends BaseAdapter {

        private final LayoutInflater inflater;

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

        @SuppressLint({"ViewHolder", "InflateParams"})
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflater.inflate(R.layout.item, null);
            TextView label_Nombre = view.findViewById(R.id.TextView_Item);
            label_Nombre.setText(Clases.get(i).getNombreDeLaClase());
            return view;
        }
    }
}