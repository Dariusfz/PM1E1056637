package com.example.pm1e1056637.configuracion;

public class Contacto {

    private int id;
    private String nombre;
    private String telefono;
    private String nota;
    private byte[] fotoPersona;

    private String pais;

    private String codigoPais;

    public Contacto() {
    }

    public Contacto(int id,String nombre, String telefono, String nota, byte[] fotoPersona, String pais, String codigoPais) {
        this.id=id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.nota = nota;
        this.fotoPersona = fotoPersona;
        this.pais=pais;
        this.codigoPais=codigoPais;
    }

    public String getCodigoPais() {
        return codigoPais;
    }

    public void setCodigoPais(String codigoPais) {
        this.codigoPais = codigoPais;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public byte[] getFotoPersona() {
        return fotoPersona;
    }

    public void setFotoPersona(byte[] fotoPersona) {
        this.fotoPersona = fotoPersona;
    }
}
