package com.devjonatanmo.carwashelamigo;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Historial extends AppCompatActivity {
    BaseDatos objRegistro;
    public Cursor misDatos;
    Button btnFiltrar;
    EditText editTextDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        btnFiltrar = findViewById(R.id.btnFiltrar);
        editTextDate = findViewById(R.id.editTextDate);

        MostrarHistorial();

        btnFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String buscarFecha;
                buscarFecha = editTextDate.getText().toString();

                if (buscarFecha == null) {
                    Toast.makeText(getApplicationContext(),"Por favor ingrese una fecha",
                            Toast.LENGTH_LONG).show();

                } else {
                    BuscarHistorial(buscarFecha);
                }
            }
        });
    }

    private void MostrarHistorial() {
        try{
            objRegistro = new BaseDatos(Historial.this, "", null, 1);
            misDatos = objRegistro.MostrarHistorial();

            if( misDatos.moveToFirst() ){
                ListView ltsAgenda = (ListView)findViewById(R.id.ltsAgenda);
                ArrayList<String> alContactos = new ArrayList<String>();
                ArrayAdapter<String> adContactos = new ArrayAdapter<String>
                        (this, android.R.layout.simple_list_item_1,alContactos);
                ltsAgenda.setAdapter(adContactos);
                do{
                    alContactos.add("Número de placa: " + misDatos.getString(0) + "\nfecha de lavado: " + misDatos.getString(2) + "\npago $: " + misDatos.getString(3));                }while( misDatos.moveToNext() );
                adContactos.notifyDataSetChanged();
                registerForContextMenu(ltsAgenda);//registra la lista
            }else{
                Toast.makeText(getApplicationContext(),"No tienes datos  que mostrar",
                        Toast.LENGTH_LONG).show();
            }

        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error de datos: "+
                    ex.getMessage().toString(),Toast.LENGTH_LONG).show();
        }
    }

    private void BuscarHistorial(String BuscarFecha) {
        try{
            objRegistro = new BaseDatos(Historial.this, "", null, 1);
            misDatos = objRegistro.BuscarHistorial(BuscarFecha);

            if( misDatos.moveToFirst() ){
                ListView ltsAgenda = (ListView)findViewById(R.id.ltsAgenda);
                ArrayList<String> alContactos = new ArrayList<String>();
                ArrayAdapter<String> adContactos = new ArrayAdapter<String>
                        (this, android.R.layout.simple_list_item_1,alContactos);
                ltsAgenda.setAdapter(adContactos);
                do{
                    alContactos.add("Número de placa: " + misDatos.getString(0) + "\nfecha de lavado: " + misDatos.getString(2) + "\npago $: " + misDatos.getString(3));                }while( misDatos.moveToNext() );
                adContactos.notifyDataSetChanged();
                registerForContextMenu(ltsAgenda);//registra la lista
            }else{
                Toast.makeText(getApplicationContext(),"No tienes datos  que mostrar",
                        Toast.LENGTH_LONG).show();
                MostrarHistorial();
            }

        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error de datos: "+
                    ex.getMessage().toString(),Toast.LENGTH_LONG).show();
        }
    }
}