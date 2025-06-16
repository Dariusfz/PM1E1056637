package com.example.pm1e1056637;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.pm1e1056637.configuracion.Pais;
import com.example.pm1e1056637.configuracion.SQLConexion;
import com.example.pm1e1056637.configuracion.Transacciones;
import com.example.pm1e1056637.configuracion.TransaccionesPais;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

        Spinner paises;
        EditText txtNombre, txtTelefono, txtNota;
        Button btnAgregar, btnVerContactos;
        ImageButton btnFoto;
        ImageView imgFoto;

        String rutaImagen;

        ArrayList<Pais> pais;
        ArrayList<String> listaPais;

        SQLConexion conexion;

       public static int version=3;


    static  final int peticionCamara= 101;
    static final int peticionFoto= 102;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        paises= findViewById(R.id.paises);
        txtNombre=  findViewById(R.id.nombre);
        txtTelefono = findViewById(R.id.telefono);
        txtNota = findViewById(R.id.nota);
        btnAgregar = findViewById(R.id.btnAgregar);
        btnVerContactos = findViewById(R.id.btnVerContactos);
        imgFoto = findViewById(R.id.imgFoto);
        btnFoto= findViewById(R.id.btnFoto);

        conexion= new SQLConexion(this, Transacciones.NameDB,null,version);

        obtenerDatos();

        ArrayAdapter<CharSequence> adp = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,listaPais);
        paises.setAdapter(adp);


        //listener para agregar contactos
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validacion();

                if(validacion()){
                    agregarContactos();
                    limpiarDatos();
                }


            }
        });

//accion para acceder a la camara
        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permisos();

            }
        });

        //accion para ver la lista de contactos
        btnVerContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ListaContactos.class);
                startActivity(intent);

            }
        });





    }
    //metodo para saber si relleno los campos necesarios
    public boolean validacion(){
        boolean validar= true;
        String nombre= txtNombre.getText().toString();
        String telefono = txtTelefono.getText().toString();
        String nota = txtNota.getText().toString();
        if(nombre.isEmpty()){
            txtNombre.setError("Nombre es requerido");
            validar=false;
        }
        if(telefono.isEmpty()){
            txtTelefono.setError("Telefono es requerido");
            validar=false;
        }
        if(nota.isEmpty()){
            txtNota.setError("Nota es requerida");
            validar=false;
        }

        return validar;
    }

    //solicitar permisos para usar la camara

    private void permisos()
    {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            //solicitar el permiso para usar la camara
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},peticionCamara);
        }else{
            //tomar la foto
            tomarFoto();

        }
    }

    private void tomarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null){
            File imagenArchivo= null;
            try {
                imagenArchivo= capturarImagen();

            }catch (IOException ex){

                Log.e("Error",ex.toString());
            }

            if(imagenArchivo !=null){
                Uri fotoUri = FileProvider.getUriForFile(this,"com.example.pm1e1056637.fileprovider",imagenArchivo);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,fotoUri);
                startActivityForResult(intent,peticionFoto);
            }


        }
        // startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==peticionCamara){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                tomarFoto();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==peticionFoto && resultCode==RESULT_OK){


            if (rutaImagen != null) {
                File imgFile = new File(rutaImagen);
                if (imgFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imgFoto.setImageBitmap(bitmap);
                }

            /*Bundle extras = data.getExtras();
            Bitmap image = (Bitmap) extras.get("data");
            imageFoto.setImageBitmap(image);*/
            }
        }
    }

    //metodo para capturar la imagen y guardarla con formato de fecha
    private File capturarImagen() throws IOException {
        String tiempoDeFoto = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String nombreImagen = "foto_"+tiempoDeFoto+"_";
        File direcotrio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //si no existe el directorio lo crea
        if (!direcotrio.exists()) {
            direcotrio.mkdirs();
        }
        File imagenTemporal = File.createTempFile(nombreImagen,".jpg",direcotrio);
        rutaImagen= imagenTemporal.getAbsolutePath();

        return imagenTemporal;

    }

    //metodo para crar los paises


    //metodo para obtener los paises en el spinner
    private void obtenerDatos()
    {
        try {
            SQLiteDatabase db = conexion.getReadableDatabase();
            Pais objPaises = null;
            pais= new ArrayList<Pais>();
            Cursor cursor= db.rawQuery(TransaccionesPais.selectPais,null);

            while (cursor.moveToNext()){
                objPaises = new Pais();
                objPaises.setId(cursor.getInt(0));
                objPaises.setNombrePais(cursor.getString(1));
                objPaises.setCodRegion(cursor.getString(2));

                pais.add(objPaises);
            }
            cursor.close();
            fillData();

        } catch (Exception e) {
            Log.e("ERROR BD", e.toString());
            Toast.makeText(this, "Error al cargar países", Toast.LENGTH_SHORT).show();
        }


    }

    private void fillData()
    {
        listaPais= new ArrayList<String>();

        for(int i=0; i<pais.size();i++){
            listaPais.add(pais.get(i).getNombrePais()+" "+"("+ pais.get(i).getCodRegion()+")");
        }
    }

    //metodo para agregar contactos
    public void agregarContactos(){
        conexion = new SQLConexion(this,Transacciones.NameDB,null,version);
        Pais objPais = new Pais();

        SQLiteDatabase db = conexion.getWritableDatabase();

        String paisDeOrigen = paises.getSelectedItem().toString();// obtenemos el pais selecionado por el usuario
        ContentValues values = new ContentValues();
        values.put(Transacciones.nombre,txtNombre.getText().toString());
        values.put(Transacciones.telefono, txtTelefono.getText().toString());
        values.put(Transacciones.nota,txtNota.getText().toString());
        values.put(Transacciones.pais,paisDeOrigen);
        values.put(Transacciones.codigoPais,objPais.getCodRegion());


        if (rutaImagen != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(rutaImagen);
            if (bitmap != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                byte[] byteArrayFoto = stream.toByteArray();
                values.put(Transacciones.fotoPersona, byteArrayFoto);
            }
        }

        Long resultado = db.insert(Transacciones.tablaContactos,Transacciones.id,values);

        if(resultado != -1){
            Toast.makeText(getApplicationContext(), "Contacto ingresado con exito ",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "Erros al ingresar la persona ",Toast.LENGTH_LONG).show();
        }
        db.close();
    }

    public void limpiarDatos(){
        txtNombre.setText("");
        txtTelefono.setText("");
        txtNota.setText(" ");
        imgFoto.setImageResource(R.drawable.imagenpersona);
        rutaImagen=null;
    }


}