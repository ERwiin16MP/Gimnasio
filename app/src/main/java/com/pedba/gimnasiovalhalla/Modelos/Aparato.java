package com.pedba.gimnasiovalhalla.Modelos;

public class Aparato {
    String Id, Descripcion, Cantidad, Area;

    public Aparato(String id, String descripcion, String cantidad, String area) {
        Id = id;
        Descripcion = descripcion;
        Cantidad = cantidad;
        Area = area;
    }

    public String getId() {
        return Id;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public String getCantidad() {
        return Cantidad;
    }

    public String getArea() {
        return Area;
    }
}
