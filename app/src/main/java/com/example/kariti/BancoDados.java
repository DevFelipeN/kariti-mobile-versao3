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
import java.util.ArrayList;
import java.util.List;
public class BancoDados extends SQLiteOpenHelper {
    public static final String DBNAME = "base_dados.db";
    public static Integer USER_ID;
    public static Integer ID_ESCOLA;
    public BancoDados(Context context) {
        super(context, "base_dados", null, 13);
    }
    @Override
    public void onCreate(SQLiteDatabase base_dados) {
        try {
            base_dados.execSQL("create Table usuario( id_usuario INTEGER primary Key AUTOINCREMENT, nomeUsuario TEXT, email TEXT UNIQUE, password varchar(256))");
            base_dados.execSQL("create Table validacao_usuario( id_validacao INTEGER primary Key AUTOINCREMENT, id_usuario INT NOT NULL, codigo TEXT, data_expiracao TEXT)");
            base_dados.execSQL("create Table escola( id_escola INTEGER PRIMARY KEY AUTOINCREMENT, nomeEscola TEXT, bairro TEXT, id_usuario INTEGER)");
            base_dados.execSQL("create Table escolasDesativadas( id_scolDesativadas INTEGER PRIMARY KEY AUTOINCREMENT, nomeScolDesativada TEXT, bairro TEXT, id_usuario INTEGER)");
            base_dados.execSQL("create Table aluno (id_aluno Integer PRIMARY KEY AUTOINCREMENT, nomeAluno TEXT, email TEXT, n Integer, id_escola INTEGER)");
            base_dados.execSQL("create Table turma (id_turma Integer PRIMARY KEY AUTOINCREMENT, id_escola INTEGER, nomeTurma TEXT, qtdAnonimos Integer)");
            base_dados.execSQL("create Table alunosTurma (id_turma Integer, id_aluno Integer)");
            base_dados.execSQL("create Table prova (id_prova Integer PRIMARY KEY AUTOINCREMENT, nomeProva TEXT, dataProva TEXT, qtdQuestoes Integer, qtdAlternativas Interger, id_escola INTEGER, id_turma Integer)");
            base_dados.execSQL("create Table gabarito (id_gabarito Integer PRIMARY KEY AUTOINCREMENT, id_prova Integer, questao Integer, resposta Integer, nota Integer)");
            base_dados.execSQL("create Table resultadoCorrecao (id_resultado Integer PRIMARY KEY AUTOINCREMENT, id_prova Integer, id_aluno Integer, acertos Integer, nota Integer)");
            base_dados.execSQL("create Table galeria(id INTEGER PRIMARY KEY AUTOINCREMENT, foto BLOB)");
        }catch(Exception e){
            Log.e("Error data_base: ",e.getMessage());
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase data_base, int oldVersion, int newVersion) {
        try {
            data_base.execSQL("drop Table if exists usuario");
            data_base.execSQL("drop Table if exists validacao_usuario");
            data_base.execSQL("drop Table if exists escola");
            data_base.execSQL("drop Table if exists aluno");
            data_base.execSQL("drop Table if exists turma");
            data_base.execSQL("drop Table if exists prova");
            data_base.execSQL("drop Table if exists escolasDesativadas");
            data_base.execSQL("drop Table if exists gabarito");
            data_base.execSQL("drop Table if exists galeria");
            data_base.execSQL("drop Table if exists alunosTurma");
            data_base.execSQL("drop Table if exists resultadoCorrecao");
            onCreate(data_base);
        }catch(Exception e){
            Log.e("Error base_dados: ",e.getMessage());
        }

    }
    public Boolean insertData(String nome, String password, String email){
            SQLiteDatabase base_dados = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("nomeUsuario", nome);
            contentValues.put("password", to256(password));
            contentValues.put("email", email);
            long inserir = base_dados.insert("usuario", null, contentValues);
            if (inserir == -1) return false;
            else {return true;}
    }
    public Boolean inserirDadosEscola(String nomeEscola, String bairro){
        SQLiteDatabase base_dados = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nomeEscola", nomeEscola);
        contentValues.put("bairro", bairro);
        contentValues.put("id_usuario", BancoDados.USER_ID);
        long inserir = base_dados.insert("escola", null, contentValues);
        return inserir != -1;
    }
    public Boolean inserirTurma(String nomeTurma, Integer an){
        SQLiteDatabase base_dados = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nomeTurma", nomeTurma);
        contentValues.put("qtdAnonimos", an);
        contentValues.put("id_escola", BancoDados.ID_ESCOLA);
        long inserir = base_dados.insert("turma", null, contentValues);
        return inserir != -1;
    }
    public Boolean inserirAlunosNaTurma(Integer id_turma, Integer id_aluno){
        SQLiteDatabase base_dados = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id_turma", id_turma);
        contentValues.put("id_aluno", id_aluno);
        long inserir = base_dados.insert("alunosTurma", null, contentValues);
        return inserir != -1;
    }
    public Boolean inserirResultCorrecao(Integer id_prova, Integer id_aluno, Integer acertos, Integer nota){
        SQLiteDatabase base_dados = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id_prova", id_prova);
        contentValues.put("id_aluno", id_aluno);
        contentValues.put("acertos", acertos);
        contentValues.put("nota", nota);
        long inserir = base_dados.insert("resultadoCorrecao", null, contentValues);
        return inserir != -1;
    }
    public Boolean inserirProva(String nomeProva, String dataProva, Integer qtdQuestoes, Integer qtdAlternativas, Integer id_turma){
        SQLiteDatabase base_dados = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nomeProva", nomeProva);
        contentValues.put("dataProva", dataProva);
        contentValues.put("qtdQuestoes", qtdQuestoes);
        contentValues.put("qtdAlternativas", qtdAlternativas);
        contentValues.put("id_escola", BancoDados.ID_ESCOLA);
        contentValues.put("id_turma", id_turma);
        long inserir = base_dados.insert("prova", null, contentValues);
        return inserir != -1;
    }
    public Boolean inserirGabarito(Integer id_prova, Integer questao, Integer resposta, Integer nota){
        SQLiteDatabase base_dados = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id_prova", id_prova);
        contentValues.put("questao", questao);
        contentValues.put("resposta", resposta);
        contentValues.put("nota", nota);
        long inserir = base_dados.insert("gabarito", null, contentValues);
        return inserir != -1;
    }
    public Boolean inserirEscolaDesativada(String scolDesativada, String bairro){
        SQLiteDatabase base_dados = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nomeScolDesativada", scolDesativada);
        contentValues.put("bairro", bairro);
        contentValues.put("id_usuario", BancoDados.USER_ID);
        long inserir = base_dados.insert("escolasDesativadas", null, contentValues);
        return inserir != -1;
    }
    public Boolean inserirDadosAluno(String nomeAluno, String email){
        SQLiteDatabase base_dados = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nomeAluno", nomeAluno);
        contentValues.put("email", email);
        contentValues.put("n", 1);
        contentValues.put("id_escola", BancoDados.ID_ESCOLA);
        long inserir = base_dados.insert("aluno", null, contentValues);
        return inserir != -1;
    }
    public Boolean inserirDadosAlunoSemail(String nomeAluno){
        SQLiteDatabase base_dados = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nomeAluno", nomeAluno);
        contentValues.put("n", 1);
        contentValues.put("id_escola", BancoDados.ID_ESCOLA);
        long inserir = base_dados.insert("aluno", null, contentValues);
        return inserir != -1;
    }
    public Boolean inserirAnonimos(String anonimo){
        SQLiteDatabase base_dados = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nomeAluno", anonimo);
        contentValues.put("n", 0);
        contentValues.put("id_escola", BancoDados.ID_ESCOLA);
        long inserir = base_dados.insert("aluno", null, contentValues);
        return inserir != -1;
    }
    public Boolean deletarDasAtivadas(String id_escola){
        try {
            SQLiteDatabase base_dados = this.getWritableDatabase();
            String deleta = "DELETE FROM escola WHERE id_escola=?";
            SQLiteStatement stmt = base_dados.compileStatement(deleta);
            stmt.bindString(1, id_escola);
            stmt.executeUpdateDelete();
            base_dados.close();
        }catch (Exception e){e.printStackTrace();}
        return true;
    }
    public Boolean deletarEscola(Integer id_scolDesativadas){
        try {
            SQLiteDatabase base_dados= this.getWritableDatabase();
            String deleta = "DELETE FROM escolasDesativadas WHERE id_scolDesativadas=?";
            SQLiteStatement stmt = base_dados.compileStatement(deleta);
            stmt.bindLong(1, id_scolDesativadas);
            stmt.executeUpdateDelete();
            base_dados.close();
        }catch (Exception e){e.printStackTrace();}
        return true;
    }
    public Boolean deletarAluno(Integer id_aluno){
        try {
            SQLiteDatabase base_dados = this.getWritableDatabase();
            String deleta = "DELETE FROM aluno WHERE id_aluno=?";
            SQLiteStatement stmt = base_dados.compileStatement(deleta);
            stmt.bindLong(1, id_aluno);
            stmt.executeUpdateDelete();
            base_dados.close();
        }catch (Exception e){e.printStackTrace();}
        return true;
    }
    public Boolean deletarTurma(String turma){
        try {
            SQLiteDatabase base_dados = this.getWritableDatabase();
            String deleta = "DELETE FROM turma WHERE nomeTurma=?";
            SQLiteStatement stmt = base_dados.compileStatement(deleta);
            stmt.bindString(1, turma);
            stmt.executeUpdateDelete();
            base_dados.close();
        }catch (Exception e){e.printStackTrace();}
        return true;
    }
    public Boolean deletarAlunoDturma(Integer id_turma){
        try {
            SQLiteDatabase base_dados = this.getWritableDatabase();
            String deleta = "DELETE FROM alunosTurma WHERE id_turma = ?";
            SQLiteStatement stmt = base_dados.compileStatement(deleta);
            stmt.bindLong(1, id_turma);
            stmt.executeUpdateDelete();
            base_dados.close();
        }catch (Exception e){e.printStackTrace();}
        return true;
    }
    public Boolean deletaAnonimos(Integer id_aluno){
        try {
            SQLiteDatabase base_dados = this.getWritableDatabase();
            String deleta = "DELETE FROM aluno WHERE id_aluno = ?";
            SQLiteStatement stmt = base_dados.compileStatement(deleta);
            stmt.bindLong(1, id_aluno);
            stmt.executeUpdateDelete();
            base_dados.close();
        }catch (Exception e){e.printStackTrace();}
        return true;
    }
    public Boolean upadateSenha(String password, Integer id){
        try {
            SQLiteDatabase base_dados = this.getWritableDatabase();
            String altera = "UPDATE usuario SET password=? WHERE id_usuario=?";
            SQLiteStatement stmt = base_dados.compileStatement(altera);
            stmt.bindString(1, to256(password));
            stmt.bindLong(2, id);
            stmt.executeUpdateDelete();
            base_dados.close();
        }catch (Exception e){e.printStackTrace();}
       return true;
    }
    public Boolean upadateTurma(String turma, Integer qtdAnonimos, Integer id_turma){
        try {
            SQLiteDatabase base_dados = this.getWritableDatabase();
            String altera = "UPDATE turma SET nomeTurma=?, qtdAnonimos = ? WHERE id_turma=?";
            SQLiteStatement stmt = base_dados.compileStatement(altera);
            stmt.bindString(1, turma);
            stmt.bindLong(2, qtdAnonimos);
            stmt.bindLong(3, id_turma);
            stmt.executeUpdateDelete();
            base_dados.close();
        }catch (Exception e){e.printStackTrace();}
        return true;
    }
    public Boolean upadateDadosAluno(String nomeAluno, String email, Integer id_aluno){
        try {
            SQLiteDatabase base_dados = this.getWritableDatabase();
            String altera = "UPDATE aluno SET nomeAluno=?, email=? WHERE id_aluno=?";
            SQLiteStatement stmt = base_dados.compileStatement(altera);
            stmt.bindString(1, nomeAluno);
            stmt.bindString(2, email);
            stmt.bindLong(3, id_aluno);
            stmt.executeUpdateDelete();
            base_dados.close();
        }catch (Exception e){e.printStackTrace();}
        return true;
    }
    public Boolean upadateAluno(String nomeAluno, Integer id_aluno){
        try {
            SQLiteDatabase base_dados = this.getWritableDatabase();
            String altera = "UPDATE aluno SET nomeAluno=? WHERE id_aluno=?";
            SQLiteStatement stmt = base_dados.compileStatement(altera);
            stmt.bindString(1, nomeAluno);
            stmt.bindLong(2, id_aluno);
            stmt.executeUpdateDelete();
            base_dados.close();
        }catch (Exception e){e.printStackTrace();}
        return true;
    }
    public Boolean checkNome(String nome, String email) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select * from usuario where nomeUsuario =? and email = ?", new String[]{nome, email});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }
    public Integer checkemail(String email) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select * from usuario where email = ?", new String[]{email});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getInt(0);
    }
    public String pegaNome(String id_usuario) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select * from usuario where id_usuario = ?", new String[]{id_usuario});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getString(1);
    }
    public String pegaNomeTurma(String id_turma) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select * from turma where id_turma = ?", new String[]{id_turma});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getString(2);
    }
    public String pegaNomeAluno(String id_aluno) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select * from aluno where id_aluno = ? and n = ?", new String[]{id_aluno, "1"});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getString(1);
        }else
            return null;
    }
    public String pegaData(String id_prova) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select * from prova where id_prova = ?", new String[]{id_prova});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getString(2);
    }
    public Integer pegaIdAluno(String nomeAluno) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select id_aluno from Aluno where nomeAluno = ?", new String[]{nomeAluno});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getInt(0);
    }
    public Integer pegaIdEscola(String nomeEscola) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select id_escola from escola where nomeEscola = ?", new String[]{nomeEscola});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getInt(0);
    }
    public Integer pegaIdEscolaDesativada(String nomeEscola) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select id_scolDesativadas from escolasDesativadas where nomeScolDesativada = ?", new String[]{nomeEscola});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getInt(0);
    }
    public Integer pegaIdAnonimo(String an) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select id_aluno from Aluno where nomeAluno = ?", new String[]{an});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getInt(0);
    }
    public Integer pegaqtdAnonimos(String id_turma) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select * from turma where id_turma = ?", new String[]{id_turma});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getInt(3);
    }
    public Integer pegaIdProva(String provacad) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select * from prova where nomeProva = ?", new String[]{provacad});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getInt(0);
    }
    public Integer pegaIdTurma(String nomeTurma) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select * from turma where nomeTurma = ?", new String[]{nomeTurma});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getInt(0);
    }
    public Integer pegaqtdQuestoes(String id_prova) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select * from prova where id_prova = ?", new String[]{id_prova});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getInt(3);
    }
    public Integer pegaqtdAlternativas(String id_prova) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select * from prova where id_prova = ?", new String[]{id_prova});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getInt(4);
    }
    public String pegaEmailAluno(String id_aluno) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select * from aluno where id_aluno = ?", new String[]{id_aluno});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getString(2);
    }
    public String pegaEscola(String id) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select * from escola where id_escola = ?", new String[]{id});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getString(1);
    }
    public String pegaUsuario(String id_usuario) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select * from usuario where id_usuario = ?", new String[]{id_usuario});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getString(1);
    }
    public String pegaEscolaDesativada(String id_scolDesativadas) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select * from escolasDesativadas where id_scolDesativadas = ?", new String[]{id_scolDesativadas});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getString(1);
    }
    public String pegaBairro(String id) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select * from escola where id_escola = ?", new String[]{id});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getString(2);
    }
    public String pegaBairroDesativado(String id_scolDesativadas) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select * from escolasDesativadas where id_scolDesativadas = ?", new String[]{id_scolDesativadas});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getString(2);
    }
    //Verifica se a senha Ligada ao email é a mesma informada
    public Integer checkemailpass(String email, String password){
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select * from usuario where email =? and password = ?", new String[] {email, to256(password)});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getInt(0);
        }else
            return null;
    }
    public Boolean checkEscola(String nomeEscola){
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("SELECT * FROM escola WHERE nomeEscola = ?", new String[]{nomeEscola});

        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }
    public Boolean checkprovasNome(String nomeProva, String id_turma) {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT id_turma FROM prova WHERE nomeProva = ? and id_turma = ?", new String[]{nomeProva, id_turma});
        if (cursor.getCount() > 0){
            return true;
        }else
            return false;
    }
    public Boolean checkProvaCorrigida(String id_prova){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM resultadoCorrecao WHERE id_prova = ?", new String[]{id_prova});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }
    public Boolean checkprovaId(String id_prova){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM prova WHERE id_prova = ?", new String[]{id_prova});
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
        Cursor cursor = database.rawQuery("SELECT nomeAluno FROM aluno WHERE nomeAluno = ?", new String[]{nome});
        if (cursor.getCount() > 0) return true;
        else return false;
    }
    public Boolean checkTurma(String turma){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT nomeTurma FROM turma WHERE nomeTurma = ? and id_escola = ?", new String[]{turma, String.valueOf(BancoDados.ID_ESCOLA)});
        if (cursor.getCount() > 0) return true;
        else return false;
    }
    public Boolean checkTurmaEmProva(Integer id_turma){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT id_turma FROM prova WHERE id_turma = ? and id_escola = ?", new String[]{String.valueOf(id_turma), String.valueOf(BancoDados.ID_ESCOLA)});
        if (cursor.getCount() > 0) return true;
        else return false;
    }
    public Boolean checkAlunoEmTurma(Integer id_aluno){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM alunosTurma WHERE id_aluno = ?", new String[]{String.valueOf(id_aluno)});
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
    public List<String> obterNomesAlunos() {
        List<String> nomesAlunos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nomeAluno FROM aluno where n = ? and id_escola = ? ORDER BY nomeAluno ASC", new String[]{"1", String.valueOf(BancoDados.ID_ESCOLA)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // O índice 0 corresponde à coluna 'nome' no exemplo
                String nomeAluno = cursor.getString(0);
                nomesAlunos.add(nomeAluno);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return nomesAlunos;
    }
    public List<String> obterNomeTurmas() {
        List<String>  nomesTurma = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nomeTurma FROM turma where id_escola = ?", new String[]{String.valueOf(BancoDados.ID_ESCOLA)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // O índice 0 corresponde à coluna 'nome' no exemplo
                String nomeTurma = cursor.getString(0);
                nomesTurma.add(nomeTurma);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return nomesTurma;
    }
    public List<String> obterNomeProvas(String id_turma) {
        List<String>  nomesProvas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nomeProva FROM prova where id_turma = ?", new String[]{id_turma});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // O índice 0 corresponde à coluna 'nome' no exemplo
                String nomeProva = cursor.getString(0);
                nomesProvas.add(nomeProva);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return nomesProvas;
    }
    public List<Integer> listAlunosDturma(String id_turma) {
        List<Integer>  ids = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_aluno FROM alunosTurma where id_turma = ?", new String[]{id_turma});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // O índice 0 corresponde à coluna 'nome' no exemplo
                Integer id = cursor.getInt(0);
                ids.add(id);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return ids;
    }
    public Integer listNota(String id_prova) {
        int notaTot = 0;
        //ArrayList<Integer>  notas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM gabarito where id_prova = ?", new String[]{id_prova});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // O índice 0 corresponde à coluna 'nome' no exemplo
                Integer nota = cursor.getInt(4);
                notaTot = notaTot + nota;
                //notas.add(nota);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return notaTot;
    }
    public List<String> listAluno(String id_prova) {
        ArrayList<String>  alunos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM resultadoCorrecao where id_prova = ?", new String[]{id_prova});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String aluno = cursor.getString(2);
                alunos.add(aluno);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return alunos;
    }
    public List<Integer> listAcertos(String id_prova) {
        ArrayList<Integer>  acertos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM resultadoCorrecao where id_prova = ?", new String[]{id_prova});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Integer acerto = cursor.getInt(3);
                acertos.add(acerto);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return acertos;
    }
    public List<Integer> listNotaAluno(String id_prova) {
        ArrayList<Integer>  notas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM resultadoCorrecao where id_prova = ?", new String[]{id_prova});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Integer nota = cursor.getInt(4);
                notas.add(nota);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return notas;
    }
    public List<String> listAlunos() {
        ArrayList<String>  alunos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM aluno where n = ? and id_escola = ? ORDER BY nomeAluno ASC", new String[]{"1", BancoDados.ID_ESCOLA.toString()});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String aluno = cursor.getString(1);
                alunos.add(aluno);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return alunos;
    }
    public List<String> listEscolas() {
        ArrayList<String>  escolas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM escola ORDER BY nomeEscola ASC", new String[]{});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String escola = cursor.getString(1);
                escolas.add(escola);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return escolas;
    }
    public List<String> listDesativadas() {
        ArrayList<String>  desativadas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM escolasDesativadas ORDER BY nomeScolDesativada ASC", new String[]{});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String desativada = cursor.getString(1);
                desativadas.add(desativada);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return desativadas;
    }


/*
    SQLiteDatabase database = bancoDados.getReadableDatabase();
    String [] projection = {"nomeAluno", "id_aluno"};
    Cursor cursor = database.query("aluno", projection, "id_escola="+BancoDados.ID_ESCOLA, null, null, null, null);
    ArrayList<String> alunos = new ArrayList<>();
    ArrayList<String> idsAlunos = new ArrayList<>();
    int nomeColumIndex = cursor.getColumnIndex("nomeAluno");
        if (nomeColumIndex != -1){
        while (cursor.moveToNext()){
            String nome = cursor.getString(0);
            String idAluno = cursor.getString(1);
            alunos.add(nome);
            idsAlunos.add(idAluno);
        }
    }else{
        Log.e("VisualAlunoActivity", "A coluna 'nome' não foi encontrada no cursor.");
    }
        cursor.close();
        database.close();


         SQLiteDatabase database = bancoDados.getReadableDatabase();
        String [] projection = {"nomeEscola", "id_escola"};
        Cursor cursor = database.query("escola", projection, "id_usuario="+BancoDados.USER_ID, null, null, null, null);
        ArrayList<String> nomesEscolas = new ArrayList<>();
        ArrayList<String> idsEscolas = new ArrayList<>();
        int nomeColumIndex = cursor.getColumnIndex("nomeEscola");
        if (nomeColumIndex != -1){
            while (cursor.moveToNext()){
                String nomeEscola = cursor.getString(0);
                String idEscola = cursor.getString(1);
                nomesEscolas.add(nomeEscola);
                idsEscolas.add(idEscola);
            }
        }else{
            Log.e("VisualEscolaActivity", "A coluna 'nomeEscola' não foi encontrada no cursor.");
        }
        cursor.close();
        database.close();

        SQLiteDatabase database = bancoDados.getReadableDatabase();
        String [] projection = {"nomeScolDesativada", "id_scolDesativadas"};
        Cursor cursor = database.query("escolasDesativadas", projection, "id_usuario="+BancoDados.USER_ID, null, null, null, null);
        ArrayList<String> nomesEscolasDesativadas = new ArrayList<>();
        ArrayList<String> idsEscolasDesativadas = new ArrayList<>();
        int nomeColumIndex = cursor.getColumnIndex("nomeScolDesativada");
        if (nomeColumIndex != -1){
            while (cursor.moveToNext()){
                String nomeEscola = cursor.getString(0);
                String idEscola = cursor.getString(1);
                nomesEscolasDesativadas.add(nomeEscola);
                idsEscolasDesativadas.add(idEscola);
            }
        }else{Log.e("VisualEscolaActivity", "A coluna 'nomeEscola' não foi encontrada no cursor.");}
        cursor.close();
        database.close();

 */


}