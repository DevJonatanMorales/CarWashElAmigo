package com.devjonatanmo.carwashelamigo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Historial extends AppCompatActivity {
    BaseDatos objRegistro;
    public Cursor misDatos;
    Button btnFiltrar;
    EditText editTextDate;
    String Fecha;

    RecyclerView lista;
    ArrayList<Registro> listaArrayRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        btnFiltrar = findViewById(R.id.btnFiltrar);
        editTextDate = findViewById(R.id.editTextDate);
        lista = findViewById(R.id.lista);

        try {

            lista.setLayoutManager(new LinearLayoutManager(this));
            objRegistro = new BaseDatos(Historial.this, "", null, 1);

            listaArrayRegistro= new ArrayList<>();

            if (objRegistro.mostrar_registro().isEmpty()) {
                Toast.makeText(getApplicationContext(), "No hay datos que mostrar", Toast.LENGTH_LONG).show();

            } else {
                ListaRegistroAdapter listaRegistroAdapter = new ListaRegistroAdapter(objRegistro.mostrar_registro());
                lista.setAdapter(listaRegistroAdapter);
            }



        } catch (Exception ex) {

            Toast.makeText(getApplicationContext(),"Error: " + ex.toString(), Toast.LENGTH_LONG).show();
        }


        btnFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String fecha = editTextDate.getText().toString();

                    lista.setLayoutManager(new LinearLayoutManager(Historial.this));
                    objRegistro = new BaseDatos(Historial.this, "", null, 1);

                    listaArrayRegistro= new ArrayList<>();


                    if (objRegistro.filtar_registro(fecha).isEmpty()) {
                        Toast.makeText(getApplicationContext(), "No hay datos que mostrar", Toast.LENGTH_LONG).show();

                    } else {
                        ListaRegistroAdapter listaRegistroAdapter = new ListaRegistroAdapter(objRegistro.filtar_registro(fecha));
                        lista.setAdapter(listaRegistroAdapter);

                    }


                } catch (Exception exception) {
                    Toast.makeText(getApplicationContext(), "Error XD " + exception.toString(), Toast.LENGTH_LONG);
                }

            }
        });
    }
}