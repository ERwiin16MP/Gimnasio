package com.pedba.gimnasiovalhalla.Modelos;

public class QuejaSugerenciaModelo {
    String Id, Descripcion, Fecha, Cliente_Id;

    public QuejaSugerenciaModelo(String id, String descripcion, String fecha, String cliente_Id) {
        Id = id;
        Descripcion = descripcion;
        Fecha = fecha;
        Cliente_Id = cliente_Id;
    }

    public String getId() {
        return Id;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public String getFecha() {
        return Fecha;
    }

    public String getCliente_Id() {
        return Cliente_Id;
    }
}
