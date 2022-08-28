package com.devjonatanmo.carwashelamigo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// Import neccessor namespace
import android.bluetooth.BluetoothAdapter;
import  android.bluetooth.BluetoothDevice;
import  android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;
import android.os.Handler;

import java.util.concurrent.RunnableFuture;
import java.util.logging.LogRecord;


public class Factura extends AppCompatActivity {
    /*
    BLUETOOTH
    */

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;
    private static final int REQUEST_ENABLE_BT = 1;

    OutputStream outputStream;
    InputStream inputStream;
    Thread thread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    public static final byte[] ESC_ALIGN_LEFT = new byte[] { 0x1b, 'a', 0x00 };
    public static final byte[] ESC_ALIGN_RIGHT = new byte[] { 0x1b, 'a', 0x02 };
    public static final byte[] ESC_ALIGN_CENTER = new byte[] { 0x1b, 'a', 0x01 };
    public static final byte[] ESC_CANCEL_BOLD = new byte[] { 0x1B, 0x45, 0 };

    // interfas
    TextView txtInfoPlaca, historial, txtTotal;
    EditText etxtPrecio, etxtDescuento;
    Button btnGuardar, btnImprimir;

    //var db
    BaseDatos objRegistro;
    int _id;
    String _accion;
    String _numPlaca;
    String _placaInfo;
    String _historial;
    int _contador = 1;
    String _fechaActual;
    String strPrecio;
    String strDescuento , strTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factura);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        etxtPrecio = findViewById(R.id.etxtPrecio);
        etxtDescuento = findViewById(R.id.etxtDescuento);

        txtInfoPlaca = findViewById(R.id.txtInfoPlaca);
        historial = findViewById(R.id.historial);
        txtTotal = findViewById(R.id.txtTotal);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnImprimir = findViewById(R.id.btnImprimir);

        /*==============================================================================================
        ======================================RECIBIMOS DATOS===========================================
        ==============================================================================================*/
        try {

            _id = getIntent().getExtras().getInt("idPlaca");
            _accion = getIntent().getStringExtra("accion");
            _numPlaca = getIntent().getStringExtra("numPlaca");
            _placaInfo = getIntent().getStringExtra("placaInfo");
            _historial = getIntent().getStringExtra("historial");
            _contador = getIntent().getExtras().getInt("contador");
            _fechaActual = getIntent().getStringExtra("nuencaFecha");

            txtInfoPlaca.setText(_placaInfo);
            historial.setText(_historial);

            if (_contador == 5) {
                LavadoGratis();
                FocusableEditText();
            }

        }catch (Exception ex) {
            Toast.makeText(getApplicationContext(),"Error al recibir datos: " + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }

        /*==============================================================================================
        =========================================BLUETOOTH==============================================
        ==============================================================================================*/
        /*
        try {

            if (bluetoothAdapter.isEnabled()) {

                if(bluetoothAdapter==null){
                    new AlertDialog.Builder(this)
                            .setTitle("No compatible")
                            .setMessage("El teléfono no es compatible con Bluetooth")
                            .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    System.exit(0);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    FindBluetoothDevice();
                    openBluetoothPrinter();
                }

            } else {
                new AlertDialog.Builder(this)
                        .setTitle("ADVERTENCIA")
                        .setMessage("Por favor active el Bluetooth")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Toast.makeText(getApplicationContext(), "Activando...", Toast.LENGTH_LONG).show();
                                    bluetoothAdapter.enable();
                                    FindBluetoothDevice();
                                    openBluetoothPrinter();
                                } catch (Exception ex) {

                                }
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getApplicationContext(), "Cancelado...", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "BLUETOOTH ERROR: " + ex.toString(), Toast.LENGTH_LONG).show();

        }
        */

        etxtPrecio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (etxtPrecio.getText().toString().isEmpty() == false &&
                            etxtDescuento.getText().toString().isEmpty() == false
                    ) {
                        CalcularPago();
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), "onTextChanged Error: " + ex.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etxtDescuento.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (etxtPrecio.getText().toString().isEmpty() == false &&
                            etxtDescuento.getText().toString().isEmpty() == false
                    ) {
                        CalcularPago();
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), "onTextChanged Error: " + ex.toString(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        /*==============================================================================================
        ============================================GUARDAR=============================================
        ==============================================================================================*/
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etxtPrecio.getText().toString().isEmpty() == false &&
                        etxtDescuento.getText().toString().isEmpty() == false
                ) {
                    if (strTotal != null) {
                        ProcesarDatos();
                    } else {
                        Toast.makeText(Factura.this, "Por favor complete los campos", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(Factura.this, "Por favor complete los campos", Toast.LENGTH_LONG).show();
                }

            }
        });

        /*==============================================================================================
        ============================================IMPRIMIR============================================
        ==============================================================================================*/
        btnImprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "En desarrollo XD", Toast.LENGTH_LONG).show();

                //verificamos si el bluetooth este encendido
                /*
                if (bluetoothAdapter.isEnabled()) {

                    try{
                        printData();

                    }catch (Exception ex){
                        Toast.makeText(getApplicationContext(), "Error: " + ex.toString(), Toast.LENGTH_LONG).show();
                    }

                } else {
                    //le pedimos que enciendad el bluetooth
                    new AlertDialog.Builder(Factura.this)
                            .setTitle("ADVERTENCIA")
                            .setMessage("Por favor active el Bluetooth")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        Toast.makeText(getApplicationContext(), "Activando...", Toast.LENGTH_LONG).show();
                                        bluetoothAdapter.enable();
                                        FindBluetoothDevice();
                                        openBluetoothPrinter();
                                    } catch (Exception ex) {

                                    }
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(getApplicationContext(), "Cancelado...", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
                */
            }
        });
    }

    /*==============================================================================================
    ======================================Desactivar EditText=======================================
    ==============================================================================================*/
    protected void FocusableEditText () {
        etxtPrecio.setFocusable(false);
        etxtDescuento.setFocusable(false);
    }

    /*==============================================================================================
    ======================================Lavado Gratis=============================================
    ==============================================================================================*/

    protected void  LavadoGratis() {
        txtTotal.setText("Total a pagar: lavado gratis" );
        etxtPrecio.setText("0.00");
        etxtDescuento.setText("0.00");

        strPrecio = "0.00";
        strDescuento = "0.00";
        strTotal = "lavado gratis";

    }

    /*==============================================================================================
    ======================================CALCULAR PAGO=============================================
    ==============================================================================================*/
    protected void CalcularPago() {
        float precio, descuento, decuentoFracion, totalDescuento, totalInt;

        try {
            descuento = Integer.parseInt(etxtDescuento.getText().toString());
            precio = Integer.parseInt(etxtPrecio.getText().toString());

            if (descuento >= 0 || precio > 0) {
                decuentoFracion = descuento / 100;
                totalDescuento = precio * decuentoFracion;
                totalInt = precio - totalDescuento;
                strTotal = String.valueOf(totalInt);
                txtTotal.setText("Total a pagar: $" + totalInt );
            }


        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Operacion Error: " + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /*==============================================================================================
    ======================================PROCESAMOS DATOS==========================================
    ==============================================================================================*/
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

    /*==============================================================================================
    ========================================CLEAR DATA==============================================
    ==============================================================================================*/
    protected void ClearData() {
        txtInfoPlaca.setText("========================");
        etxtPrecio.setText("");
        etxtDescuento.setText("");
        historial.setText("========================");
        txtTotal.setText("Total a pagar: $0.00");
    }

    /*==============================================================================================
    =====================================BuscarDispositivoBluetooth=================================
    ==============================================================================================*/
    protected void FindBluetoothDevice(){

        try{

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();

            if(pairedDevice.size()>0){
                for(BluetoothDevice pairedDev:pairedDevice){

                    // My Bluetoth printer name is BTP_F09F1A
                    if(pairedDev.getName().equals("BT-SPEAKER")){
                        bluetoothDevice=pairedDev;
                        Toast.makeText(getApplicationContext(),"Impresora Bluetooth adjunta: " + pairedDev.getName(), Toast.LENGTH_LONG).show();

                        break;
                    }
                }
                if(bluetoothDevice == null)
                {
                    Toast.makeText(getApplicationContext(), "No fue posible conectar con la impresora intente de nuevo", Toast.LENGTH_LONG).show();
                }
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    /*==============================================================================================
    =====================================comenzarListenData=========================================
    ==============================================================================================*/
    protected void beginListenData(){
        try{

            final Handler handler =new Handler();
            final byte delimiter=10;
            stopWorker =false;
            readBufferPosition=0;
            readBuffer = new byte[1024];

            thread=new Thread(new Runnable() {
                @Override
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker){
                        try{
                            int byteAvailable = inputStream.available();
                            if(byteAvailable>0){
                                byte[] packetByte = new byte[byteAvailable];
                                inputStream.read(packetByte);

                                for(int i=0; i<byteAvailable; i++){
                                    byte b = packetByte[i];
                                    if(b==delimiter){
                                        byte[] encodedByte = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer,0,
                                                encodedByte,0,
                                                encodedByte.length
                                        );
                                        final String data = new String(encodedByte,"US-ASCII");
                                        readBufferPosition=0;
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "Data: " + data, Toast.LENGTH_LONG).show();
                                                try {
                                                    printData();
                                                } catch (Exception ex) {

                                                }
                                                //lblPrinterName.setText(data);
                                            }
                                        });
                                    }else{
                                        readBuffer[readBufferPosition++]=b;
                                    }
                                }
                            }
                        }catch(Exception ex){
                            stopWorker=true;
                        }
                    }

                }
            });

            thread.start();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /*==============================================================================================
    ==========================================FACTURA===============================================
    ==============================================================================================*/
    void printData() throws  IOException{
        try{

            String Encabezado, titulo, precio, descuento, tottal, pie;
/*
            precio = "PRECIO: $" + strPrecio;
            descuento = "DESCUENTO: " + strDescuento + "%";
            tottal = "TOTAL: $" + strTotal;

            pie = "Gracias por su visita los esperamos pronto.\n";

            Encabezado = "Car Wash El Amigo";
            outputStream.write(ESC_ALIGN_CENTER);
            outputStream.write(ESC_CANCEL_BOLD);
            outputStream.write(Encabezado.getBytes());

            titulo = "Ticket de Control de clientes \ny historial de lavado.\n";
            outputStream.write(ESC_ALIGN_CENTER);
            outputStream.write(titulo.getBytes());

            outputStream.write(ESC_ALIGN_CENTER);
            outputStream.write(_numPlaca.getBytes());

            outputStream.write(ESC_ALIGN_CENTER);
            outputStream.write(_placaInfo.getBytes());

            outputStream.write(ESC_ALIGN_RIGHT);
            outputStream.write(precio.getBytes());
            outputStream.write(descuento.getBytes());
            outputStream.write(tottal.getBytes());

            outputStream.write(ESC_CANCEL_BOLD);
            outputStream.write(ESC_ALIGN_CENTER);
            outputStream.write(pie.getBytes());
*/
            Toast.makeText(getApplicationContext(), "Imprimiendo...", Toast.LENGTH_LONG).show();
            disconnectBT();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /*==============================================================================================
    ======================================OPEN BLUETOOTH============================================
    ==============================================================================================*/
    protected void openBluetoothPrinter() throws IOException{
        try{
            //Standard uuid from string //
            UUID uuidSting = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            bluetoothSocket=bluetoothDevice.createRfcommSocketToServiceRecord(uuidSting);
            bluetoothSocket.connect();
            outputStream=bluetoothSocket.getOutputStream();
            inputStream=bluetoothSocket.getInputStream();

            beginListenData();

        }catch (Exception ex){

        }
    }

    /*==============================================================================================
    ======================================CLOSE BLUETOOTH===========================================
    ==============================================================================================*/
    protected void disconnectBT() throws IOException{
        try {
            stopWorker=true;
            outputStream.close();
            inputStream.close();
            bluetoothSocket.close();
            Toast.makeText(getApplicationContext(),"Printer Disconnected.", Toast.LENGTH_LONG).show();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}