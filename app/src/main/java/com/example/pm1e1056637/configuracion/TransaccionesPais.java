package com.example.pm1e1056637.configuracion;

public class TransaccionesPais {
    //crar tabla para los paises
   // public static final String nombreBaseDatos="PAISES";

    //nombre de la tabla paises
    public static final String tablaPaises = "pais";

    //campos de la tabla
    public static final String id = "id";
    public static final String nombrePais = "nombre";
    public static final String codigoPais= "codigo";

    //crear la tabla

    public static final String createTablePais="CREATE TABLE "+ tablaPaises+ "( "+ id+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            nombrePais+" TEXT, "+
            codigoPais+" TEXT )";
    //DROP TABLE
    public static final String DROPTablePais="DROP TABLE IF EXISTS "+tablaPaises;

    //DML SELECT
    public static final String selectPais="SELECT * FROM "+tablaPaises;



}
