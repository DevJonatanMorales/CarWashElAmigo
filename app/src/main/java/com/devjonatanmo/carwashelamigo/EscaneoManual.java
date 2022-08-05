package com.devjonatanmo.carwashelamigo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EscaneoManual extends AppCompatActivity {

    EditText txtPlaca;
    Button btnPlaca;

    //dase de datos
    private Cursor fila;

    String accion = "nuevo", placa, placaInfo, strDate;
    int idPlaca = 0, contador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escaneo_manual);

        txtPlaca = findViewById(R.id.txtPlaca);
        btnPlaca = findViewById(R.id.btnPlaca);

        btnPlaca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numPlaca;

                numPlaca = txtPlaca.getText().toString();
                if (numPlaca.isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Por favor ingrese un número de placa", Toast.LENGTH_LONG).show();
                } else {
                    BuscarPlaca(numPlaca);

                }
            }
        });


    }


    //buscamos la placa en la bd
    private void BuscarPlaca(String numPlaca) {
        //obtenemos la fecha
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        strDate = sdf.format(c.getTime());

        BaseDatos root = new BaseDatos (EscaneoManual.this,"CarWash",null,1);
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

                    String historial = "";
                    int item = 0;

                    for (int i = 1; i <= fila.getCount(); i++ ){
                        if (item == 0) {
                            historial += "\nfecha de lavado: " + fila.getString(3);
                            item = 1;

                        }else if (fila.moveToNext()) {
                            historial += "\nfecha de lavado: " + fila.getString(3);

                        }

                    }


                    placaInfo = "Número de placa: " + placa + "\nfecha: " + strDate + "\nhistorial: " + historial;

                }

            } else {
                placa = numPlaca;
                placaInfo = "Número de placa: " + numPlaca + "\n" + //agregamos la descripcion
                        "placa sin historial \nfecha: " + strDate;
            }

            EnviarDatos();

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),"Error: " + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void EnviarDatos () {
        try {
            Intent intent = new Intent( EscaneoManual.this, Factura.class);

            intent.putExtra("numPlaca", placa);
            intent.putExtra("idPlaca", idPlaca);
            intent.putExtra("placaInfo", placaInfo);
            intent.putExtra("accion", accion);
            intent.putExtra("contador", contador);
            intent.putExtra("nuencaFecha", strDate);

            startActivity(intent);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),"Error al enviar: " + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }
}