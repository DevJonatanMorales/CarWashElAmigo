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

                Intent intent = new Intent(MainActivity.this, EscanearPlaca.class);
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
}