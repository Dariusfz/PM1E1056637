package com.example.pm1e1056637.configuracion;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLConexion extends SQLiteOpenHelper {
    public SQLConexion(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Transacciones.createTableContactos);
        db.execSQL(TransaccionesPais.createTablePais);
        insertarPaisesIniciales(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Transacciones.DROPTableContactos);
        db.execSQL(TransaccionesPais.DROPTablePais);
        onCreate(db);
    }

    public void insertarPaisesIniciales(SQLiteDatabase db) {
        String[][] paises = {
                {"Honduras", "504"},
                {"El Salvador", "503"},
                {"Guatemala", "502"},
                {"Nicaragua", "505"},
                {"Costa Rica", "506"}
        };

        for (String[] pais : paises) {
            ContentValues values = new ContentValues();
            values.put(TransaccionesPais.nombrePais, pais[0]);
            values.put(TransaccionesPais.codigoPais, pais[1]);
            db.insert(TransaccionesPais.tablaPaises, null, values);
        }
    }
}
