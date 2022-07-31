package com.devjonatanmo.carwashelamigo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class BaseDatos extends SQLiteOpenHelper {

    public static final String db_CarWash = "registro";//mi base de datos...
    public static final int v = 1;//versionado de la bd

    String resgistro_placa = "CREATE TABLE IF NOT EXISTS t_registro_placa " +
            "(idplaca INTEGER PRIMARY KEY AUTOINCREMENT," +
            "placa text, contador integer)";

    String historias_lavados = "CREATE TABLE IF NOT EXISTS t_historial_lavados (" +
            " fk_placa integer, " +
            " fecha data, " +
            " precio integer, " +
            " descuento integer, " +
            " total integer," +
            "FOREIGN KEY (fk_placa) REFERENCES resgistro_placa (idplaca) ON DELETE CASCADE ON UPDATE NO ACTION)";

    public BaseDatos(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, db_CarWash, factory, v);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(resgistro_placa);
        sqLiteDatabase.execSQL(historias_lavados);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long LastIdInsert(String numPlaca, int contador) {

        long lastId = 0;

        try {
            SQLiteDatabase db = getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("placa", numPlaca);
            contentValues.put("contador", contador);

            lastId = db.insert("t_registro_placa", null, contentValues);

        } catch (Exception ex) {
            ex.toString();
        }

        return lastId;
    }


    public String InsertHistory (int fk_placa, String fecha, String precio, String descuento, String total) {

        SQLiteDatabase db = getWritableDatabase();

        boolean correcto = false;
        String alert = "sin errores";

        try {
            db.execSQL("INSERT INTO t_historial_lavados(fk_placa, fecha, precio, descuento, total) VALUES (" + fk_placa + ", '" + fecha + "', '" + precio + "', '" + descuento + "', '" + total + "')");
            correcto = true;
        } catch (Exception ex) {
            alert = ex.toString();
            correcto = false;
        } finally {
            db.close();
        }

        return alert;
    }

    public String  UpdateContador (int id, int contador) {

        SQLiteDatabase db = getWritableDatabase();

        boolean correcto = false;
        String alert = "sin errores";

        try {
            db.execSQL("UPDATE t_resgistro_placa SET contador = '" + contador +  "' WHERE id='" + id + "' ");
            correcto = true;
        } catch (Exception ex) {
            alert = ex.toString();
            correcto = false;
        } finally {
            db.close();
        }

        return alert;
    }

    public String ClearHistory(int id) {

        boolean correcto = false;
        String alert = "sin errores";

        SQLiteDatabase db = getWritableDatabase();

        try {
            db.execSQL("DELETE FROM t_historias_lavados WHERE fk_placa = '" + id + "'");
            correcto = true;
        } catch (Exception ex) {
            alert = ex.toString();
            correcto = false;
        } finally {
            db.close();
        }

        return alert;
    }
}
