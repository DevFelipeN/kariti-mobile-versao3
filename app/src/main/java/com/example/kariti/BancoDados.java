package com.example.kariti;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class BancoDados extends SQLiteOpenHelper {

    public static final String DBNAME = "data_base.db";

    public BancoDados(Context context) {
        super(context, "data_base", null, 9);
    }

    @Override
    public void onCreate(SQLiteDatabase data_base) {
        try {
            data_base.execSQL("create Table usuario( id INTEGER primary Key AUTOINCREMENT, user TEXT, email TEXT UNIQUE, password varchar(256))");
            data_base.execSQL("create Table validacao_usuario( id INTEGER primary Key AUTOINCREMENT, id_usuario INT NOT NULL, codigo TEXT, data_expiracao TEXT)");
            data_base.execSQL("create Table escola( id INTEGER PRIMARY KEY AUTOINCREMENT, nomeEscola TEXT, bairr0 TEXT)");
        }catch(Exception e){
            Log.e("Error data_base: ",e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase data_base, int oldVersion, int newVersion) {
        try {
            data_base.execSQL("drop Table if exists usuario");
            data_base.execSQL("drop Table if exists escola");
            onCreate(data_base);
        }catch(Exception e){
            Log.e("Error data_base: ",e.getMessage());
        }

    }
    //Metodo para inserir dados no Banco de Dados
    public Boolean insertData(String user, String password, String email){
            SQLiteDatabase data_base = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("user", user);
            contentValues.put("password", to256(password));
            contentValues.put("email", email);
            long inserir = data_base.insert("usuario", null, contentValues);
            if (inserir == -1) return false;
            else {
                return true;
            }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


    public String to256(String text){
        try {

            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hashbytes = digest.digest(
                    text.getBytes(StandardCharsets.UTF_8));
            String sha3Hex = bytesToHex(hashbytes);
            return sha3Hex;
        }catch(Exception e){
            return "ERROR";
        }
    }

    public Boolean checkuser(String user) {
        SQLiteDatabase data_base = this.getWritableDatabase();
        Cursor cursor = data_base.rawQuery("Select * from usuario where user =?", new String[]{user});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }

    //Verificando se Usuario que esta sendo informado já existe na tabela
    public Boolean checkemail(String email) {
        SQLiteDatabase data_base = this.getWritableDatabase();
        Cursor cursor = data_base.rawQuery("Select * from usuario where email =?", new String[]{email});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }
    //Verifica se a senha Ligada ao email é a mesma informada
    public Boolean checkemailpass(String email, String password){
        SQLiteDatabase data_base = this.getWritableDatabase();
        Cursor cursor = data_base.rawQuery("Select * from usuario where email =? and password = ?", new String[] {email, to256(password)});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }
}