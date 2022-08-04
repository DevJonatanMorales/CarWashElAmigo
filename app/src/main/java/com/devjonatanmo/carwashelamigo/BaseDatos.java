package com.devjonatanmo.carwashelamigo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;


public class BaseDatos extends SQLiteOpenHelper {

    public static final String db_CarWash = "CarWash";//mi base de datos...
    public static final int v = 1;//versionado de la bd

    String Tabla_placa ="t_placa";
    String Tabla_historial ="t_historial";

    String resgistro_placa = "CREATE TABLE IF NOT EXISTS " + Tabla_placa +
            "(id_placa INTEGER PRIMARY KEY AUTOINCREMENT," +
            "placa text, contador integer)";

    String historias_lavados = "CREATE TABLE IF NOT EXISTS " + Tabla_historial + " (" +
            "id_historial INTEGER PRIMARY KEY AUTOINCREMENT," +
            " fk_placa integer, " +
            " fecha data, " +
            " precio integer, " +
            " descuento integer, " +
            " total integer," +
            "FOREIGN KEY (fk_placa) REFERENCES t_placa (id_placa) ON DELETE CASCADE ON UPDATE NO ACTION)";

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

            lastId = db.insert("t_placa", null, contentValues);

        } catch (Exception ex) {
            ex.toString();
        }

        return lastId;
    }


    public Boolean InsertHistory (int fk_placa, String fecha, String precio, String descuento, String total) {

        SQLiteDatabase db = getWritableDatabase();

        boolean correcto = false;

        try {
            db.execSQL("INSERT INTO " + Tabla_historial + "(fk_placa, fecha, precio, descuento, total) VALUES (" + fk_placa + ", '" + fecha + "', '" + precio + "', '" + descuento + "', '" + total + "')");
            correcto = true;
        } catch (Exception ex) {
            ex.toString();
            correcto = false;
        } finally {
            db.close();
        }

        return correcto;
    }

    public Boolean  UpdateContador (int id, int contador) {

        SQLiteDatabase db = getWritableDatabase();

        boolean correcto = false;

        try {
            db.execSQL("UPDATE " + Tabla_placa + " SET contador = " + contador +  " WHERE id_placa= " + id);
            correcto = true;
        } catch (Exception ex) {
            ex.toString();
            correcto = false;
        } finally {
            db.close();
        }

        return correcto;
    }

    public Boolean ClearHistory(int id) {

        boolean correcto = false;

        SQLiteDatabase db = getWritableDatabase();

        try {
            db.execSQL("DELETE FROM " + Tabla_historial + " WHERE fk_placa = '" + id + "'");
            correcto = true;
        } catch (Exception ex) {
            ex.toString();
            correcto = false;
        } finally {
            db.close();
        }

        return correcto;
    }

    public Cursor MostrarHistorial(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT t_placa.placa, t_placa.contador, t_historial.fecha, t_historial.total FROM t_placa INNER JOIN t_historial ON t_placa.id_placa=t_historial.fk_placa", null);
        return cursor;
    }

    public Cursor BuscarHistorial(String buscar_fecha){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT t_placa.placa, t_placa.contador, t_historial.fecha, t_historial.total FROM t_placa INNER JOIN t_historial ON t_placa.id_placa=t_historial.fk_placa WHERE t_historial.fecha LIKE '%"+ buscar_fecha +"%'", null);
        return cursor;
    }



}
