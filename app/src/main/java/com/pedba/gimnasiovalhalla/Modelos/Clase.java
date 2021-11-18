package com.pedba.gimnasiovalhalla.Modelos;

public class Clase {
    String Id, NombreDeLaClase, Entrenador, Hora, Lunes, Martes, Miercoles, Jueves, Viernes, Sabado;

    public Clase(String id, String nombreDeLaClase, String entrenador, String hora, String lunes, String martes, String miercoles, String jueves, String viernes, String sabado) {
        Id = id;
        NombreDeLaClase = nombreDeLaClase;
        Entrenador = entrenador;
        Hora = hora;
        Lunes = lunes;
        Martes = martes;
        Miercoles = miercoles;
        Jueves = jueves;
        Viernes = viernes;
        Sabado = sabado;
    }

    public String getId() {
        return Id;
    }

    public String getNombreDeLaClase() {
        return NombreDeLaClase;
    }

    public String getEntrenador() {
        return Entrenador;
    }

    public String getHora() {
        return Hora;
    }

    public String getLunes() {
        return Lunes;
    }

    public String getMartes() {
        return Martes;
    }

    public String getMiercoles() {
        return Miercoles;
    }

    public String getJueves() {
        return Jueves;
    }

    public String getViernes() {
        return Viernes;
    }

    public String getSabado() {
        return Sabado;
    }
}
