// Equipo Dinamita 4
//
// Gimnasio Valhalla
// Objeto: clase
// Autores:
// Chavarria Ramirez Brenda Lizbeth
// Grande Mendoza Diana Axelle
// Martinez Perez Erwin
// Morales Belman Alan Ouseiri
// Vargas Alegria Paloma Guadalupe
//
// Fecha Creación: 25 - Octubre - 2021
//
// Instituto Tecnologico de México Campus San Juan del Rio
//
// Historial de modificación
// Version Modificación  Fecha           Cambio                                         Razón
// 1.0     Erwin M.      30 - 10 - 2021  Aumento de tamaño de Edit Text                 Mejora en diseño de la interfaz
// 1.0     Erwin M.      30 - 10 - 2021  Disminucion tamaño del Logo                    Mejora en diseño de la interfaz
// 1.0     Erwin M.      30 - 10 - 2021  Cambio color del boton "Registrarse"           Mejora en diseño de la interfaz
// 1.0.1   Brenda C.     01 - 11 - 2021  Adición de iconos en el menu principal         Mejora en diseño de la interfaz
// 1.0.1   Brenda C.     01 - 11 - 2021  Cambio de colores en el menu principal         Mejora en diseño de la interfaz
// 1.1     Paloma V.     02 - 11 - 2021  Habilitación interfaz de registro              Implementación de la aplicación
// 1.1     Alan M.       02 - 11 - 2021  Habilitacion interfaz de login cliente         Implementación de la aplicación
// 1.1     Diana G.      03 - 11 - 2021  Habilitacion interfaz de login personal        Implementación de la aplicación
// 1.1     Erwin M.      03 - 11 - 2021  Habilitacion interfaz de gestion de aparatos   Implementación de la aplicación

package com.pedba.gimnasiovalhalla;

import static com.pedba.gimnasiovalhalla.Registrarse.CLIENTES;
import static com.pedba.gimnasiovalhalla.Registrarse.CORREO_CLIENTE;
import static com.pedba.gimnasiovalhalla.Registrarse.FOTO_CLIENTE;
import static com.pedba.gimnasiovalhalla.Registrarse.ID_CLIENTE;
import static com.pedba.gimnasiovalhalla.Registrarse.NOM_CLIENTE;
import static com.pedba.gimnasiovalhalla.Registrarse.PASS_CLIENTE;
import static com.pedba.gimnasiovalhalla.Registrarse.TEL_CLIENTE;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pedba.gimnasiovalhalla.Modelos.Cliente;

import java.util.ArrayList;

public class Login extends AppCompatActivity {

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference BaseDeDatosClientes = database.getReference(CLIENTES);
    public static ArrayList<Cliente> Clientes;
    private EditText TextBox_Correo;
    private EditText TextBox_Contraseña;
    public static String Id, Nombre, Correo, Foto, Telefono, Contraseña;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        inicializarVistas();
        Id = "Erwin";
        startActivity(new Intent(this, MenuCliente.class));
    }

    public void iniciarSesion(View view) {
        String Correo = TextBox_Correo.getText().toString(), Contraseña = TextBox_Contraseña.getText().toString();

        if (!verificarInternet()) {
            Toast.makeText(this, R.string.NoHayInternet, Toast.LENGTH_SHORT).show();
        } else if (Correo.isEmpty()) {
            Snackbar.make(view, R.string.UsuarioVacio, Snackbar.LENGTH_LONG).show();
            TextBox_Correo.setError(getString(R.string.CampoRequerido));
            TextBox_Correo.requestFocus();
        } else if (Contraseña.isEmpty()) {
            Snackbar.make(view, R.string.ContraseñaVacia, Snackbar.LENGTH_LONG).show();
            TextBox_Contraseña.setError(getString(R.string.CampoRequerido));
            TextBox_Contraseña.requestFocus();
        } else if (Contraseña.length() < 7) {
            Toast.makeText(this, R.string.LaClaveDebe, Toast.LENGTH_SHORT).show();
        } else {
            login(Correo, Contraseña);
        }
    }

    private void login(String correo, String contraseña) {
        ProgressDialog Progreso = new ProgressDialog(this);
        Progreso.setTitle(R.string.IniciandoSesion);
        Progreso.setMessage(getString(R.string.EspereUnMomento));
        Progreso.setCanceledOnTouchOutside(false);
        Progreso.show();

        BaseDeDatosClientes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Clientes = new ArrayList<>();
                    for (DataSnapshot i : snapshot.getChildren()) {
                        Id = i.child(ID_CLIENTE).getValue().toString();
                        Nombre = i.child(NOM_CLIENTE).getValue().toString();
                        Correo = i.child(CORREO_CLIENTE).getValue().toString();
                        Foto = i.child(FOTO_CLIENTE).getValue().toString();
                        Telefono = i.child(TEL_CLIENTE).getValue().toString();
                        Contraseña = i.child(PASS_CLIENTE).getValue().toString();
                        Clientes.add(new Cliente(Id, Nombre, Correo, Foto, Telefono, Contraseña));
                    }
                    for (int i = 0; i < Clientes.size(); i++)
                        if (correo.equals(Clientes.get(i).getCorreo())) {
                            if (contraseña.equals(Clientes.get(i).getContraseña())) {
                                if (correo.equals("root"))
                                    startActivity(new Intent(Login.this, MenuPersonal.class));
                                else {
                                    Id = Clientes.get(i).getId();
                                    startActivity(new Intent(Login.this, MenuCliente.class));
                                }
                                finish();
                                break;
                            } else {
                                Toast.makeText(Login.this, R.string.ContraseñaIncorrecta, Toast.LENGTH_SHORT).show();
                                TextBox_Contraseña.requestFocus();
                            }
                        }
                    Progreso.dismiss();
                } else {
                    Progreso.dismiss();
                    Toast.makeText(Login.this, "No hay clientes", Toast.LENGTH_SHORT).show();
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
        return networkInfo != null && networkInfo.isConnected();
    }

    private void inicializarVistas() {
        TextBox_Correo = findViewById(R.id.EditText_Correo_Cliente);
        TextBox_Contraseña = findViewById(R.id.EditText_Contraseña_Cliente);
    }

    public void registrarse(View view) {
        startActivity(new Intent(this, Registrarse.class));
    }
}