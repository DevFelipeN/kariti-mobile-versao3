package com.example.kariti;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class BancoDados extends SQLiteOpenHelper {

    public static final String DBNAME = "data_base.db";
    public static Integer USER_ID;
    public static Integer ID_ESCOLA;
    public BancoDados(Context context) {
        super(context, "data_base", null, 27);
    }

    @Override
    public void onCreate(SQLiteDatabase data_base) {
        try {
            data_base.execSQL("create Table usuario( id_usuario INTEGER primary Key AUTOINCREMENT, nomeUsuario TEXT, email TEXT UNIQUE, password varchar(256))");
            //data_base.execSQL("create Table validacao_usuario( id_validacao INTEGER primary Key AUTOINCREMENT, id_usuario INT NOT NULL, codigo TEXT, data_expiracao TEXT)");
            data_base.execSQL("create Table escola( id_escola INTEGER PRIMARY KEY AUTOINCREMENT, nomeEscola TEXT, bairro TEXT, id_usuario INTEGER)");
            data_base.execSQL("create Table escolasDesativadas( id_scolDesativadas INTEGER PRIMARY KEY AUTOINCREMENT, nomeScolDesativada TEXT, bairro TEXT, id_usuario INTEGER)");
            data_base.execSQL("create Table aluno (id_aluno Integer PRIMARY KEY AUTOINCREMENT, nomeAluno TEXT, email TEXT, id_escola INTEGER)");
            data_base.execSQL("create Table turma (id_turma Integer PRIMARY KEY AUTOINCREMENT, nomeTurma TEXT, id_escola INTEGER)");
        }catch(Exception e){
            Log.e("Error data_base: ",e.getMessage());
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase data_base, int oldVersion, int newVersion) {
        try {
            data_base.execSQL("drop Table if exists usuario");
            data_base.execSQL("drop Table if exists escola");
            data_base.execSQL("drop Table if exists aluno");
            data_base.execSQL("drop Table if exists turma");
            data_base.execSQL("drop Table if exists escolasDesativadas");
            onCreate(data_base);
        }catch(Exception e){
            Log.e("Error data_base: ",e.getMessage());
        }

    }
    public Boolean insertData(String nome, String password, String email){
            SQLiteDatabase data_base = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("nomeUsuario", nome);
            contentValues.put("password", to256(password));
            contentValues.put("email", email);
            long inserir = data_base.insert("usuario", null, contentValues);
            if (inserir == -1) return false;
            else {return true;}
    }
    public Boolean inserirDadosEscola(String nomeEscola, String bairro){
        SQLiteDatabase data_base = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nomeEscola", nomeEscola);
        contentValues.put("bairro", bairro);
        contentValues.put("id_usuario", BancoDados.USER_ID);
        long inserir = data_base.insert("escola", null, contentValues);
        return inserir != -1;
    }

    public Boolean inserirTurma(String nomeTurma){
        SQLiteDatabase data_base = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nomeTurma", nomeTurma);
        contentValues.put("id_escola", BancoDados.ID_ESCOLA);
        long inserir = data_base.insert("turma", null, contentValues);
        return inserir != -1;
    }
    public Boolean inserirEscolaDesativada(String scolDesativada, String bairro){
        SQLiteDatabase data_base = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nomeScolDesativada", scolDesativada);
        contentValues.put("bairro", bairro);
        contentValues.put("id_usuario", BancoDados.USER_ID);
        long inserir = data_base.insert("escolasDesativadas", null, contentValues);
        return inserir != -1;
    }
    public Boolean inserirDadosAluno(String nomeAluno, String email){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nomeAluno", nomeAluno);
        contentValues.put("email", email);
        contentValues.put("id_escola", BancoDados.ID_ESCOLA);
        long inserir = database.insert("aluno", null, contentValues);
        return inserir != -1;
    }
    public Boolean deletarDasAtivadas(String id_escola){
        try {
            SQLiteDatabase data_base = this.getWritableDatabase();
            String deleta = "DELETE FROM escola WHERE id_escola=?";
            SQLiteStatement stmt = data_base.compileStatement(deleta);
            stmt.bindString(1, id_escola);
            stmt.executeUpdateDelete();
            data_base.close();
        }catch (Exception e){e.printStackTrace();}
        return true;
    }
    public Boolean deletarEscola(Integer id_scolDesativadas){
        try {
            SQLiteDatabase data_base = this.getWritableDatabase();
            String deleta = "DELETE FROM escolasDesativadas WHERE id_scolDesativadas=?";
            SQLiteStatement stmt = data_base.compileStatement(deleta);
            stmt.bindLong(1, id_scolDesativadas);
            stmt.executeUpdateDelete();
            data_base.close();
        }catch (Exception e){e.printStackTrace();}
        return true;
    }
    public Boolean deletarAluno(Integer id_aluno){
        try {
            SQLiteDatabase data_base = this.getWritableDatabase();
            String deleta = "DELETE FROM aluno WHERE id_aluno=?";
            SQLiteStatement stmt = data_base.compileStatement(deleta);
            stmt.bindLong(1, id_aluno);
            stmt.executeUpdateDelete();
            data_base.close();
        }catch (Exception e){e.printStackTrace();}
        return true;
    }
    public Boolean upadateSenha(String password, Integer id){
        try {
            SQLiteDatabase data_base = this.getWritableDatabase();
            String altera = "UPDATE usuario SET password=? WHERE id=?";
            SQLiteStatement stmt = data_base.compileStatement(altera);
            stmt.bindString(1, to256(password));
            stmt.bindLong(2, id);
            stmt.executeUpdateDelete();
            data_base.close();
        }catch (Exception e){e.printStackTrace();}
       return true;
    }
    public Boolean checkNome(String nome, String email) {
        SQLiteDatabase data_base = this.getWritableDatabase();
        Cursor cursor = data_base.rawQuery("Select * from usuario where nomeUsuario =? and email = ?", new String[]{nome, email});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }
    public Integer checkemail(String email) {
        SQLiteDatabase data_base = this.getWritableDatabase();
        Cursor cursor = data_base.rawQuery("Select * from usuario where email = ?", new String[]{email});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getInt(0);
    }
    public String pegaNome(String id_usuario) {
        SQLiteDatabase data_base = this.getWritableDatabase();
        Cursor cursor = data_base.rawQuery("Select * from usuario where id_usuario = ?", new String[]{id_usuario});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getString(1);
    }
    public String pegaEscola(String id) {
        SQLiteDatabase data_base = this.getWritableDatabase();
        Cursor cursor = data_base.rawQuery("Select * from escola where id_escola = ?", new String[]{id});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getString(1);
    }

    public String pegaBairro(String id) {
        SQLiteDatabase data_base = this.getWritableDatabase();
        Cursor cursor = data_base.rawQuery("Select * from escola where id_escola = ?", new String[]{id});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getString(2);
    }
    //Verifica se a senha Ligada ao email é a mesma informada
    public Integer checkemailpass(String email, String password){
        SQLiteDatabase data_base = this.getWritableDatabase();
        Cursor cursor = data_base.rawQuery("Select * from usuario where email =? and password = ?", new String[] {email, to256(password)});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getInt(0);
        }else
            return null;
    }
    public Boolean checkEscola(String nomeEscola){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM escola WHERE nomeEscola = ?", new String[]{nomeEscola});

        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }
    public Integer checkEscolaDesativada(String nomeScolDesativada){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM escolasDesativadas WHERE nomeScolDesativada = ?", new String[]{nomeScolDesativada});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getInt(0);
        }else
            return null;
    }
    public Boolean checkAluno(String nome){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM aluno WHERE nomeAluno = ?", new String[]{nome});

        if (cursor.getCount() > 0) return true;
        else return false;
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
}