package com.example.pm1e1056637.configuracion;

public class Transacciones {
    //Nombre de la base de datos
    public static final String NameDB="ListaContactos";

    //Tablas de la DB
    public static final String tablaContactos="contactos";

    //campos de la tabla
    public static final String id="id";
    public static final String nombre="nombre";
    public static final String telefono="telefono";
    public static final String nota="nota";
    public static final String fotoPersona="imagen";

    public static final String pais= "pais";

    public static final String codigoPais="codigo";

    //DDL createc

    public static final String createTableContactos="CREATE TABLE "+ tablaContactos+ "( "+ id+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            nombre+" TEXT, "+
            telefono+" TEXT, "+
            nota+" TEXT, "+
            pais+" TEXT, "+
            fotoPersona+" BLOB)";

    //DROP TABLE
    public static final String DROPTableContactos="DROP TABLE IF EXISTS "+tablaContactos;

    //DML SELECT
    public static final String selectContactos = "SELECT c." + id + ", c." + nombre + ", c." + telefono + ", p." + codigoPais +", c."+fotoPersona+ " FROM " + tablaContactos + " c" +
                    " LEFT JOIN " + TransaccionesPais.tablaPaises + " p ON c." + pais + " = p." + TransaccionesPais.nombrePais;

    public static final String busquedaContacto ="SELECT id, nombre, telefono, imagen FROM contactos";

}
