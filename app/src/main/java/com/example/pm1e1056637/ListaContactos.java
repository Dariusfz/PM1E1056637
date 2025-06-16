package com.example.pm1e1056637;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;

import com.example.pm1e1056637.configuracion.Contacto;
import com.example.pm1e1056637.configuracion.Pais;
import com.example.pm1e1056637.configuracion.SQLConexion;
import com.example.pm1e1056637.configuracion.Transacciones;
import com.example.pm1e1056637.configuracion.TransaccionesPais;

import java.util.ArrayList;

public class ListaContactos extends AppCompatActivity {

    SQLConexion conexion;
    ListView listaContactos;

    SearchView txtBuscar;

    ArrayList<Contacto> listContacto;

    ArrayList<String> lista;

    Button btnAtras, btnCompartir, btnVerImagen, btnEliminar, btnActualizar;
    int posicion = -1;
    private static final int CODIGO_PERMISO_LLAMADA = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_contactos);


        listaContactos = findViewById(R.id.listaContactos);
        txtBuscar = findViewById(R.id.txtBuscar);
        btnAtras = findViewById(R.id.btnAtras);
        btnCompartir = findViewById(R.id.btnCompartir);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnVerImagen = findViewById(R.id.btnVerImg);

        listaContactos.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        conexion = new SQLConexion(this,
                Transacciones.NameDB, null, MainActivity.version);

        listaContactos.setOnItemClickListener((parent, view, po, id) -> {
            posicion = po;
            for (int i = 0; i < parent.getChildCount(); i++) {
                parent.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
            }

            view.setBackgroundColor(Color.LTGRAY);
        });

        obtenerContactos();
        ArrayAdapter adp = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lista);
        listaContactos.setAdapter(adp);

        //metodo para buscar contactos en tiempo real

        txtBuscar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String texto) {
                buscarContactos(texto);
                return true;
            }
        });

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contacto contactoSeleccionado = obtenerContactoSeleccionado();

                if (contactoSeleccionado != null) {
                    compartirContacto(contactoSeleccionado);
                } else {
                    Toast.makeText(ListaContactos.this, "Selecciona un contacto primero", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnVerImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contacto contactoSeleccionado = obtenerContactoSeleccionado();

                if (contactoSeleccionado != null && contactoSeleccionado.getFotoPersona() != null) {
                    mostrarImagenContacto(contactoSeleccionado.getFotoPersona());
                } else {
                    Toast.makeText(ListaContactos.this,
                            "No hay imagen para este contacto",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        listaContactos.setOnItemLongClickListener((parent, view, position, id) -> {

            Contacto contacto = listContacto.get(position);

            mostrarDialogoLlamada(contacto.getTelefono());

            return true;
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarContactoSeleccionado();
            }
        });

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Toast.makeText(getApplicationContext(),"NO QUEDO TIEMPO INGE ;(", Toast.LENGTH_LONG).show();
            }
        });


    }



    private void mostrarDialogoLlamada(String numeroTelefono) {

        String numeroFormateado = formatoTelefono(numeroTelefono);

        new AlertDialog.Builder(this)
                .setTitle("Llamar a contacto")
                .setMessage("¿Deseas llamar al número " + numeroFormateado + "?")
                .setPositiveButton("Llamar", (dialog, which) -> {
                    // Verificar permisos antes de llamar
                    if (tienePermisoLlamada()) {
                        iniciarLlamada(numeroTelefono);
                    } else {
                        solicitarPermisoLlamada();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .setIcon(android.R.drawable.ic_menu_call)
                .show();
    }

    private void mostrarImagenContacto(byte[] imagenBlob) {
        try {
            // Convertir BLOB a Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(imagenBlob, 0, imagenBlob.length);

            // Crear un diálogo para mostrar la imagen
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("FOTO CONTACTO");

            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(bitmap);
            imageView.setAdjustViewBounds(true);
            imageView.setMaxHeight(1000);
            imageView.setMaxWidth(800);

            builder.setView(imageView);
            builder.setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();

        } catch (Exception e) {
            Log.e("IMAGEN_ERROR", "Error al mostrar imagen: " + e.getMessage());
            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
        }
    }


    private void compartirContacto(Contacto contacto) {

        String datosContacto = "Nombre: " + contacto.getNombre() + "\n" +
                "Teléfono: " + contacto.getTelefono() + "\n" +
                "País: " + contacto.getPais();


        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Compartiendo contacto");
        shareIntent.putExtra(Intent.EXTRA_TEXT, datosContacto);

        // Verificar si se puede compartir el contacto
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(shareIntent, "Compartir contacto via"));
        } else {
            Toast.makeText(this, "No hay aplicaciones para compartir", Toast.LENGTH_SHORT).show();
        }
    }

    public Contacto obtenerContactoSeleccionado() {

        int posicion = listaContactos.getCheckedItemPosition();
        if (posicion != ListView.INVALID_POSITION) {
            return listContacto.get(posicion);
        }
        return null;
    }

    private void buscarContactos(String query) {
        SQLiteDatabase db = conexion.getReadableDatabase();

        // Consulta simplificada sin JOIN
        String sql = "SELECT id, nombre, telefono FROM contactos " +
                "WHERE nombre LIKE ? OR telefono LIKE ?";

        Cursor cursor = db.rawQuery(sql, new String[]{"%" + query + "%", "%" + query + "%"});

        ArrayList<String> resultados = new ArrayList<>();
        while (cursor.moveToNext()) {
            String contacto = cursor.getString(1) + " | " + cursor.getString(2);
            resultados.add(contacto);
        }
        cursor.close();

        // Actualizar el adaptador
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                resultados
        );
        listaContactos.setAdapter(adapter);
    }

    private void obtenerContactos() {
        try {
            SQLiteDatabase db = conexion.getReadableDatabase();
            Contacto objContacto = null;


            listContacto = new ArrayList<>();
            Cursor cursor = db.rawQuery(Transacciones.busquedaContacto, null);


            while (cursor.moveToNext()) {
                objContacto = new Contacto();

                objContacto.setId(cursor.getInt(0));
                objContacto.setNombre(cursor.getString(1));
                objContacto.setTelefono(cursor.getString(2));
                objContacto.setFotoPersona(cursor.getBlob(3));

                listContacto.add(objContacto);
            }
            cursor.close();
            fillDataContactos();

        } catch (Exception e) {
            Log.e("ERROR BD", e.toString());
            Toast.makeText(this, "Error al cargar países", Toast.LENGTH_SHORT).show();
        }
    }

    private void fillDataContactos() {
        lista = new ArrayList<String>();

        for (int i = 0; i < listContacto.size(); i++) {
            lista.add(listContacto.get(i).getNombre() + " | " + listContacto.get(i).getTelefono());
        }
    }

    //metodo para llamar al contacto
    private boolean tienePermisoLlamada() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
    }

    private void solicitarPermisoLlamada() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CALL_PHONE},
                CODIGO_PERMISO_LLAMADA);
    }

    private void iniciarLlamada(String numero) {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + limpiarNumero(numero)));
            startActivity(intent);
        } catch (SecurityException e) {
            Toast.makeText(this, "Permiso denegado para realizar llamadas",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private String limpiarNumero(String numero) {

        return numero.replaceAll("[^0-9+]", "");
    }

    private String formatoTelefono(String numero) {
        String limpio = limpiarNumero(numero);
        if (limpio.length() > 3) {
            return limpio.substring(0, 3) + " " + limpio.substring(3);
        }
        return limpio;
    }

    //METODO PARA ELIMINAR UN CONTACTO
    private void eliminarContactoSeleccionado() {

        if (posicion == -1 || posicion >= listContacto.size()) {
            Toast.makeText(this, "Selecciona un contacto primero", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostrar diálogo
        new AlertDialog.Builder(this)
                .setTitle("Confirmar")
                .setMessage("Estas seguro de eliminar este contacto?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    // Obtener el contacto a eliminar
                    Contacto contacto = listContacto.get(posicion);

                    // Eliminar de la base de datos
                    SQLiteDatabase db = conexion.getWritableDatabase();
                    try {
                        int filasEliminadas = db.delete(
                                Transacciones.tablaContactos,
                                Transacciones.id + " = ?",
                                new String[]{String.valueOf(contacto.getId())}
                        );

                        if (filasEliminadas > 0) {
                            // Eliminar de la lista en memoria
                            listContacto.remove(posicion);

                            // Actualizar la vista para que desaparezca el contacto eliminado
                            actualizarVista();

                            Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show();

                            // Resetear selección
                            posicion = -1;
                        } else {
                            Toast.makeText(this, "Error al eliminar el contacto", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("ELIMINAR", "Error: " + e.getMessage());
                        Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    } finally {
                        db.close();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // Metodo para actualizar la vista
    private void actualizarVista() {
        lista.clear();
        for (Contacto contacto : listContacto) {
            lista.add(contacto.getNombre() + " | " + contacto.getTelefono());
        }

        ArrayAdapter<String> adapter = (ArrayAdapter<String>) listaContactos.getAdapter();
        adapter.notifyDataSetChanged();
    }
}