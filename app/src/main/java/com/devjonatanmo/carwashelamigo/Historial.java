package com.devjonatanmo.carwashelamigo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Historial extends AppCompatActivity {
    BaseDatos objRegistro;
    public Cursor misDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        obtenerDatos();
    }


    private void obtenerDatos() {
        try{
            objRegistro = new BaseDatos(Historial.this, "", null, 1);
            misDatos = objRegistro.ObtenerHistorial();

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
}