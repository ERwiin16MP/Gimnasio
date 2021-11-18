package com.pedba.gimnasiovalhalla;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;

public class Registrarse extends AppCompatActivity {

    public static final String CLIENTES = "Clientes";
    public static final String ID_CLIENTE = "Id_cliente";
    public static final String NOM_CLIENTE = "Nom_cliente";
    public static final String CORREO_CLIENTE = "Correo_cliente";
    public static final String TEL_CLIENTE = "Tel_cliente";
    public static final String FOTO_CLIENTE = "Foto_cliente";
    public static final String PASS_CLIENTE = "Pass_cliente";
    public static final int CODIGO_IMAGEN = 0;
    private EditText TextBox_NombreCompleto;
    private EditText TextBox_Correo;
    private EditText TextBox_Telefono;
    private EditText TextBox_Contraseña;
    private EditText TextBox_ContraseñaConfirmacion;
    private ImageView Boton_SubirImagen;
    private Uri ImagenUri;
    private StorageReference storageReference;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference BaseDeDatosClientes = database.getReference(CLIENTES);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrarse);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        inicializarVistas();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    private void inicializarVistas() {
        TextBox_NombreCompleto = findViewById(R.id.EditText_Nombre_Registrarse);
        TextBox_Telefono = findViewById(R.id.EditText_Telefono_Registrarse);
        TextBox_Correo = findViewById(R.id.EditText_Usuario_Registrarse);
        TextBox_Contraseña = findViewById(R.id.EditText_Contraseña_Registrarse);
        TextBox_ContraseñaConfirmacion = findViewById(R.id.EditText_Contraseña_Registrarse_Confirmacion);
        Boton_SubirImagen = findViewById(R.id.ImagenCliente_Registrarse);
    }

    public void subirImagen(View view) {
        Intent intent = new Intent();
        intent.setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, CODIGO_IMAGEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CODIGO_IMAGEN && resultCode == RESULT_OK && data != null && data.getData() != null) {
            ImagenUri = data.getData();
            Boton_SubirImagen.setImageURI(ImagenUri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void registrar(View view) {
        String NombreCompleto = TextBox_NombreCompleto.getText().toString();
        String Correo = TextBox_Correo.getText().toString();
        String Telefono = TextBox_Telefono.getText().toString();
        String Contraseña = TextBox_Contraseña.getText().toString();
        String ContraseñaConfirmacion = TextBox_ContraseñaConfirmacion.getText().toString();

        if (!verificarInternet()) {
            Toast.makeText(this, R.string.NoHayInternet, Toast.LENGTH_SHORT).show();
        } else if (NombreCompleto.isEmpty()) {
            Snackbar.make(view, R.string.IngreseSuNombre, Snackbar.LENGTH_LONG).show();
            TextBox_NombreCompleto.setError(getString(R.string.CampoRequerido));
            TextBox_NombreCompleto.requestFocus();
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
        } else if (ContraseñaConfirmacion.isEmpty()) {
            Snackbar.make(view, R.string.ContraseñaDeConfirmacionVacia, Snackbar.LENGTH_LONG).show();
            TextBox_ContraseñaConfirmacion.setError(getString(R.string.CampoRequerido));
            TextBox_ContraseñaConfirmacion.requestFocus();
        } else if (!Contraseña.equals(ContraseñaConfirmacion)) {
            Snackbar.make(view, R.string.ContraseñasDiferentes, Snackbar.LENGTH_LONG).show();
            TextBox_ContraseñaConfirmacion.requestFocus();
        } else if (ImagenUri == null)
            Snackbar.make(view, "Seleccione una imagen de usuario", Snackbar.LENGTH_LONG).show();
        else registrarUsuario(NombreCompleto, Telefono, Correo, Contraseña);
    }

    private void registrarUsuario(String NombreCompleto, String Telefono, String Correo, String Contraseña) {
        ProgressDialog Progreso = new ProgressDialog(this);
        Progreso.setTitle(getString(R.string.ProcesoDeRegistro));
        Progreso.setMessage(getString(R.string.EspereUnMomento));
        Progreso.setCanceledOnTouchOutside(false);
        Progreso.show();

        StorageReference riversRef = storageReference.child(CLIENTES + "/" + Correo);
        riversRef.putFile(ImagenUri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            Uri dowloadUri = uriTask.getResult();
            String Id = BaseDeDatosClientes.push().getKey();
            Map<String, Object> Datos = new HashMap<>();
            Datos.put(ID_CLIENTE, Id);
            Datos.put(NOM_CLIENTE, NombreCompleto);
            Datos.put(CORREO_CLIENTE, Correo);
            Datos.put(TEL_CLIENTE, Telefono);
            Datos.put(PASS_CLIENTE, Contraseña);
            Datos.put(FOTO_CLIENTE, dowloadUri.toString());
            BaseDeDatosClientes.child(Id).setValue(Datos).addOnCompleteListener(task -> {
                Toast.makeText(Registrarse.this, R.string.RegistroUsuarioExitoso, Toast.LENGTH_SHORT).show();
                Progreso.dismiss();
                finish();
            }).addOnFailureListener(e -> Toast.makeText(Registrarse.this, R.string.HaOcurridoUnErrorAlRegistrarSusDatos, Toast.LENGTH_SHORT).show());
        }).addOnFailureListener(e -> {
            Progreso.dismiss();
            e.printStackTrace();
            Toast.makeText(Registrarse.this, R.string.HaOcurridoUnErrorAlSubirLaImagen, Toast.LENGTH_SHORT).show();
        }).addOnProgressListener(snapshot -> Progreso.setMessage(getString(R.string.EspereUnMomento) + "\n" + getString(R.string.Progreso) + ": " + (int) (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount()) + " %"));
    }

    @NonNull
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
}