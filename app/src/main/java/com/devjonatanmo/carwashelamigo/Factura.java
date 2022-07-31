package com.devjonatanmo.carwashelamigo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Factura extends AppCompatActivity {

    TextView txtInfoPlaca, txtTotal;
    EditText etxtPrecio, etxtDescuento;
    Button btnGuardar, btnImprimir, btnCalcular;

    //var db
    int _id;
    String _accion;
    String _numPlaca;
    int _contador = 1;
    String _fechaActual;
    String precio;
    String descuento , total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factura);

        etxtPrecio = findViewById(R.id.etxtPrecio);
        etxtDescuento = findViewById(R.id.etxtDescuento);

        txtInfoPlaca = findViewById(R.id.txtInfoPlaca);
        txtTotal = findViewById(R.id.txtTotal);

        btnCalcular = findViewById(R.id.btnCalcular);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnImprimir = findViewById(R.id.btnImprimir);

        try {
            txtInfoPlaca.setText(getIntent().getStringExtra("placaInfo"));

            _id = getIntent().getExtras().getInt("idPlaca");
            _accion = getIntent().getStringExtra("accion");
            _numPlaca = getIntent().getStringExtra("numPlaca");
            _contador = getIntent().getExtras().getInt("contador");
            _fechaActual = getIntent().getStringExtra("nuencaFecha");
        }catch (Exception ex) {
            Toast.makeText(getApplicationContext(),"Error al recibir datos: " + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }

        btnCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float precio, descuento, decuentoFracion, totalDescuento, totalInt;

                descuento = Integer.parseInt(etxtDescuento.getText().toString());
                decuentoFracion = descuento / 100;
                precio = Integer.parseInt(etxtPrecio.getText().toString());
                totalDescuento = precio * decuentoFracion;
                totalInt = precio - totalDescuento;
                total = String.valueOf(totalInt);
                txtTotal.setText("Total a pagar: " + totalInt );

            }
        });
    }
}