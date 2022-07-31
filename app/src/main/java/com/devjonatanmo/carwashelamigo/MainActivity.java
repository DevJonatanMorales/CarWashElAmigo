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
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    //dase de datos
    BaseDatos objRegistro;
    private Cursor fila;

    //activity
    Button BtnPlaca, BtnHistorial;

    String accion = "nuevo", placa, placaInfo, strDate;
    int idPlaca = 0, contador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BtnPlaca = findViewById(R.id.btnPlaca);
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        strDate = sdf.format(c.getTime());

        try {
            BaseDatos root = new BaseDatos (this,"registro",null,1);
            SQLiteDatabase db = root.getWritableDatabase();

            fila = db.rawQuery("SELECT idplaca, placa, contador FROM t_registro_placa WHERE placa='"+ numPlaca +"'", null);

            if (fila.moveToFirst()==true) {
                while(fila.moveToFirst()==true){
                    idPlaca = fila.getInt(0);
                    placa = fila.getString(1);
                    contador = fila.getInt(2);

                    if (numPlaca.equals(placa)){
                        Intent ver = new Intent(this, Factura.class);
                        startActivity(ver);

                        if (contador == 5) {
                            accion = "reset";
                        } else {
                            accion = "update";
                        }

                    }else{
                        Toast.makeText(getApplicationContext(),"Cometio un error intente de nuevo",Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                placaInfo = "Número de placa: " + numPlaca + "\n" + //agregamos la descripcion
                    "placa sin historial \nfecha: " + strDate;
            }

            EnviarDatos();

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),"Error: " + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void EnviarDatos () {
        Intent intent = new Intent( MainActivity.this, Factura.class);

        intent.putExtra("numPlaca", placa);
        intent.putExtra("idPlaca", idPlaca);
        intent.putExtra("placaInfo", placaInfo);
        intent.putExtra("accion", accion);
        intent.putExtra("contador", contador);
        intent.putExtra("nuencaFecha", strDate);

        startActivity(intent);
    }
}