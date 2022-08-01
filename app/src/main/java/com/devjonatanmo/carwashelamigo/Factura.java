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
    BaseDatos objRegistro;
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

        try {
            btnGuardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    precio = etxtPrecio.getText().toString();
                    descuento = etxtDescuento.getText().toString();

                    if (total != null) {
                        ProcesarDatos();
                    } else {
                        Toast.makeText(Factura.this, "Por favor complete los campos", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),"Error al guardar: " + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }


    protected void ProcesarDatos(){
        objRegistro = new BaseDatos(getApplicationContext(), "", null, 1);

        long id;
        switch (_accion) {
            case "nuevo":
                _contador = 1;
                id = objRegistro.LastIdInsert(_numPlaca, _contador);

                _id = Math.toIntExact(id);
                if (_id > 0) {
                    String error = "";
                    error = objRegistro.InsertHistory(_id, _fechaActual, precio, descuento, total);
                    Toast.makeText(this, "ALERT - " +error, Toast.LENGTH_LONG).show();
                    etxtPrecio.setText("");
                    etxtDescuento.setText("");
                    txtTotal.setText("Total a pagar: $0.00");
/*
                    if (objRegistro.InsertHistory(_id, _fechaActual, precio, descuento, total) == true) {
                        Toast.makeText(this, "Guardado con exito", Toast.LENGTH_LONG).show();
                        etxtPrecio.setText("");
                        etxtDescuento.setText("");
                        txtTotal.setText("Total a pagar: $0.00");
                    } else {
                        Toast.makeText(this, "Error al guardar historial", Toast.LENGTH_LONG).show();
                    }*/

                } else {
                    Toast.makeText(this, "Error al guardar placa", Toast.LENGTH_LONG).show();
                }
                break;

            case "update":
                Toast.makeText(getApplicationContext(),"En desarrollo", Toast.LENGTH_LONG).show();
/*
                _contador += 1;
                if (objRegistro.UpdateContador(_id, _contador) == true) {

                    if (objRegistro.ClearHistory(_id) == true) {
                        Toast.makeText(this, "Guardado con exito", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Error al guardar", Toast.LENGTH_LONG).show();
                    }
                }*/
                break;

            case "reset":
                _contador = 0;
                Toast.makeText(getApplicationContext(),"En desarrollo", Toast.LENGTH_LONG).show();
                /*
                if (dbCarWash.UpdateContador(_id, _contador) == true) {

                    if (dbCarWash.ClearHistory(_id) == true) {
                        if (dbCarWash.InsertHistory(_id, _fechaActual, precio, descuento, total) == true) {
                            Toast.makeText(this, "Guardado con exito", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Error al guardar", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "Error al guardar", Toast.LENGTH_LONG).show();
                    }
                }*/
                break;
        }
    }
}