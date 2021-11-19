package com.pedba.gimnasiovalhalla;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pedba.gimnasiovalhalla.Modelos.Cliente;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FormularioCliente extends AppCompatActivity {

    private static final String ID_CLIENTE = "Id_cliente";
    private static final String NOM_CLIENTE = "Nom_cliente";
    private static final String CORREO_CLIENTE = "Correo_cliente";
    private static final String TEL_CLIENTE = "Tel_cliente";
    private static final String CLIENTES = "Clientes";
    private static final String PASS_CLIENTE = "Pass_cliente";
    private static final String FOTO_CLIENTE = "Foto_cliente";
    private static final int CODIGO_IMAGEN = 0;
    static ArrayList<Cliente> Clientes;
    private final DatabaseReference BaseDeDatosClientes = FirebaseDatabase.getInstance().getReference(CLIENTES);
    int Indice = -1;
    Boolean Datos = false;
    private TextInputLayout Layout_Contraseña;
    private TextInputLayout Layout_ContraseñaConfirmacion;
    private EditText TextBox_NombreCompleto, TextBox_Correo, TextBox_Telefono, TextBox_Contraseña, TextBox_ContraseñaConfirmacion;
    private ImageView FotoCliente;
    private Uri ImagenUri;
    private StorageReference storageReference;
    private ListView ListaDeClientes;
    private ProgressDialog Progreso;
    private Button Boton;

    public FormularioCliente() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formulario_cliente);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        inicializarVistas();
        setMetodoDelBoton(getIntent().getExtras().getInt("Opcion"));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setMetodoDelBoton(int opcion) {
        switch (opcion) {
            case 0:
                Boton.setBackgroundResource(R.drawable.boton_redondo_amarillo);
                Boton.setText(getString(R.string.RegistrarCliente));
                Boton.setOnClickListener(v -> registrar());
                ListaDeClientes.setVisibility(View.GONE);
                break;
            case 1:
                Boton.setBackgroundResource(R.drawable.boton_redondo_rojo);
                Boton.setText(getString(R.string.EliminarCliente));
                Boton.setTextColor(getColor(R.color.white));
                Boton.setOnClickListener(v -> eliminar());
                setListView();
                ocultarLayouts();
                break;
            case 2:
                Boton.setBackgroundResource(R.drawable.boton_redondo_amarillo);
                Boton.setText(getString(R.string.ModificarCliente));
                Boton.setOnClickListener(v -> modificar());
                setListView();
                Layout_ContraseñaConfirmacion.setVisibility(View.GONE);
                break;
            case 3:
                Boton.setVisibility(View.GONE);
                setListView();
                ocultarLayouts();
                break;
        }
    }

    private void ocultarLayouts() {
        TextBox_NombreCompleto.setEnabled(false);
        TextBox_Correo.setEnabled(false);
        TextBox_Telefono.setEnabled(false);
        FotoCliente.setEnabled(false);
        Layout_Contraseña.setVisibility(View.GONE);
        Layout_ContraseñaConfirmacion.setVisibility(View.GONE);
    }

    private void modificar() {
        if (Indice > -1) {
            String NombreCompleto = TextBox_NombreCompleto.getText().toString(), Correo = TextBox_Correo.getText().toString(),
                    Telefono = TextBox_Telefono.getText().toString(), Contraseña = TextBox_Contraseña.getText().toString();

            if (!verificarInternet()) {
                Toast.makeText(this, R.string.NoHayInternet, Toast.LENGTH_SHORT).show();
            } else if (NombreCompleto.isEmpty()) {
                Snackbar.make(findViewById(android.R.id.content), R.string.IngreseSuNombre, Snackbar.LENGTH_LONG).show();
                TextBox_NombreCompleto.setError(getString(R.string.CampoRequerido));
                TextBox_NombreCompleto.requestFocus();
            } else if (Correo.isEmpty()) {
                Snackbar.make(findViewById(android.R.id.content), R.string.UsuarioVacio, Snackbar.LENGTH_LONG).show();
                TextBox_Correo.setError(getString(R.string.CampoRequerido));
                TextBox_Correo.requestFocus();
            } else if (Contraseña.isEmpty()) {
                Snackbar.make(findViewById(android.R.id.content), R.string.ContraseñaVacia, Snackbar.LENGTH_LONG).show();
                TextBox_Contraseña.setError(getString(R.string.CampoRequerido));
                TextBox_Contraseña.requestFocus();
            } else if (Contraseña.length() < 7) {
                Toast.makeText(this, R.string.LaClaveDebe, Toast.LENGTH_SHORT).show();
            } else modificarCliente(NombreCompleto, Correo, Telefono, Contraseña);
        } else Toast.makeText(this, R.string.DebeDeSeleccionarUnCliente, Toast.LENGTH_LONG).show();
    }

    private void modificarCliente(String NombreCompleto, String Correo, String Telefono, String Contraseña) {
        ProgressDialog Progreso = new ProgressDialog(this);
        Progreso.setTitle(getString(R.string.ProcesoDeRegistro));
        Progreso.setMessage(getString(R.string.EspereUnMomento));
        Progreso.setCanceledOnTouchOutside(false);
        Progreso.show();
        if (Datos) {
            StorageReference riversRef = storageReference.child(CLIENTES + "/" + Correo);
            riversRef.putFile(ImagenUri).addOnSuccessListener(taskSnapshot -> {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                Uri dowloadUri = uriTask.getResult();
                String Id = Clientes.get(Indice).getId();
                Map<String, Object> Datos = new HashMap<>();
                Datos.put(ID_CLIENTE, Id);
                Datos.put(NOM_CLIENTE, NombreCompleto);
                Datos.put(CORREO_CLIENTE, Correo);
                Datos.put(TEL_CLIENTE, Telefono);
                Datos.put(PASS_CLIENTE, Contraseña);
                Datos.put(FOTO_CLIENTE, dowloadUri.toString());
                BaseDeDatosClientes.child(Id).setValue(Datos).addOnCompleteListener(task -> {
                    Toast.makeText(FormularioCliente.this, R.string.ModificacionesExitosas, Toast.LENGTH_SHORT).show();
                    Progreso.dismiss();
                    finish();
                }).addOnFailureListener(e -> Toast.makeText(FormularioCliente.this, R.string.HaOcurridoUnErrorAlModificarSusDatos, Toast.LENGTH_SHORT).show());
                finish();
            }).addOnFailureListener(e -> {
                Progreso.dismiss();
                Toast.makeText(FormularioCliente.this, R.string.HaOcurridoUnErrorAlModificarSusDatos, Toast.LENGTH_SHORT).show();
            }).addOnProgressListener(snapshot -> Progreso.setMessage(getString(R.string.EspereUnMomento) + "\n" + getString(R.string.Progreso) + ": " + (int) (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount()) + " %"));
        } else {
            String Id = Clientes.get(Indice).getId();
            Map<String, Object> Datos = new HashMap<>();
            Datos.put(ID_CLIENTE, Id);
            Datos.put(NOM_CLIENTE, NombreCompleto);
            Datos.put(CORREO_CLIENTE, Correo);
            Datos.put(TEL_CLIENTE, Telefono);
            Datos.put(PASS_CLIENTE, Contraseña);
            Datos.put(FOTO_CLIENTE, Clientes.get(Indice).getFoto());
            BaseDeDatosClientes.child(Id).setValue(Datos).addOnCompleteListener(task -> {
                Toast.makeText(FormularioCliente.this, R.string.ModificacionesExitosas, Toast.LENGTH_SHORT).show();
                Progreso.dismiss();
                finish();
            }).addOnFailureListener(e -> Toast.makeText(FormularioCliente.this, R.string.HaOcurridoUnErrorAlModificarSusDatos, Toast.LENGTH_SHORT).show());
        }
    }

    private void setListView() {
        Progreso = new ProgressDialog(this);
        Progreso.setTitle(getString(R.string.DescargandoInformacion));
        Progreso.setMessage(getString(R.string.EspereUnMomento));
        Progreso.setCanceledOnTouchOutside(false);
        Progreso.show();
        BaseDeDatosClientes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Clientes = new ArrayList<>();
                    String Id, Nombre, Correo, Foto, Telefono, Contraseña;
                    for (DataSnapshot i : snapshot.getChildren()) {
                        Id = i.child(ID_CLIENTE).getValue().toString();
                        Nombre = i.child(NOM_CLIENTE).getValue().toString();
                        Correo = i.child(CORREO_CLIENTE).getValue().toString();
                        Foto = i.child(FOTO_CLIENTE).getValue().toString();
                        Telefono = i.child(TEL_CLIENTE).getValue().toString();
                        Contraseña = i.child(PASS_CLIENTE).getValue().toString();
                        if (!Correo.equals("root"))
                            Clientes.add(new Cliente(Id, Nombre, Correo, Foto, Telefono, Contraseña));
                    }
                    ListaDeClientes.setAdapter(new AdaptadorClientes());
                    ListaDeClientes.setOnItemClickListener((parent, view, position, id) -> {
                        TextBox_NombreCompleto.setText(Clientes.get(position).getNombre());
                        TextBox_Correo.setText(Clientes.get(position).getCorreo());
                        TextBox_Telefono.setText(Clientes.get(position).getTelefono());
                        TextBox_Contraseña.setText(Clientes.get(position).getContraseña());
                        Glide.with(FormularioCliente.this).load(Clientes.get(position).getFoto()).into(FotoCliente);
                        Indice = position;
                    });
                    Progreso.dismiss();
                } else
                    Toast.makeText(FormularioCliente.this, R.string.NoHayClientes, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Progreso.dismiss();
                Toast.makeText(FormularioCliente.this, R.string.HaOcurridoUnError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminar() {
        if (Indice > -1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.EliminarCliente))
                    .setMessage("¿Estas seguro de eliminar a \"" + Clientes.get(Indice).getNombre() + "\"?")
                    .setNegativeButton(R.string.No, null)
                    .setPositiveButton(R.string.Vale, (dialog, which) -> {
                        StorageReference reference = storageReference.child(CLIENTES + "/" + Clientes.get(Indice).getCorreo());
                        reference.delete().addOnSuccessListener(aVoid -> BaseDeDatosClientes.child(Clientes.get(Indice).getId()).removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(FormularioCliente.this, R.string.ClienteEliminadoCorrectamente, Toast.LENGTH_SHORT).show();
                                Progreso.dismiss();
                                finish();
                            }
                        }).addOnFailureListener(e -> Toast.makeText(FormularioCliente.this, R.string.HaOcurridoUnError, Toast.LENGTH_LONG).show())).addOnFailureListener(e -> Toast.makeText(FormularioCliente.this, R.string.HaOcurridoUnError, Toast.LENGTH_LONG).show());
                    }).show();
        } else Toast.makeText(this, R.string.DebeDeSeleccionarUnCliente, Toast.LENGTH_SHORT).show();
    }

    private void registrar() {
        String NombreCompleto = TextBox_NombreCompleto.getText().toString(), Correo = TextBox_Correo.getText().toString(),
                Telefono = TextBox_Telefono.getText().toString(), Contraseña = TextBox_Contraseña.getText().toString(),
                ContraseñaConfirmacion = TextBox_ContraseñaConfirmacion.getText().toString();

        if (!verificarInternet()) {
            Toast.makeText(this, R.string.NoHayInternet, Toast.LENGTH_SHORT).show();
        } else if (NombreCompleto.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), R.string.IngreseSuNombre, Snackbar.LENGTH_LONG).show();
            TextBox_NombreCompleto.setError(getString(R.string.CampoRequerido));
            TextBox_NombreCompleto.requestFocus();
        } else if (Correo.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), R.string.UsuarioVacio, Snackbar.LENGTH_LONG).show();
            TextBox_Correo.setError(getString(R.string.CampoRequerido));
            TextBox_Correo.requestFocus();
        } else if (Contraseña.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), R.string.ContraseñaVacia, Snackbar.LENGTH_LONG).show();
            TextBox_Contraseña.setError(getString(R.string.CampoRequerido));
            TextBox_Contraseña.requestFocus();
        } else if (Contraseña.length() < 7) {
            Toast.makeText(this, R.string.LaClaveDebe, Toast.LENGTH_SHORT).show();
        } else if (ContraseñaConfirmacion.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), R.string.ContraseñaDeConfirmacionVacia, Snackbar.LENGTH_LONG).show();
            TextBox_ContraseñaConfirmacion.setError(getString(R.string.CampoRequerido));
            TextBox_ContraseñaConfirmacion.requestFocus();
        } else if (!Contraseña.equals(ContraseñaConfirmacion)) {
            Snackbar.make(findViewById(android.R.id.content), R.string.ContraseñasDiferentes, Snackbar.LENGTH_LONG).show();
            TextBox_ContraseñaConfirmacion.requestFocus();
        } else if (ImagenUri == null)
            Snackbar.make(findViewById(android.R.id.content), "Seleccione una imagen de usuario", Snackbar.LENGTH_LONG).show();
        else registrarCliente(NombreCompleto, Correo, Telefono, Contraseña);
    }

    private void registrarCliente(String NombreCompleto, String Correo, String Telefono, String Contraseña) {
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
                Toast.makeText(FormularioCliente.this, "Registro exitoso, ahora el cliente puede iniciar sesión", Toast.LENGTH_SHORT).show();
                Progreso.dismiss();
                finish();
            }).addOnFailureListener(e -> Toast.makeText(FormularioCliente.this, "Error al registrar al cliente", Toast.LENGTH_SHORT).show());
        }).addOnFailureListener(e -> {
            Progreso.dismiss();
            Toast.makeText(FormularioCliente.this, R.string.HaOcurridoUnErrorAlSubirLaImagen, Toast.LENGTH_SHORT).show();
        }).addOnProgressListener(snapshot -> Progreso.setMessage(getString(R.string.EspereUnMomento) + "\n" + getString(R.string.Progreso) + ": " + (int) (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount()) + " %"));
    }

    private void inicializarVistas() {
        Boton = findViewById(R.id.Boton_FormularioClientes);
        TextBox_NombreCompleto = findViewById(R.id.EditText_NombreCompletoClienteFormulario);
        TextBox_Correo = findViewById(R.id.EditText_CorreoClienteFormulario);
        TextBox_Telefono = findViewById(R.id.EditText_TelefonoClienteFormulario);
        TextBox_Contraseña = findViewById(R.id.EditText_ContraseñaClienteFormulario);
        TextBox_ContraseñaConfirmacion = findViewById(R.id.EditText_ContraseñaConfirmacionClienteFormulario);
        FotoCliente = findViewById(R.id.ImageView_FotoClienteFormulario);
        ListaDeClientes = findViewById(R.id.ListView_ListaClientes);
        Layout_Contraseña = findViewById(R.id.ReferenciaContraseñaClienteFormulario);
        Layout_ContraseñaConfirmacion = findViewById(R.id.ReferenciaContraseñaConfirmacionClienteFormulario);
    }

    public void imagen(View view) {
        Intent intent = new Intent();
        intent.setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, CODIGO_IMAGEN);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CODIGO_IMAGEN && resultCode == RESULT_OK && data != null && data.getData() != null) {
            ImagenUri = data.getData();
            FotoCliente.setImageURI(ImagenUri);
            Datos = true;
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    public class AdaptadorClientes extends BaseAdapter {

        private final LayoutInflater inflater;

        public AdaptadorClientes() {
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return Clientes.size();
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
            label_Nombre.setText(Clientes.get(i).getNombre());
            return view;
        }
    }
}