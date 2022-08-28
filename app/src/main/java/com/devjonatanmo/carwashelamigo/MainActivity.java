package com.devjonatanmo.carwashelamigo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    //dase de datos
    private Cursor fila;

    //activity
    Button BtnPlaca, BtnManual, BtnHistorial;

    String accion = "nuevo", placa, placaInfo, historial, strDate;
    int idPlaca = 0, contador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BtnPlaca = findViewById(R.id.btnPlaca);
        BtnManual = findViewById(R.id.btnManual);
        BtnHistorial = findViewById(R.id.btnHistorial);

        //escaneamos la placa
        BtnPlaca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IntentIntegrator integrator = new IntentIntegrator( MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Lector - Car Wash El Amigo");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.initiateScan();

            }
        });

        BtnManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EscaneoManual.class);
                startActivity(intent);
            }
        });

        //Hitorial...
        BtnHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Historial.class);
                startActivity(intent);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Lectora cancelada", Toast.LENGTH_LONG).show();
            } else {
                BuscarPlaca(result.getContents());

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //buscamos la placa en la bd
    private void BuscarPlaca(String numPlaca) {
        //obtenemos la fecha
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d HH:mm");
        strDate = sdf.format(c.getTime());

        BaseDatos root = new BaseDatos (MainActivity.this,"CarWash",null,1);
        SQLiteDatabase BaseDatos = root.getWritableDatabase();

        try {

            //fila = BaseDatos.rawQuery("SELECT id_placa, placa, contador, fecha FROM t_placa INNER JOIN t_historial ON t_placa.id_placa=t_historial.fk_placa WHERE t_placa.placa='"+ numPlaca +"'", null);
            fila = BaseDatos.rawQuery("SELECT id_placa, placa, contador, fecha " +
                    "FROM t_placa INNER JOIN t_historial ON t_placa.id_placa=t_historial.fk_placa " +
                    "WHERE t_placa.placa='"+ numPlaca +"'", null);
            if (fila.moveToFirst() == true) {
                idPlaca = fila.getInt(0);
                placa = fila.getString(1);
                contador = fila.getInt(2);

                if (numPlaca.equals(placa)) {

                    if (contador == 5) {
                        accion = "reset";
                    } else {
                        accion = "update";
                    }

                    int item = 0;

                    for (int i = 1; i <= fila.getCount(); i++ ){
                        if (item == 0) {
                            historial = "fecha de lavado: " + fila.getString(3);
                            item = 1;

                        }else if (fila.moveToNext()) {
                            historial += "\nfecha de lavado: " + fila.getString(3);

                        }

                    }
                    placaInfo = "Número de placa: " + placa + "\nfecha: " + strDate;

                }

            } else {
                placa = numPlaca;
                placaInfo = "Número de placa: " + numPlaca + //agregamos la descripcion
                        "\nfecha: " + strDate;
                historial = "Sin historial";
            }

            EnviarDatos();

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),"Error: " + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void EnviarDatos () {
        try {
            Intent intent = new Intent( MainActivity.this, Factura.class);

            intent.putExtra("numPlaca", placa);
            intent.putExtra("idPlaca", idPlaca);
            intent.putExtra("placaInfo", placaInfo);
            intent.putExtra("historial", historial);
            intent.putExtra("accion", accion);
            intent.putExtra("contador", contador);
            intent.putExtra("nuencaFecha", strDate);

            startActivity(intent);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),"Error al enviar: " + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }
}