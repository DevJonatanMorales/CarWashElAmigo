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
    String strPrecio;
    String strDescuento , strTotal;

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

                if (etxtPrecio.getText().toString().isEmpty() == false &&
                    etxtDescuento.getText().toString().isEmpty() == false
                ) {
                    CalcularPago();
                } else {
                    Toast.makeText(Factura.this, "Por favor complete los campos", Toast.LENGTH_LONG).show();
                }

            }
        });


        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strPrecio = etxtPrecio.getText().toString();
                strDescuento = etxtDescuento.getText().toString();

                if (strTotal != null) {
                    ProcesarDatos();
                } else {
                    Toast.makeText(Factura.this, "Por favor complete los campos", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected void CalcularPago() {
        float precio, descuento, decuentoFracion, totalDescuento, totalInt;
        descuento = Integer.parseInt(etxtDescuento.getText().toString());
        precio = Integer.parseInt(etxtPrecio.getText().toString());

        if (_contador == 5) {
            txtTotal.setText("Total a pagar: lavado gratis" );
            etxtPrecio.setText("0.00");
            etxtDescuento.setText("0.00");

            strPrecio = "0.00";
            strDescuento = "0.00";
            strTotal = "lavado gratis";
        }else if (descuento >= 0 || precio > 0) {
            decuentoFracion = descuento / 100;
            totalDescuento = precio * decuentoFracion;
            totalInt = precio - totalDescuento;
            strTotal = String.valueOf(totalInt);
            txtTotal.setText("Total a pagar: " + totalInt );
        }
    }

    protected void ProcesarDatos() {
        objRegistro = new BaseDatos(getApplicationContext(), "", null, 1);

        long id;
        switch (_accion) {
            case "nuevo":
                _contador = 1;

                id = objRegistro.LastIdInsert(_numPlaca, _contador);

                _id = Math.toIntExact(id);
                if (_id > 0) {

                    if (objRegistro.InsertHistory(_id, _fechaActual, strPrecio, strDescuento, strTotal) == true) {
                        ClearData();
                        Toast.makeText(this, "Guardado con exito", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(this, "Error al guardar historial", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(this, "Error al guardar placa", Toast.LENGTH_LONG).show();
                }
                break;


            case "update":

                _contador += 1;

                if (objRegistro.UpdateContador(_id, _contador) == true) {

                    if (objRegistro.InsertHistory(_id, _fechaActual, strPrecio, strDescuento, strTotal) == true) {
                        ClearData();
                        Toast.makeText(this, "Guardado con exito", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(this, "Error al guardar historial", Toast.LENGTH_LONG).show();
                    }
                }

                break;

            case "reset":

                if (objRegistro.UpdateContador(_id, 0) == true) {

                    if (objRegistro.ClearHistory(_id) == true) {
                        if (objRegistro.InsertHistory(_id, _fechaActual, strPrecio, strDescuento, strTotal) == true) {
                            Toast.makeText(this, "Guardado con exito", Toast.LENGTH_LONG).show();
                            ClearData();
                        } else {
                            Toast.makeText(this, "Error al guardar", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "Error al guardar", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    protected void ClearData() {
        txtInfoPlaca.setText("");
        etxtPrecio.setText("");
        etxtDescuento.setText("");
        txtTotal.setText("Total a pagar: $0.00");
    }
}