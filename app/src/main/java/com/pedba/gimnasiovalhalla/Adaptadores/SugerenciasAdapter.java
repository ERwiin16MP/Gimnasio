package com.pedba.gimnasiovalhalla.Adaptadores;

import static com.pedba.gimnasiovalhalla.QuejasSugerencias.SUGERENCIAS;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pedba.gimnasiovalhalla.Login;
import com.pedba.gimnasiovalhalla.Modelos.QuejaSugerenciaModelo;
import com.pedba.gimnasiovalhalla.R;

import java.util.ArrayList;

public class SugerenciasAdapter extends RecyclerView.Adapter<SugerenciasAdapter.MyViewHolder> {

    private final Context context;
    private final ArrayList<QuejaSugerenciaModelo> list;
    private DatabaseReference TablaSugerencias = FirebaseDatabase.getInstance().getReference(SUGERENCIAS);

    public SugerenciasAdapter(Context context, ArrayList<QuejaSugerenciaModelo> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SugerenciasAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_qs, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SugerenciasAdapter.MyViewHolder holder, int position) {
        String Id = list.get(position).getId();
        String Fecha = list.get(position).getFecha();
        String Descripcion = list.get(position).getDescripcion();
        String Cliente = "Anonimo";
        for (int i = 0; i < Login.Clientes.size(); i++)
            if (list.get(position).getCliente_Id().equals(Login.Clientes.get(i).getId()))
                Cliente = Login.Clientes.get(i).getNombre();
        holder.Label.setText("Fecha: ".concat(Fecha).concat("\n\n").concat("Cliente: ".concat(Cliente).concat("\n\n").concat("Descripción: ").concat(Descripcion)));
        holder.Label.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Eliminar");
            builder.setMessage("¿Eliminar la sugerencia?");
            builder.setPositiveButton("Vale", (dialog, which) -> TablaSugerencias.child(Id).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful())
                    Toast.makeText(context, "Se ha eliminado la sugerencia", Toast.LENGTH_SHORT).show();
            }));
            builder.setNegativeButton("No", null);
            builder.create().show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView Label;

        public MyViewHolder(@NonNull View view) {
            super(view);
            Label = view.findViewById(R.id.TextView_QS);
        }
    }
}
