package com.example.pm1e1056637;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pm1e1056637.configuracion.Contacto;
import com.example.pm1e1056637.configuracion.Pais;
import com.example.pm1e1056637.configuracion.SQLConexion;
import com.example.pm1e1056637.configuracion.Transacciones;
import com.example.pm1e1056637.configuracion.TransaccionesPais;

import java.util.ArrayList;

public class ListaContactos extends AppCompatActivity {

    SQLConexion conexion;
    ListView listaContactos;

    ArrayList<Contacto> listContacto;
    ArrayList<Pais> listaPais;
    ArrayList<String> lista;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_contactos);

        listaContactos = findViewById(R.id.listaContactos);

        conexion= new SQLConexion(this,
                Transacciones.NameDB,null,MainActivity.version);

        obtenerContactos();
        ArrayAdapter adp = new ArrayAdapter(this, android.R.layout.simple_list_item_1,lista);
        listaContactos.setAdapter(adp);

    }

    private void obtenerContactos()
    {
        try {
            SQLiteDatabase db = conexion.getReadableDatabase();
            Contacto objContacto = null;


            listContacto = new ArrayList<>();
            Cursor cursor= db.rawQuery(Transacciones.selectContactos,null);


            while (cursor.moveToNext()){
                objContacto = new Contacto();

                objContacto.setId(cursor.getInt(0));
                objContacto.setNombre(cursor.getString(1));
                objContacto.setTelefono(cursor.getString(2));

                listContacto.add(objContacto);
            }
            cursor.close();
            fillDataContactos();

        } catch (Exception e) {
            Log.e("ERROR BD", e.toString());
            Toast.makeText(this, "Error al cargar países", Toast.LENGTH_SHORT).show();
        }
    }

    private void fillDataContactos()
    {
        lista= new ArrayList<String>();

        for(int i=0; i<listContacto.size();i++){
            lista.add(listContacto.get(i).getNombre()+" | "+listContacto.get(i).getCodigoPais() + listContacto.get(i).getTelefono());
        }
    }
}