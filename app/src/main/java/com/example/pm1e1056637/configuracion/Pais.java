package com.example.pm1e1056637.configuracion;

public class Pais {
    private int id;
    private String nombrePais;
    private String codRegion;

    public Pais() {
    }

    public Pais(int id, String nombrePais, String codRegion) {
        this.id = id;
        this.nombrePais = nombrePais;
        this.codRegion = codRegion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombrePais() {
        return nombrePais;
    }

    public void setNombrePais(String nombrePais) {
        this.nombrePais = nombrePais;
    }

    public String getCodRegion() {
        return codRegion;
    }

    public void setCodRegion(String codRegion) {
        this.codRegion = codRegion;
    }
}
