package com.devjonatanmo.carwashelamigo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EscanearPlaca extends AppCompatActivity {
    Button btnAutomatico;
    Button btnManual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escanear_placa);

        btnAutomatico = findViewById(R.id.btnAutomatico);
        btnManual = findViewById(R.id.btnManual);

        btnAutomatico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(EscanearPlaca.this, EscaneoAuntomatico.class);
                startActivity(intent);

            }
        });

        btnManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(EscanearPlaca.this, EscaneoManual.class);
                startActivity(intent);

            }
        });
    }
}