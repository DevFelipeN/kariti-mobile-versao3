package online.padev.kariti;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import androidx.annotation.NonNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
public class BancoDados extends SQLiteOpenHelper {
    public static final String DBNAME = "base_dados.db";
    public static Integer USER_ID;
    public static Integer ID_ESCOLA;
    public BancoDados(Context context) {
        super(context, DBNAME, null, 25);
    }
    @Override
    public void onCreate(SQLiteDatabase base_dados) {
        try {
            base_dados.execSQL("create Table usuario( id_usuario INTEGER primary Key AUTOINCREMENT, nomeUsuario TEXT not null, email TEXT UNIQUE not null, password varchar(256) not null)");
            base_dados.execSQL("create Table validacao_usuario( id_validacao INTEGER primary Key AUTOINCREMENT, id_usuario INT NOT NULL references usuario(id_usuario), codigo TEXT, data_expiracao TEXT)");
            base_dados.execSQL("create Table escola(id_escola INTEGER PRIMARY KEY AUTOINCREMENT, nomeEscola TEXT, bairro TEXT, id_usuario INT NOT NULL references usuario(id_usuario), status Integer not null check(status = 0 or status = 1))");
            base_dados.execSQL("create Table aluno(id_aluno Integer PRIMARY KEY AUTOINCREMENT, nomeAluno TEXT not null, email TEXT, status Integer not null check(status = 0 or status = 1), id_usuario INTEGER not null references usuario(id_usuario))");
            base_dados.execSQL("create Table turma(id_turma Integer PRIMARY KEY AUTOINCREMENT, id_escola INTEGER not null references escola(id_escola), nomeTurma TEXT not null, qtdAnonimos Integer not null)");
            base_dados.execSQL("create Table alunosTurma(id_turma Integer not null references turma(id_turma), id_aluno Integer not null references aluno(id_aluno), primary key (id_turma, id_aluno))");
            base_dados.execSQL("create Table prova(id_prova Integer PRIMARY KEY AUTOINCREMENT, nomeProva TEXT not null, dataProva TEXT not null, qtdQuestoes Integer not null, qtdAlternativas Interger not null, id_escola INTEGER, id_turma Integer not null references turma(id_turma))");
            base_dados.execSQL("create Table gabarito(id_gabarito Integer PRIMARY KEY AUTOINCREMENT, id_prova Integer not null references prova(id_prova), questao Integer not null, resposta Integer not null, nota Real not null)");
            base_dados.execSQL("create Table resultadoCorrecao(id_resultado Integer PRIMARY KEY AUTOINCREMENT, id_prova Integer not null references prova(id_prova), id_aluno Integer not null references aluno(id_aluno), questao Integer, respostaDada Integer)");
            base_dados.execSQL("create Table galeria(id INTEGER PRIMARY KEY AUTOINCREMENT, foto BLOB)");
        }catch(Exception e){
            Log.e("Error base_dados: ",e.getMessage());
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase base_dados, int oldVersion, int newVersion) {
        try {
            base_dados.execSQL("drop Table if exists usuario");
            base_dados.execSQL("drop Table if exists validacao_usuario");
            base_dados.execSQL("drop Table if exists escola");
            base_dados.execSQL("drop Table if exists aluno");
            base_dados.execSQL("drop Table if exists turma");
            base_dados.execSQL("drop Table if exists prova");
            base_dados.execSQL("drop Table if exists gabarito");
            base_dados.execSQL("drop Table if exists galeria");
            base_dados.execSQL("drop Table if exists alunosTurma");
            base_dados.execSQL("drop Table if exists resultadoCorrecao");
            onCreate(base_dados);
        }catch(Exception e){
            Log.e("Error base_dados: ",e.getMessage());
        }

    }

    /**
     * Este metodo cadastra novos usuários na tabela usuário.
     * @param nome nome do novo usuário que se deseja cadastrar
     * @param password senha do novo usuário que se deseja cadastrar
     * @param email email do novo usuário que se deseja cadastrar
     * @return retorna verdadeiro de cadastrado com sucesso falso, caso contrario
     */
    public boolean cadastrarUsuario(String nome, String password, String email) {
        SQLiteDatabase base_dados = null;
        try {
            base_dados = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("nomeUsuario", nome);
            contentValues.put("password", to256(password));
            contentValues.put("email", email);
            long inserir = base_dados.insert("usuario", null, contentValues);
            return inserir != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (base_dados != null && base_dados.isOpen()) {
                base_dados.close(); // Fecha o banco de dados para liberar recursos
            }
        }
    }

    /**
     * Este método insere uma nova escola no banco
     * @param nomeEscola parametro esperado como nome da escola a ser cadastrada
     * @param bairro parametro esperado contendo o bairro da escola
     * @param status paramentro indicativo que a escola sera ativa
     * @return retorna true se a inserção for bem sucedida ou falso, caso contrario
     */
    public Boolean inserirDadosEscola(String nomeEscola, String bairro, Integer status){
        SQLiteDatabase base_dados = null;
        try {
            base_dados = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("nomeEscola", nomeEscola);
            contentValues.put("bairro", bairro);
            contentValues.put("status", status);
            contentValues.put("id_usuario", BancoDados.USER_ID);
            long inserir = base_dados.insert("escola", null, contentValues);
            return inserir != -1;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
        }

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
    public void inserirAlunosNaTurma(Integer id_turma, Integer id_aluno){
        SQLiteDatabase base_dados = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id_turma", id_turma);
        contentValues.put("id_aluno", id_aluno);
        base_dados.insert("alunosTurma", null, contentValues);
        base_dados.close();
    }
    public void inserirResultCorrecao(Integer id_prova, Integer id_aluno, Integer questao, Integer respostaDada){
        SQLiteDatabase base_dados = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id_prova", id_prova);
        contentValues.put("id_aluno", id_aluno);
        contentValues.put("questao", questao);
        contentValues.put("respostaDada", respostaDada);
        base_dados.insert("resultadoCorrecao", null, contentValues);
        base_dados.close();
    }
    public Integer inserirProva(String nomeProva, String dataProva, Integer qtdQuestoes, Integer qtdAlternativas, Integer id_turma){
        SQLiteDatabase base_dados = null;
        Integer id_prova = null;
        try {
            base_dados = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("nomeProva", nomeProva);
            contentValues.put("dataProva", dataProva);
            contentValues.put("qtdQuestoes", qtdQuestoes);
            contentValues.put("qtdAlternativas", qtdAlternativas);
            contentValues.put("id_escola", BancoDados.ID_ESCOLA); // Excluir posteriormente
            contentValues.put("id_turma", id_turma);
            long inserir = base_dados.insert("prova", null, contentValues);
            int convert = (int) inserir; //remover gambiarra após segunda bateria de testes
            id_prova = Integer.valueOf(convert);
            return id_prova;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }finally {
            if (base_dados != null && base_dados.isOpen()) {
                base_dados.close();
            }
        }
    }
    public void inserirGabarito(Integer id_prova, Integer questao, Integer resposta, Float nota){
        SQLiteDatabase base_dados = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id_prova", id_prova);
        contentValues.put("questao", questao);
        contentValues.put("resposta", resposta);
        contentValues.put("nota", nota);
        base_dados.insert("gabarito", null, contentValues);
        base_dados.close();
        Log.e("kariti","Cadastrado");
    }
    public Boolean inserirDadosAluno(String nomeAluno, String email, Integer status){
        SQLiteDatabase base_dados = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nomeAluno", nomeAluno);
        contentValues.put("email", email);
        contentValues.put("status", status);
        contentValues.put("id_usuario", BancoDados.USER_ID);
        long inserir = base_dados.insert("aluno", null, contentValues);
        return inserir != -1;
    }
    public Integer inserirNovoAluno(String nomeAluno, String email, Integer status){
        SQLiteDatabase base_dados = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nomeAluno", nomeAluno);
        contentValues.put("email", email);
        contentValues.put("status", status);
        contentValues.put("id_usuario", BancoDados.USER_ID);
        long inserir = base_dados.insert("aluno", null, contentValues);
        return Math.toIntExact(inserir);
    }

    /**
     * Este método deleta uma escola do banco
     * @param id_escola
     * @return
     */
    public Boolean deletarEscola(Integer id_escola){
        SQLiteDatabase base_dados = null;
        SQLiteStatement stmt = null;
        try {
            base_dados = this.getWritableDatabase();
            String deleta = "DELETE FROM escola WHERE id_escola = ?";
            stmt = base_dados.compileStatement(deleta);
            stmt.bindLong(1, id_escola);
            stmt.executeUpdateDelete();
            base_dados.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(base_dados != null){
                base_dados.close();
            }
            if(stmt != null){
                stmt.close();
            }
        }
    }
    public Boolean deletarAluno(Integer id_aluno){
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
    public void deletarAlunoDturma(Integer id_turma){
        try {
            SQLiteDatabase base_dados = this.getWritableDatabase();
            String deleta = "DELETE FROM alunosTurma WHERE id_turma = ?";
            SQLiteStatement stmt = base_dados.compileStatement(deleta);
            stmt.bindLong(1, id_turma);
            stmt.executeUpdateDelete();
            base_dados.close();
        }catch (Exception e){e.printStackTrace();}
    }
    public void deletaGabarito(Integer id_prova){
        try {
            SQLiteDatabase base_dados = this.getWritableDatabase();
            String deleta = "DELETE FROM gabarito WHERE id_prova = ?";
            SQLiteStatement stmt = base_dados.compileStatement(deleta);
            stmt.bindLong(1, id_prova);
            stmt.executeUpdateDelete();
            base_dados.close();
        }catch (Exception e){e.printStackTrace();}
    }
    public void deletaProva(Integer id_prova){
        try {
            SQLiteDatabase base_dados = this.getWritableDatabase();
            String deleta = "DELETE FROM prova WHERE id_prova = ?";
            SQLiteStatement stmt = base_dados.compileStatement(deleta);
            stmt.bindLong(1, id_prova);
            stmt.executeUpdateDelete();
            base_dados.close();
        }catch (Exception e){e.printStackTrace();}
    }
    public void deletaCorrecao(Integer id_prova){
        try {
            SQLiteDatabase base_dados = this.getWritableDatabase();
            String deleta = "DELETE FROM resultadoCorrecao WHERE id_prova = ?";
            SQLiteStatement stmt = base_dados.compileStatement(deleta);
            stmt.bindLong(1, id_prova);
            stmt.executeUpdateDelete();
            base_dados.close();
        }catch (Exception e){e.printStackTrace();}
    }
    public void deletaCorrecaoPorAluno(Integer id_prova, Integer id_aluno){
        try {
            SQLiteDatabase base_dados = this.getWritableDatabase();
            String deleta = "DELETE FROM resultadoCorrecao WHERE id_prova = ? and id_aluno = ?";
            SQLiteStatement stmt = base_dados.compileStatement(deleta);
            stmt.bindLong(1, id_prova);
            stmt.bindLong(2, id_aluno);
            stmt.executeUpdateDelete();
            base_dados.close();
        }catch (Exception e){e.printStackTrace();}
    }
    public void deletaAnonimos(Integer id_turma){
        try {
            SQLiteDatabase base_dados = this.getWritableDatabase();
            String deleta = "DELETE FROM aluno WHERE status = ? and id_aluno in (select DISTINCT id_aluno FROM alunosTurma WHERE id_turma = ?)";
            SQLiteStatement stmt = base_dados.compileStatement(deleta);
            stmt.bindLong(1, 0);
            stmt.bindLong(2, id_turma);
            stmt.executeUpdateDelete();
            base_dados.close();
        }catch (Exception e){e.printStackTrace();}
    }

    /**
     * Este método altera a senha do usuario no banco
     * @param password parâmetro esperado para substituir a senha antiga
     * @param id_usuario parâmetro esperado para determinar de qual usuário se deseja alterar a senha
     * @return retorna true em caso de sucesso e false caso contrário
     */
    public Boolean alterarSenha(String password, Integer id_usuario){
        SQLiteDatabase base_dados = null;
        SQLiteStatement stmt = null;
        try {
            base_dados = this.getWritableDatabase();
            String altera = "UPDATE usuario SET password = ? WHERE id_usuario = ?";
            stmt = base_dados.compileStatement(altera);
            stmt.bindString(1, to256(password));
            stmt.bindLong(2, id_usuario);
            stmt.executeUpdateDelete();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if (stmt != null) {
                stmt.close();
            }
            if (base_dados != null) {
                base_dados.close();
            }
        }
    }
    public void upadateTurma(String turma, Integer qtdAnonimos, Integer id_turma){
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
    }
    public void upadateProva(Integer id_prova, String nomeProva, String dataProva, Integer id_turma, Integer questoes, Integer alternativas){
        try {
            SQLiteDatabase base_dados = this.getWritableDatabase();
            String altera = "UPDATE prova SET nomeProva = ?, dataProva = ?, qtdQuestoes = ?, qtdAlternativas = ?, id_turma = ?  WHERE id_prova = ?";
            SQLiteStatement stmt = base_dados.compileStatement(altera);
            stmt.bindString(1, nomeProva);
            stmt.bindString(2, dataProva);
            stmt.bindLong(3, questoes);
            stmt.bindLong(4, alternativas);
            stmt.bindLong(5, id_turma);
            stmt.bindLong(6, id_prova);
            stmt.executeUpdateDelete();
            base_dados.close();
            Log.e("kariti","Alterado");
        }catch (Exception e){e.printStackTrace();}
    }
    public void upadateResultadoCorrecao(Integer id_prova, Integer id_aluno, Integer questao, Integer respostaDada){
        try {
            SQLiteDatabase base_dados = this.getWritableDatabase();
            String altera = "UPDATE resultadoCorrecao SET respostaDada = ? WHERE id_prova = ? and id_aluno = ? and questao = ?";
            SQLiteStatement stmt = base_dados.compileStatement(altera);
            stmt.bindLong(1, respostaDada);
            stmt.bindLong(2, id_prova);
            stmt.bindLong(3, id_aluno);
            stmt.bindLong(4, questao);
            stmt.executeUpdateDelete();
            base_dados.close();
        }catch (Exception e){e.printStackTrace();}
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

    /**
     * Este método altera o status da escola para ativa ou desativada.
     * @param id_escola parametro usado para determinar qual escola sera realizada a ação
     * @param status parametro de identificação que determina se a escola será ativada(1) ou desativada(0).
     * @return retorna true se execução bem sucedida e false caso contrário
     */
    public Boolean alterarStatusEscola(Integer id_escola, Integer status){
        SQLiteDatabase base_dados = null;
        SQLiteStatement stmt = null;
        try {
            base_dados = this.getWritableDatabase();
            String altera = "UPDATE escola SET status = ? WHERE id_escola = ?";
            stmt = base_dados.compileStatement(altera);
            stmt.bindLong(1, status);
            stmt.bindLong(2, id_escola);
            stmt.executeUpdateDelete();
            base_dados.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(base_dados != null){
                base_dados.close();
            }
            if(stmt != null){
                stmt.close();
            }
        }
    }

    /**
     *Este método verifica a existencia de determinado usuario cadastrado no banco de dados.
     * @param email email do usuario que se deseja saber se já esta cadastrado
     * @return retorna true se usuario está cadastrado e falso caso contrário
     */
    public Boolean checkNome(String email) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = null;
        boolean exists = false;
        try {
            cursor = base_dados.rawQuery("SELECT email FROM usuario WHERE email = ?", new String[]{email});
            exists = cursor.moveToFirst();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return exists;
    }
    public Boolean checkResultadoCorrecao(Integer id_prova, Integer id_aluno) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select id_resultado from resultadoCorrecao where id_prova = ? and id_aluno = ?", new String[]{id_prova.toString(), id_aluno.toString()});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return true;
        }else
            return false;
    }

    /**
     * Este método verifica se determinado email está cadstrado no banco de dados
     * @param email parâmetro usado para verificar se existe o email no banco
     * @return retorna o id do usuario, caso exista o email e null caso contrário
     */
    public Integer verificaEmail(String email) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = null;
        Integer id_usuario = null;
        try {
            cursor = base_dados.rawQuery("SELECT id_usuario FROM usuario WHERE email = ?", new String[]{email});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                id_usuario = cursor.getInt(0);
            }
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }
        return id_usuario;
    }
    public Boolean checkEmailDAluno(String email) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select email from aluno where email = ? and id_usuario = ?", new String[]{email, BancoDados.USER_ID.toString()});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return true;
        }else {
            return false;
        }
    }
    public String pegaNome(Integer id_usuario) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select nomeUsuario from usuario where id_usuario = ?", new String[]{id_usuario.toString()});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getString(0);
    }
    public Integer pegaRespostaDada(Integer id_prova, Integer id_aluno, Integer questao) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = null;
        Integer respostaDada = null;
        try {
            cursor = base_dados.rawQuery("SELECT respostaDada FROM resultadoCorrecao WHERE id_prova = ? AND id_aluno = ? AND questao = ?", new String[]{id_prova.toString(), id_aluno.toString(), questao.toString()});
            if (cursor != null && cursor.moveToFirst()) {
                respostaDada = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return respostaDada;
    }

    public Integer pegaRespostaQuestao(Integer id_prova, Integer questao) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select resposta from gabarito where id_prova = ? and questao = ?", new String[]{id_prova.toString(), questao.toString()});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getInt(0);
    }
    public Float pegaNotaQuestao(Integer id_prova, Integer questao) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select nota from gabarito where id_prova = ? and questao = ?", new String[]{id_prova.toString(), questao.toString()});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getFloat(0);
    }
    public String pegaNomeTurma(String id_turma) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select nomeTurma from turma where id_turma = ?", new String[]{id_turma});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getString(0);
    }
    public String pegaNomeAluno(String id_aluno) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select nomeAluno from aluno where id_aluno = ? and id_usuario = ? and status = ?", new String[]{id_aluno, BancoDados.USER_ID.toString(), "1"});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getString(0);
        }else
            return null;
    }
    public String pegaNomeParaDetalhe(String id_aluno) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select nomeAluno from aluno where id_aluno = ? and id_usuario = ?", new String[]{id_aluno, BancoDados.USER_ID.toString()});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getString(0);
        }else
            return null;
    }
    public String pegaAlunoProvaCorrigida(Integer id_aluno) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select nomeAluno from aluno where id_aluno = ? and id_usuario = ? ORDER BY nomeAluno", new String[]{id_aluno.toString(), BancoDados.USER_ID.toString()});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getString(0);
        }else
            return null;
    }
    public String alunosGerarProva(String id_aluno) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select nomeAluno from aluno where id_aluno = ?", new String[]{id_aluno});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getString(0);
        }else
            return null;
    }
    public String pegaData(String id_prova) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select dataProva from prova where id_prova = ?", new String[]{id_prova});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getString(0);
    }
    public Integer pegaIdAluno(String nomeAluno) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select id_aluno from Aluno where nomeAluno = ?", new String[]{nomeAluno});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        Integer retorno = cursor.getInt(0);
        base_dados.close();
        return retorno;
    }

    /**
     * Este método pega id de uma determinda escola
     * @param nomeEscola parâmetro usado para identificar o id de qual escola esta sendo solicitado.
     * @return retorna o id da escola do tipo inteiro
     */
    public Integer pegaIdEscola(String nomeEscola) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = null;
        Integer id_escola = null;
        try {
            cursor = base_dados.rawQuery("SELECT id_escola FROM escola WHERE nomeEscola = ? AND id_usuario = ?", new String[]{nomeEscola, BancoDados.USER_ID.toString()});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                id_escola = cursor.getInt(0);
            }
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }
        return id_escola;
    }

    public Integer pegaIdProva(String provacad) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select id_prova from prova where nomeProva = ?", new String[]{provacad});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public Integer pegaIdProvaTESTEEEEE(String provacad, @NonNull Integer id_turma) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select id_prova from prova where nomeProva = ? and id_turma = ?", new String[]{provacad, id_turma.toString()});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getInt(0);
    }
    //AQUI
    public Integer pegaIdTurma(String nomeTurma) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = null;
        Integer id_turma = null;
        try {
            cursor = base_dados.rawQuery("Select id_turma from turma where nomeTurma = ? and id_escola = ?", new String[]{nomeTurma, BancoDados.ID_ESCOLA.toString()});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                id_turma = cursor.getInt(0);
            }
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }
        return id_turma;
    }
    public Integer pegaqtdQuestoes(String id_prova){
        SQLiteDatabase base_dados = this.getReadableDatabase();
        Cursor cursor = null;
        Integer qtdQuestoes = null;
        try {
            cursor = base_dados.rawQuery("SELECT qtdQuestoes FROM prova WHERE id_prova = ?", new String[]{id_prova});
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                qtdQuestoes = cursor.getInt(0);
            }
        }finally{
            if(cursor != null){
                cursor.close();
            }
        }
        return qtdQuestoes;
    }
    public Integer pegaqtdAlternativas(String id_prova) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select qtdAlternativas from prova where id_prova = ?", new String[]{id_prova});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getInt(0);
    }
    public String pegaEmailAluno(String id_aluno) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select * from aluno where id_aluno = ? and status = ? and id_usuario = ?", new String[]{id_aluno, "1", BancoDados.USER_ID.toString()});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getString(2);
    }
    public String pegaEscola(String escola) {
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = base_dados.rawQuery("Select * from escola where id_escola = ? and id_usuario = ?", new String[]{escola, BancoDados.USER_ID.toString()});
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

    /**
     * Este método verifica se o email e senha informado pelo usuário são validos
     * @param email parameto usado para vericar se existe no banco
     * @param password parametro usado para analisa se pertence ao email informado
     * @return retorna o id do usuario caso os dados de autenticação sejam validos ou null caso contrário
     */
    public Integer verificaAutenticacao(String email, String password){
        SQLiteDatabase base_dados = this.getWritableDatabase();
        Cursor cursor = null;
        Integer id_usuario = null;
        try {
            cursor = base_dados.rawQuery("SELECT id_usuario FROM usuario WHERE email = ? and password = ?", new String[] {email, to256(password)});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                id_usuario = cursor.getInt(0);
            }
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }
        return id_usuario;
    }

    /**
     * Este método verifica se existe um determinada escola cadastrada no banco
     * @param nomeEscola parametro usado para saber qual escola esta sendo pesquisada
     * @return restorna true se a escola já estiver cadastrada ou false caso contrario
     */
    public Boolean verificaEscola(String nomeEscola){
        Cursor cursor = null;
        boolean status = false;
        try {
            SQLiteDatabase base_dados = this.getWritableDatabase();
            cursor = base_dados.rawQuery("SELECT nomeEscola FROM escola WHERE nomeEscola = ? and id_usuario = ?", new String[]{nomeEscola, BancoDados.USER_ID.toString()});
            status = cursor.moveToFirst();
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }
        return status;
    }
    public Boolean checkprovasNome(String nomeProva, String id_turma) {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT nomeProva FROM prova WHERE nomeProva = ? and id_turma = ?", new String[]{nomeProva, id_turma});
        if (cursor.getCount() > 0){
            return true;
        }else
            return false;
    }
    public Boolean checkCorrigida(String id_prova){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT id_prova FROM resultadoCorrecao WHERE id_prova = ?", new String[]{id_prova});
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
    public Boolean checkAluno(String nome){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT nomeAluno FROM aluno WHERE nomeAluno = ? and id_usuario = ?", new String[]{nome, BancoDados.USER_ID.toString()});
        if (cursor.getCount() > 0) return true;
        else return false;
    }
    public Integer checkSituacaoCorrecao(Integer id_prova, Integer id_aluno){
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT questao FROM resultadoCorrecao WHERE id_prova = ? and id_aluno = ?", new String[]{id_prova.toString(), id_aluno.toString()});
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        return cursor.getInt(0);
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
    @NonNull
    private static String bytesToHex(@NonNull byte[] hash) {
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
        Cursor cursor = db.rawQuery("SELECT nomeAluno FROM aluno where status = ? and id_usuario = ? ORDER BY nomeAluno ASC", new String[]{"1", BancoDados.USER_ID.toString()});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String nomeAluno = cursor.getString(0);
                nomesAlunos.add(nomeAluno);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return nomesAlunos;
    }
    public List<String> listTodosAlunosDaTurma(String id_turma) {
        ArrayList<String>  alunos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nomeAluno FROM aluno where id_usuario = ? and id_aluno in (select DISTINCT id_aluno FROM alunosTurma WHERE id_turma = ?) ORDER BY nomeAluno ASC", new String[]{BancoDados.USER_ID.toString(), id_turma});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String aluno = cursor.getString(0);
                alunos.add(aluno);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return alunos;
    }
    public List<String> listAlunosDaTurmaSemAnonimos(String id_turma) {
        ArrayList<String>  alunos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nomeAluno FROM aluno where id_usuario = ? and status = ? and id_aluno in (select DISTINCT id_aluno FROM alunosTurma WHERE id_turma = ?) ORDER BY nomeAluno ASC", new String[]{BancoDados.USER_ID.toString(), "1", id_turma});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String aluno = cursor.getString(0);
                alunos.add(aluno);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return alunos;
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
    public List<Integer> listProvasPorTurma(String id_turma) {
        List<Integer>  ids_provas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_prova FROM prova WHERE id_turma = ? and id_escola = ?", new String[]{id_turma, BancoDados.ID_ESCOLA.toString()});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Integer id_prova = cursor.getInt(0);
                ids_provas.add(id_prova);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return ids_provas;
    }
    public List<String> listTurmasPorProva() {
        List<String>  nomesTurma = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nomeTurma FROM turma where id_escola = ? and id_turma in (select DISTINCT id_turma FROM prova) ", new String[]{String.valueOf(BancoDados.ID_ESCOLA)});
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
                String nomeProva = cursor.getString(0);
                nomesProvas.add(nomeProva);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return nomesProvas;
    }

    public List<String> listProvasNCorrigidas(Integer id_turma) {
        List<String>  nomesProvas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nomeProva, id_prova FROM prova p WHERE id_turma = ? and NOT EXISTS (SELECT id_prova FROM resultadoCorrecao r WHERE r.id_prova = p.id_prova)", new String[]{id_turma.toString()});
        if (cursor != null && cursor.moveToFirst()) {
            do {
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
                Integer id = cursor.getInt(0);
                ids.add(id);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return ids;
    }
    public List<Integer> qtdAlunosAnonimatos(String id_turma) {
        List<Integer>  ids_anonimos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT id_aluno FROM aluno where id_aluno in (select DISTINCT id_aluno FROM alunosTurma WHERE id_turma = ?) and status = ?", new String[]{id_turma, "0"});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Integer id = cursor.getInt(0);
                ids_anonimos.add(id);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return ids_anonimos;
    }
    public Float listNota(String id_prova) {
        float notaTot = 0;
        //ArrayList<Integer>  notas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nota FROM gabarito where id_prova = ?", new String[]{id_prova});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // O índice 0 corresponde à coluna 'nome' no exemplo
                float nota = cursor.getFloat(0);
                Log.e("kariti","Nota = "+nota);
                notaTot = notaTot + nota;
                //notas.add(nota);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return notaTot;
    }

    /**
     * Este método obtém as notas de cada questão de uma prova.
     * @param id_prova codigo da prova que se deseja saber as notas das questões.
     * @return lista com um item de texto para cada questão correspondendo a nota.
     * */
    public List<String> listNotaPorQuetao(Integer id_prova) {
        ArrayList<String>  notas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nota FROM gabarito where id_prova = ? ORDER BY questao", new String[]{id_prova.toString()});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // O índice 0 corresponde à coluna 'nome' no exemplo
                String nota = cursor.getString(0);
                notas.add(nota);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return notas;
    }
    public List<Integer> listAlunoPorProvaCorrigida(Integer id_prova){
        ArrayList<Integer> ids_alunos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_aluno FROM aluno WHERE id_aluno in (SELECT distinct id_aluno FROM resultadoCorrecao where id_prova = ?) ORDER BY nomeAluno", new String[]{id_prova.toString()});
        if (cursor != null && cursor.moveToFirst()){
            do {
                Integer id_aluno = cursor.getInt(0);
                ids_alunos.add(id_aluno);
            } while (cursor.moveToNext());
                cursor.close();
            }
        db.close();
        return ids_alunos;
    }
    public List<Integer> listQuestoes(Integer id_prova, Integer id_aluno) {
        ArrayList<Integer>  questoes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM resultadoCorrecao where id_prova = ? and id_aluno = ?", new String[]{id_prova.toString(), id_aluno.toString()});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Integer questao = cursor.getInt(3);
                questoes.add(questao);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return questoes;
    }

    /**
     * Este método lista todas as escolas do banco de dados pertecentes a um determinado usuário
     * @param status parametro que determina se as escolas listadas serão as ativas ou as desativadas
     * @return retorna uma lista de string contendo todas as escolas pertencentes ao usuario
     * logado caso não tenha, retorna uma lista vazia.
     */
    public List<String> listEscolas(Integer status) {
        ArrayList<String> escolas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT nomeEscola FROM escola WHERE id_usuario = ? AND status = ?  ORDER BY nomeEscola ASC", new String[]{BancoDados.USER_ID.toString(), status.toString()});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String escola = cursor.getString(0);
                    escolas.add(escola);
                } while (cursor.moveToNext());
            }
        }finally {
            if(cursor != null){
                cursor.close();
            }
            db.close();
        }
        return escolas;
    }
    public String mostraGabarito(Integer id_prova) {
        String gabarito = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT resposta FROM gabarito WHERE id_prova = ? ORDER BY questao ASC", new String[]{id_prova.toString()});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String resposta = cursor.getString(0);
                char r = (char) (Integer.parseInt(resposta)-1+'A');
                gabarito += r + "\n";
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return gabarito;
    }
    public String mostraGabaritoInt(Integer id_prova) {
        String gabarito = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT resposta FROM gabarito WHERE id_prova = ? ORDER BY questao ASC", new String[]{id_prova.toString()});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String resposta = cursor.getString(0);
                gabarito += resposta + "\n";
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return gabarito;
    }
    public List<String> carregaGabarito(Integer id_prova) {
        ArrayList<String> respostasEsperadas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT resposta FROM gabarito WHERE id_prova = ? ORDER BY questao ASC", new String[]{id_prova.toString()});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String resposta = cursor.getString(0);
                char r = (char) (Integer.parseInt(resposta)-1+'A');
                respostasEsperadas.add(String.valueOf(r));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return respostasEsperadas;
    }

    //Restorna os dados do Gabarito
    public String detalhePorAluno(Integer id_prova, Integer id_aluno) {
        String detalhes = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT respostaDada FROM resultadoCorrecao WHERE id_prova = ? and id_aluno = ? ORDER BY questao ASC", new String[]{id_prova.toString(), id_aluno.toString()});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String resposta = cursor.getString(0);
                char r = (char) (Integer.parseInt(resposta)-1+'A');
                detalhes += r + "\n";
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return detalhes;
    }
    public List<String> respostasDadas(Integer id_prova, Integer id_aluno) {
        ArrayList<String> respostasDadas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT respostaDada FROM resultadoCorrecao WHERE id_prova = ? and id_aluno = ? ORDER BY questao ASC", new String[]{id_prova.toString(), id_aluno.toString()});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String aux = "";
                String resposta = cursor.getString(0);
                if(!resposta.equals("-1")){
                    Log.e("kariti", resposta);
                    if (resposta.equals("0")) {
                        aux = "-";
                    } else {
                        aux = String.valueOf((char) (Integer.parseInt(String.valueOf(resposta.charAt(0))) - 1 + 'A'));
                    }
                    for (int i = 1; i < resposta.length(); i++) {
                        Log.e("kariti", "respostasss: " + String.valueOf(resposta.charAt(i)));
                        if (!String.valueOf(resposta.charAt(i)).equals("0")) {
                            aux += "+" + String.valueOf((char) (Integer.parseInt(String.valueOf(resposta.charAt(i))) - 1 + 'A'));
                        }
                    }
                    respostasDadas.add(aux);
                }
                //Log.e("kariti", "Aqui 5");
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        //Log.e("kariti", "Aqui 6");
        return respostasDadas;
    }
    public String listRespostasAluno(String id_prova, String id_aluno) {
        String respostasDadas = "";
        String ultimaQuestao = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT respostaDada, questao FROM resultadoCorrecao where id_prova = ? and id_aluno = ? ORDER BY questao ASC", new String[]{id_prova, id_aluno});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String resposta = cursor.getString(0);
                String questao = cursor.getString(1);
                if(!respostasDadas.isEmpty() && !questao.equals(ultimaQuestao)){
                    respostasDadas += ",";
                }
                respostasDadas += resposta;
                ultimaQuestao = questao;
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return respostasDadas;
    }
    public String listRespostasGabarito(String id_prova) {
        String respostasGabarito = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT resposta FROM gabarito WHERE id_prova = ? ORDER BY questao ASC", new String[]{id_prova});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String resposta = cursor.getString(0);
                respostasGabarito += resposta;
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return respostasGabarito;
    }
    public String listNotaQuestao(String id_prova) {
        String notasQuestoes = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nota FROM gabarito WHERE id_prova = ? ORDER BY questao ASC", new String[]{id_prova});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String nota = cursor.getString(0);
                notasQuestoes += nota;
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return notasQuestoes;
    }
}