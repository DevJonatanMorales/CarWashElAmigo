package com.devjonatanmo.carwashelamigo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Historial extends AppCompatActivity {
    BaseDatos objRegistro;
    EditText etPlannedDate;
    TextView txt_total;
    String Fecha_actual;

    RecyclerView lista;
    ArrayList<Registro> listaArrayRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        etPlannedDate = findViewById(R.id.etPlannedDate);
        txt_total = findViewById(R.id.txt_total);
        lista = findViewById(R.id.lista);


        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
            Fecha_actual = sdf.format(c.getTime());

            etPlannedDate.setText(Fecha_actual);

            lista.setLayoutManager(new LinearLayoutManager(this));
            objRegistro = new BaseDatos(Historial.this, "", null, 1);

            listaArrayRegistro= new ArrayList<>();

            if (objRegistro.filtar_registro(Fecha_actual).isEmpty()) {
                Toast.makeText(getApplicationContext(), "No hay datos que mostrar", Toast.LENGTH_LONG).show();
                txt_total.setText("Total en lavados: $0.00" +
                        "\nLavados gratis: 0");

            } else {
                ListaRegistroAdapter listaRegistroAdapter = new ListaRegistroAdapter(objRegistro.filtar_registro(Fecha_actual));
                lista.setAdapter(listaRegistroAdapter);

                txt_total.setText("Total en lavados: $" + objRegistro.total_lavados(Fecha_actual) +
                        "\nLavados gratis: " + objRegistro.CountLavadoGratis(Fecha_actual));
            }



        } catch (Exception ex) {

            Toast.makeText(getApplicationContext(),"Error: " + ex.toString(), Toast.LENGTH_LONG).show();
        }

        etPlannedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        etPlannedDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String fecha = etPlannedDate.getText().toString();

                if(fecha.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Seleccione una fecha", Toast.LENGTH_LONG).show();
                } else {
                    lista.setLayoutManager(new LinearLayoutManager(Historial.this));
                    objRegistro = new BaseDatos(Historial.this, "", null, 1);

                    listaArrayRegistro= new ArrayList<>();

                    if (objRegistro.filtar_registro(fecha).isEmpty()) {
                        Toast.makeText(getApplicationContext(), "No hay datos que mostrar", Toast.LENGTH_SHORT).show();

                    } else {

                        txt_total.setText("Total en lavados: $" + objRegistro.total_lavados(fecha) +
                                "\nLavados gratis: " + objRegistro.CountLavadoGratis(fecha));
                        ListaRegistroAdapter listaRegistroAdapter = new ListaRegistroAdapter(objRegistro.filtar_registro(fecha));
                        lista.setAdapter(listaRegistroAdapter);

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void showDatePickerDialog() {

        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                // +1 because January is zero
                final String selectedDate = year + "-" + (month+1) + "-" + day;
                etPlannedDate.setText(selectedDate);

            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");

    }
}