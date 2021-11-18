package com.pedba.gimnasiovalhalla.Modelos;

public class Cliente {
    String Id, Nombre, Correo, Foto, Telefono, Contraseña;

    public Cliente(String id, String nombre, String correo, String foto, String telefono, String contraseña) {
        Id = id;
        Nombre = nombre;
        Correo = correo;
        Foto = foto;
        Telefono = telefono;
        Contraseña = contraseña;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getNombre() {
        return Nombre;
    }

    public String getCorreo() {
        return Correo;
    }

    public void setCorreo(String correo) {
        Correo = correo;
    }

    public String getFoto() {
        return Foto;
    }

    public String getTelefono() {
        return Telefono;
    }

    public String getContraseña() {
        return Contraseña;
    }

    public void setContraseña(String contraseña) {
        Contraseña = contraseña;
    }
}
