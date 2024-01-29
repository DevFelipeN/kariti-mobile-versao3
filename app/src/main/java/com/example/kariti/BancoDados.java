package com.example.kariti;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class BancoDados extends SQLiteOpenHelper {

    public static final String DBNAME = "data_base.db";

    public BancoDados(Context context) {
        super(context, "data_base", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase data_base) {
        data_base.execSQL("create Table usuario(user TEXT primary Key, password TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase data_base, int i, int i1) {
        data_base.execSQL("drop Table if exists usuario");

    }
    //------Metodo para inserir dados no Banco de Dados-----------
    public Boolean insertData(String user, String password){
        SQLiteDatabase data_base = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("user",user);
        contentValues.put("password",password);
        long inserir = data_base.insert("usuario", null, contentValues);
        if(inserir==-1) return false;
        else
            return true;
    }
    //--- Verificando se Usuario que esta sendo informado já existe na tabela------------
    public Boolean checkuser(String user) {
        SQLiteDatabase data_base = this.getWritableDatabase();
        Cursor cursor = data_base.rawQuery("Select * from usuario where user =?", new String[]{user});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }
    //------Verifica se a senha Ligada ao Nome é mesma informada-----------
    public Boolean checkuserpassword(String user, String password){
        SQLiteDatabase data_base = this.getWritableDatabase();
        Cursor cursor = data_base.rawQuery("Select * from usuario where user =? and password = ?", new String[] {user, password});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }
}