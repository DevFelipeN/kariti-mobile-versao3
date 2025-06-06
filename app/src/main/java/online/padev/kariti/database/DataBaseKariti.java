package online.padev.kariti.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;

import org.apache.commons.logging.LogFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import online.padev.kariti.entity.Answer_key;
import online.padev.kariti.entity.Exam;
import online.padev.kariti.entity.Student;

public class DataBaseKariti extends SQLiteOpenHelper {
    public static final String DBNAME = "base_dados.db";
    private static final int DATABASE_VERSION = 27;
    public static Integer USER_ID;
    public static Integer ID_ESCOLA;
    public DataBaseKariti(Context context) {
        super(context, DBNAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase base_dados) {
        try {
            base_dados.execSQL("PRAGMA foreign_keys=ON;");

            base_dados.execSQL("CREATE TABLE user(user_id INTEGER primary Key AUTOINCREMENT, name TEXT not null, email TEXT UNIQUE not null, password varchar(256) not null)");
            base_dados.execSQL("CREATE TABLE school(school_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, user_id INTEGER NOT NULL references user(user_id), status INTEGER not null check(status = 0 or status = 1))");
                base_dados.execSQL("CREATE TABLE student(student_id Integer PRIMARY KEY AUTOINCREMENT, name TEXT not null, email TEXT, status Integer not null check(status = 0 or status = 1), school_id INTEGER not null references school(school_id))");
            base_dados.execSQL("CREATE TABLE class(class_id Integer PRIMARY KEY AUTOINCREMENT, school_id INTEGER not null references school(school_id), name TEXT not null)");
            base_dados.execSQL("CREATE TABLE student_class(class_id Integer not null references class(class_id), student_id Integer not null references student(student_id), primary key (class_id, student_id))");
            base_dados.execSQL("CREATE TABLE exam(exam_id Integer PRIMARY KEY AUTOINCREMENT, name TEXT not null, date TEXT not null, number_questions Integer not null, number_alternatives Integer not null, class_id Integer not null references class(class_id))");
            base_dados.execSQL("CREATE TABLE answer_key(answer_key_id Integer PRIMARY KEY AUTOINCREMENT, exam_id Integer not null references exam(exam_id), question Integer not null, answer Integer not null, grade Real not null)");
            base_dados.execSQL("CREATE TABLE result(result_id Integer PRIMARY KEY AUTOINCREMENT, exam_id Integer not null references exam(exam_id), student_id Integer not null references student(student_id), question Integer, answer_given Integer)");
        }catch(Exception e){
            Log.e("Error base_dados: ",e.getMessage());
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase base_dados, int oldVersion, int newVersion) {
        if (oldVersion < 28){
            try {
                base_dados.beginTransaction();

                base_dados.execSQL("CREATE TABLE user(user_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, email TEXT UNIQUE NOT NULL, password TEXT NOT NULL)");
                base_dados.execSQL("CREATE TABLE school(school_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, user_id INTEGER NOT NULL REFERENCES user(user_id), status INTEGER NOT NULL CHECK(status = 0 OR status = 1))");
                base_dados.execSQL("CREATE TABLE student(student_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, email TEXT, status INTEGER NOT NULL CHECK(status = 0 OR status = 1), school_id INTEGER NOT NULL REFERENCES school(school_id))");
                base_dados.execSQL("CREATE TABLE class(class_id INTEGER PRIMARY KEY AUTOINCREMENT, school_id INTEGER NOT NULL REFERENCES school(school_id), name TEXT NOT NULL)");
                base_dados.execSQL("CREATE TABLE student_class(class_id INTEGER NOT NULL REFERENCES class(class_id), student_id INTEGER NOT NULL REFERENCES student(student_id), PRIMARY KEY(class_id, student_id))");
                base_dados.execSQL("CREATE TABLE exam(exam_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, date TEXT NOT NULL, number_questions INTEGER NOT NULL, number_alternatives INTEGER NOT NULL, class_id INTEGER NOT NULL REFERENCES class(class_id))");
                base_dados.execSQL("CREATE TABLE answer_key(answer_key_id INTEGER PRIMARY KEY AUTOINCREMENT, exam_id INTEGER NOT NULL REFERENCES exam(exam_id), question INTEGER NOT NULL, answer INTEGER NOT NULL, grade REAL NOT NULL)");
                base_dados.execSQL("CREATE TABLE result(result_id INTEGER PRIMARY KEY AUTOINCREMENT, exam_id INTEGER NOT NULL REFERENCES exam(exam_id), student_id INTEGER NOT NULL REFERENCES student(student_id), question INTEGER, answer_given INTEGER)");

                base_dados.execSQL("INSERT INTO user(user_id, name, email, password) SELECT id_usuario, nomeUsuario, email, password FROM usuario");
                base_dados.execSQL("INSERT INTO school(school_id, name, user_id, status) SELECT id_escola, nomeEscola, id_usuario, status FROM escola");
                base_dados.execSQL("INSERT INTO student(student_id, name, email, status, school_id) SELECT id_aluno, nomeAluno, email, status, id_escola FROM aluno");
                base_dados.execSQL("INSERT INTO class(class_id, school_id, name) SELECT id_turma, id_escola, nomeTurma FROM turma");
                base_dados.execSQL("INSERT INTO student_class(class_id, student_id) SELECT id_turma, id_aluno FROM alunosTurma");
                base_dados.execSQL("INSERT INTO exam(exam_id, name, date, number_questions, number_alternatives, class_id) SELECT prova_id, nomeProva, dataProva, qtdQuestoes, qtdAlternativas, id_turma FROM prova");
                base_dados.execSQL("INSERT INTO answer_key(answer_key_id, exam_id, question, answer, grade) SELECT id_gabarito, id_prova, questao, resposta, nota FROM gabarito");
                base_dados.execSQL("INSERT INTO result(result_id, exam_id, student_id, question, answer_given) SELECT id_resultado, id_prova, id_aluno, questao, respostaDada FROM resultadoCorrecao");

                base_dados.execSQL("DROP TABLE IF EXISTS usuario");
                base_dados.execSQL("DROP TABLE IF EXISTS validacao_usuario");
                base_dados.execSQL("DROP TABLE IF EXISTS escola");
                base_dados.execSQL("DROP TABLE IF EXISTS aluno");
                base_dados.execSQL("DROP TABLE IF EXISTS turma");
                base_dados.execSQL("DROP TABLE IF EXISTS prova");
                base_dados.execSQL("DROP TABLE IF EXISTS gabarito");
                base_dados.execSQL("DROP TABLE IF EXISTS alunosTurma");
                base_dados.execSQL("DROP TABLE IF EXISTS resultadoCorrecao");

                base_dados.setTransactionSuccessful();

            } catch (Exception e){
                Log.e("kariti", e.getMessage());
            }finally {
                if (base_dados != null && base_dados.isOpen()) {
                    if (base_dados.inTransaction()) {
                        base_dados.endTransaction();
                    }
                }
            }
        }
    }
    public int getDatabaseVersion() {
        return DATABASE_VERSION;
    }

    /**
     * Este metodo cadastra novos usuários na tabela usuário.
     * @param nameUser nome do usuário que se deseja cadastrar
     * @param password senha do novo usuário que se deseja cadastrar
     * @param email email do novo usuário que se deseja cadastrar
     * @return retorna verdadeiro se cadastrado com sucesso falso, caso contrário.
     */
    public Boolean insertUser(String nameUser, String password, String email) {
        SQLiteDatabase base_dados = null;
        try {
            base_dados = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", nameUser);
            contentValues.put("password", to256(password));
            contentValues.put("email", email);
            long insert = base_dados.insert("user", null, contentValues);
            return insert != -1;
        } catch (Exception e) {
            Log.e("kariti", e.getMessage());
            return null;
        } finally {
            if (base_dados != null && base_dados.isOpen()) {
                base_dados.close(); // Fecha o banco de dados para liberar recursos
            }
        }
    }

    /**
     * Este método insere uma nova escola no banco
     * @param nameSchool parametro esperado como nome da escola a ser cadastrada
     * @return retorna true se a inserção for bem sucedida ou falso, caso contrario
     */
    public Boolean insertSchool(String nameSchool){
        SQLiteDatabase base_dados = null;
        try {
            base_dados = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", nameSchool);
            contentValues.put("status", 1);
            contentValues.put("user_id", DataBaseKariti.USER_ID);
            long insert = base_dados.insert("school", null, contentValues);
            return insert != -1;
        } catch (Exception e){
            Log.e("kariti", e.getMessage());
            return false;
        }finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
        }

    }

    public Integer insertStudent(String nameStudent, String email, Integer status){
        SQLiteDatabase base_dados = null;
        try {
            base_dados = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", nameStudent);
            contentValues.put("email", email);
            contentValues.put("status", status);
            contentValues.put("school_id", DataBaseKariti.ID_ESCOLA);
            long insert = base_dados.insert("student", null, contentValues);
            if(insert != -1){
                return Math.toIntExact(insert);
            }else{
                return -1;
            }
        }catch (Exception e){
            Log.e("kariti","Erro: aluno nao cadastrado!"+e.getMessage());
            return -1;
        }finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
        }
    }

    public Integer insertClass(String nameClass){
        SQLiteDatabase base_dados = null;
        Integer class_id = null;
        try {
            base_dados = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", nameClass);
            contentValues.put("school_id", DataBaseKariti.ID_ESCOLA);
            long insert = base_dados.insert("class", null, contentValues);
            class_id = Math.toIntExact(insert);
        } catch (Exception e){
            Log.e("kariti", e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
        }
        return class_id;
    }

    public boolean insertExam(Exam examData, List<Answer_key> answer_key){
        SQLiteDatabase base_dados = null;
        try {
            base_dados = this.getWritableDatabase();
            base_dados.beginTransaction();

            ContentValues contentValues = new ContentValues();
            contentValues.put("name", examData.getNameExam());
            contentValues.put("date", examData.getDateExam());
            contentValues.put("number_questions", examData.getNumQuestions());
            contentValues.put("number_alternatives", examData.getNumAlternatives());
            contentValues.put("class_id", examData.getClass_id());
            Integer insertExam = Math.toIntExact(base_dados.insert("exam", null, contentValues));

            if (!insertExam.equals(-1)){
                for (Answer_key g : answer_key){
                    ContentValues contentValues2 = new ContentValues();
                    contentValues2.put("exam_id", insertExam);
                    contentValues2.put("question", g.getQuestion());
                    contentValues2.put("answer", g.getResponse());
                    contentValues2.put("grade", g.getNote());
                    Integer insert = Math.toIntExact(base_dados.insert("answer_key", null, contentValues2));
                    if(!insert.equals(-1)){
                        Log.e("kariti", "Resultado de correção cadastrado com sucesso");
                    }else{
                        Log.e("kariti", "Erro ao tentar inserir resultado de correção no banco!");
                        throw new Exception();
                    }
                }

            }else{
                throw new Exception();
            }
            base_dados.setTransactionSuccessful();
            return true;
        }catch (Exception e){
            Log.e("kariti", "Erro ao tentar inserir resultado de correção no banco: "+e.getMessage());
            return false;
        }finally {
            if (base_dados != null && base_dados.isOpen()) {
                if (base_dados.inTransaction()) {
                    base_dados.endTransaction();
                }
                base_dados.close();
            }
        }
    }

    public Boolean insertCorrected(Map<Integer, Integer> answer_key, Integer exam_id, Integer student_id){
        SQLiteDatabase base_dados = null;
        SQLiteStatement stmt = null;
        Cursor cursor = null;
        try {
            base_dados = this.getWritableDatabase();
            base_dados.beginTransaction();
            
            cursor = base_dados.rawQuery("SELECT exam_id FROM result WHERE exam_id = ? AND student_id = ?", new String[]{exam_id.toString(), student_id.toString()});
            if (cursor != null && cursor.moveToFirst()) {
                String delete = "DELETE FROM result WHERE exam_id = ? AND student_id = ?";
                stmt = base_dados.compileStatement(delete);
                stmt.bindLong(1, exam_id);
                stmt.bindLong(2, student_id);
                stmt.executeUpdateDelete();
            }

            for (Integer question : answer_key.keySet()){
                Integer answer_given = answer_key.get(question);
                ContentValues contentValues = new ContentValues();
                contentValues.put("exam_id", exam_id);
                contentValues.put("student_id", student_id);
                contentValues.put("question", question);
                contentValues.put("answer_given", answer_given);
                long resultInsertion = base_dados.insert("result", null, contentValues);
                if(resultInsertion != -1){
                    Log.e("kariti", "Resultado de correção cadastrado com sucesso");
                }else{
                    Log.e("kariti", "Erro ao tentar inserir resultado de correção no banco!");
                    throw new Exception();
                }
            }
            base_dados.setTransactionSuccessful();
            return true;
        }catch (Exception e){
            Log.e("kariti", "Erro ao tentar inserir resultado de correção no banco: "+e.getMessage());
            return false;
        }finally {
            if (base_dados != null && base_dados.isOpen()) {
                if (base_dados.inTransaction()) {
                    base_dados.endTransaction();
                }
                base_dados.close();
            }
            if (cursor != null){
                cursor.close();
            }
            if(stmt != null){
                stmt.close();
            }
        }
    }
    
    public Boolean linkStudentToClass(Integer class_id, Integer student_id){
        SQLiteDatabase base_dados = null;
        try {
            base_dados = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("class_id", class_id);
            contentValues.put("student_id", student_id);
            long insert = base_dados.insert("student_class", null, contentValues);
            return insert != -1;
        }catch (Exception e){
            Log.e("kariti", e.getMessage());
            return false;
        }finally {
            if (base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
        }
    }

    /**
     * Este método deleta uma escola do banco
     * @param school_id parametro contendo o id da prova que se deseja deletar
     * @return retorna o resultado da execução
     */
    public boolean deleteSchool(Integer school_id){
        SQLiteDatabase base_dados = null;
        SQLiteStatement stmt = null;
        try {
            base_dados = this.getWritableDatabase();
            String delete = "DELETE FROM school WHERE school_id = ?";
            stmt = base_dados.compileStatement(delete);
            stmt.bindLong(1, school_id);
            stmt.executeUpdateDelete();
            return true;
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar deletar escola!"+e.getMessage());
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
    public boolean deleteStudent(Integer student_id){
        SQLiteDatabase base_dados = null;
        SQLiteStatement stmt = null;
        try {
            base_dados = this.getWritableDatabase();
            String deleta = "DELETE FROM student WHERE student_id = ?";
            stmt = base_dados.compileStatement(deleta);
            stmt.bindLong(1, student_id);
            stmt.executeUpdateDelete();
            return true;
        }catch (Exception e){
           Log.e("kariti","Erro ao tentar deletar aluno"+e.getMessage());
           return false;
        }finally {
            if (base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if (stmt != null){
                stmt.close();
            }
        }
    }

    public boolean deleteStudentsFromClass(Integer class_id){
        SQLiteDatabase base_dados = null;
        SQLiteStatement stmt = null;
        try {
            base_dados = this.getWritableDatabase();
            String delete = "DELETE FROM student_class WHERE class_id = ?";
            stmt = base_dados.compileStatement(delete);
            stmt.bindLong(1, class_id);
            stmt.executeUpdateDelete();
            return true;
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar deletar aluno da turma! "+e.getMessage());
            return false;
        }finally {
            if (base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if (stmt != null){
                stmt.close();
            }
        }
    }

    public boolean deleteClass(Integer class_id){
        SQLiteDatabase base_dados = null;
        SQLiteStatement stmtStudentAnonymous = null;
        SQLiteStatement stmtStudent = null;
        SQLiteStatement stmtClass = null;
        try {
            base_dados = this.getWritableDatabase();
            base_dados.beginTransaction();

            String deleteAnonymous = "DELETE FROM student WHERE status = ? AND student_id IN (SELECT student_id FROM student_class WHERE class_id = ?)";
            stmtStudentAnonymous = base_dados.compileStatement(deleteAnonymous);
            stmtStudentAnonymous.bindLong(1, 0);
            stmtStudentAnonymous.bindLong(2, class_id);
            stmtStudentAnonymous.executeUpdateDelete();

            String deletaAlunos = "DELETE FROM alunosTurma WHERE id_turma = ?";
            stmtStudent = base_dados.compileStatement(deletaAlunos);
            stmtStudent.bindLong(1, class_id);
            stmtStudent.executeUpdateDelete();

            String deleteClass = "DELETE FROM class WHERE class_id = ?";
            stmtClass = base_dados.compileStatement(deleteClass);
            stmtClass.bindLong(1, class_id);
            stmtClass.executeUpdateDelete();

            base_dados.setTransactionSuccessful();
            return true;
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar deletar turma! "+e.getMessage());
            return false;
        }finally {
            if (base_dados != null && base_dados.isOpen()){
                if (base_dados.inTransaction()) {
                    base_dados.endTransaction();
                }
                base_dados.close();
            }
            if (stmtStudentAnonymous != null){
                stmtStudentAnonymous.close();
            }
            if (stmtStudent != null){
                stmtStudent.close();
            }
            if (stmtClass != null){
                stmtClass.close();
            }
        }
    }

    public boolean deleteExamData(Integer exam_id){
        SQLiteDatabase base_dados = null;
        SQLiteStatement stmtCorrection = null;
        SQLiteStatement stmtAnswer_key = null;
        SQLiteStatement stmtExam = null;
        Cursor cursor = null;
        try {
            base_dados = this.getWritableDatabase();
            base_dados.beginTransaction();
            cursor = base_dados.rawQuery("SELECT exam_id FROM result WHERE exam_id = ?", new String[]{exam_id.toString()});
            if (cursor != null && cursor.moveToFirst()) {
                String deleteCorrection = "DELETE FROM result WHERE exam_id = ?";
                stmtCorrection = base_dados.compileStatement(deleteCorrection);
                stmtCorrection.bindLong(1, exam_id);
                stmtCorrection.executeUpdateDelete();
            }

            String deleteAnswer_key = "DELETE FROM answer_key WHERE exam_id = ?";
            stmtAnswer_key = base_dados.compileStatement(deleteAnswer_key);
            stmtAnswer_key.bindLong(1, exam_id);
            stmtAnswer_key.executeUpdateDelete();

            String deleteExam = "DELETE FROM exam WHERE exam_id = ?";
            stmtExam = base_dados.compileStatement(deleteExam);
            stmtExam.bindLong(1, exam_id);
            stmtExam.executeUpdateDelete();

            base_dados.setTransactionSuccessful();
            return true;
        }catch (Exception e){
            Log.e("kariti", "Erro ao tentar inserir resultado de correção no banco: "+e.getMessage());
            return false;
        }finally {
            if (base_dados != null && base_dados.isOpen()) {
                if (base_dados.inTransaction()) {
                    base_dados.endTransaction();
                }
                base_dados.close();
            }
            if (cursor != null){
                cursor.close();
            }
            if(stmtCorrection != null){
                stmtCorrection.close();
            }
            if(stmtAnswer_key != null){
                stmtAnswer_key.close();
            }
            if(stmtExam != null){
                stmtExam.close();
            }
        }
    }

    public boolean deleteAnonymous(Integer class_id){
        SQLiteDatabase base_dados = null;
        SQLiteStatement stmt = null;
        try {
            base_dados = this.getWritableDatabase();
            String delete = "DELETE FROM student WHERE status = ? and student_id in (select student_id FROM student_class WHERE class_id = ?)";
            stmt = base_dados.compileStatement(delete);
            stmt.bindLong(1, 0);
            stmt.bindLong(2, class_id);
            stmt.executeUpdateDelete();
            return true;
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar deletar aluno anonimo! "+e.getMessage());
            return false;
        }finally {
            if (base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if (stmt != null){
                stmt.close();
            }
        }
    }
    /**
     * Este método altera a senha do usuario no banco
     * @param password parâmetro esperado para substituir a senha antiga
     * @param user_id parâmetro esperado para determinar de qual usuário se deseja alterar a senha
     * @return retorna true em caso de sucesso e false caso contrário
     */
    public boolean updatePassword(String password, Integer user_id){
        SQLiteDatabase base_dados = null;
        SQLiteStatement stmt = null;
        try {
            base_dados = this.getWritableDatabase();
            String alter = "UPDATE user SET password = ? WHERE user_id = ?";
            stmt = base_dados.compileStatement(alter);
            stmt.bindString(1, to256(password));
            stmt.bindLong(2, user_id);
            stmt.executeUpdateDelete();
            return true;
        }catch (Exception e){
            Log.e("Kariti","Erro ao tentar alterar senha! "+e.getMessage());
            return false;
        }finally {
            if (base_dados != null && base_dados.isOpen()) {
                base_dados.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    /**
     * Este método altera o status da escola para ativa ou desativada.
     * @param school_id parametro usado para determinar qual escola sera realizada a ação
     * @param status parametro de identificação que determina se a escola será ativada(1) ou desativada(0).
     * @return retorna true se execução bem sucedida e false caso contrário
     */
    public Boolean updateSchool(Integer school_id, Integer status){
        SQLiteDatabase base_dados = null;
        SQLiteStatement stmt = null;
        try {
            base_dados = this.getWritableDatabase();
            String alter = "UPDATE school SET status = ? WHERE school_id = ?";
            stmt = base_dados.compileStatement(alter);
            stmt.bindLong(1, status);
            stmt.bindLong(2, school_id);
            stmt.executeUpdateDelete();
            return true;
        }catch (Exception e){
            Log.e("Kariti", "Erro ao tentar alterar o status da escola com id: "+school_id);
            return false;
        }finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(stmt != null){
                stmt.close();
            }
        }
    }

    public Boolean updateStudentData(String nameStudent, String email, Integer student_id){
        SQLiteDatabase base_dados = null;
        SQLiteStatement stmt = null;
        try {
            base_dados = this.getWritableDatabase();
            String alter = "UPDATE student SET name = ?, email = ? WHERE student_id = ?";
            stmt = base_dados.compileStatement(alter);
            stmt.bindString(1, nameStudent);
            stmt.bindString(2, email);
            stmt.bindLong(3, student_id);
            stmt.executeUpdateDelete();
            return true;
        }catch (Exception e){
            Log.e("Kariti", "Erro ao tentar alterar dados do aluno com id: "+student_id);
            return null;
        }finally {
            if(base_dados != null){
                base_dados.close();
            }
            if(stmt != null){
                stmt.close();
            }
        }
    }

    public boolean updateClassData(String nameClass, Integer class_id){
        SQLiteDatabase base_dados = null;
        SQLiteStatement stmt = null;
        try {
            base_dados = this.getWritableDatabase();
            String alter = "UPDATE class SET name = ? WHERE class_id = ?";
            stmt = base_dados.compileStatement(alter);
            stmt.bindString(1, nameClass);
            stmt.bindLong(2, class_id);
            stmt.executeUpdateDelete();
            return true;
        }catch (Exception e){
            Log.e("Kariti","Erro ao tentar alterar Turma! "+e.getMessage());
            return false;
        }finally {
            if (base_dados != null && base_dados.isOpen()) {
                base_dados.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public boolean upadateExamData(Exam exam, List<Answer_key> answer_key, int status){
        SQLiteDatabase base_dado = null;
        SQLiteStatement stmtUpdateExam = null;
        SQLiteStatement stmtDeleteAnswer_key = null;
        try {
            base_dado = this.getWritableDatabase();
            base_dado.beginTransaction();

            if (status == 1) {

                String alter = "UPDATE exam SET name = ?, date = ?, number_questions = ?, number_alternatives = ?, class_id = ?  WHERE exam_id = ?";
                stmtUpdateExam = base_dado.compileStatement(alter);
                stmtUpdateExam.bindString(1, exam.getNameExam());
                stmtUpdateExam.bindString(2, exam.getDateExam());
                stmtUpdateExam.bindLong(3, exam.getNumQuestions());
                stmtUpdateExam.bindLong(4, exam.getNumAlternatives());
                stmtUpdateExam.bindLong(5, exam.getClass_id());
                stmtUpdateExam.bindLong(6, exam.getExam_id());
                int result = stmtUpdateExam.executeUpdateDelete();
                if (result == 0) {
                    throw new Exception();
                }
            }
            String deleteAnswer_key = "DELETE FROM answer_key WHERE exam_id = ?";
            stmtDeleteAnswer_key = base_dado.compileStatement(deleteAnswer_key);
            stmtDeleteAnswer_key.bindLong(1, exam.getExam_id());
            int resultDel = stmtDeleteAnswer_key.executeUpdateDelete();
            if (resultDel == 0) {
                throw new Exception();
            }

            for (Answer_key g : answer_key){
                ContentValues contentValues = new ContentValues();
                contentValues.put("exam_id", exam.getExam_id());
                contentValues.put("question", g.getQuestion());
                contentValues.put("answer", g.getResponse());
                contentValues.put("grade", g.getNote());
                Integer insertAnswer_key = Math.toIntExact(base_dado.insert("answer_key", null, contentValues));
                if(!insertAnswer_key.equals(-1)){
                    Log.e("kariti", "Resultado de correção cadastrado com sucesso");
                }else{
                    Log.e("kariti", "Erro ao tentar inserir resultado de correção no banco!");
                    throw new Exception();
                }
            }

            base_dado.setTransactionSuccessful();
            return true;

        }catch (Exception e){
            Log.e("Kariti","Erro ao tentar alterar dados da prova com id; "+ exam.getExam_id());
            return false;
        }finally {
            if (base_dado != null && base_dado.isOpen()) {
                if (base_dado.inTransaction()) {
                    base_dado.endTransaction();
                }
                base_dado.close();
            }
            if (stmtUpdateExam != null) {
                stmtUpdateExam.close();
            }
            if (stmtDeleteAnswer_key != null) {
                stmtDeleteAnswer_key.close();
            }
        }
    }

    /**
     * Este método verifica se determinado email está cadastrado no banco de dados
     * @param email parâmetro usado para verificar se existe o email no banco
     * @return retorna o id do usuario, caso exista o email e null caso contrário
     */
    public Integer checkUserEmail(String email) {
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        Integer user_id  = null;
        try {
            base_dados = this.getWritableDatabase();
            cursor = base_dados.rawQuery("SELECT user_id FROM user WHERE email = ?", new String[]{email});
            if (cursor != null && cursor.moveToFirst()) {
                user_id = cursor.getInt(0);
            }
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar realizar consulta de e-mail! "+e.getMessage());
            return -1;
        }finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return user_id;
    }

    public Boolean checkStudentEmail(String email) {
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT email FROM student WHERE email = ? AND school_id = ?", new String[]{email, DataBaseKariti.ID_ESCOLA.toString()});
            return cursor != null && cursor.moveToFirst();
        }catch (Exception e){
            Log.e("kariti","Erro na vericação do email na tabela aluno: "+e.getMessage());
            return null;
        }finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if (cursor != null){
                cursor.close();
            }
        }

    }
    
    public Boolean checkIfExamCorrected(String exam_id){
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT exam_id FROM result WHERE exam_id = ?", new String[]{exam_id});
            return cursor != null && cursor.moveToFirst();
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar verificar existencia de prova no banco! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
    }

    public Boolean checkIfExamStudentCorrected(Integer exam_id, Integer student_id) {
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try{
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT exam_id FROM result WHERE exam_id = ? AND student_id = ?", new String[]{exam_id.toString(), student_id.toString()});
            return cursor != null && cursor.moveToFirst();
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar consultar se a prova do aluno esta corrigida! "+e.getMessage());
            return null;
        }finally{
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if (cursor != null){
                cursor.close();
            }
        }
    }
    /**
     * Este método verifica se o email e senha informado pelo usuário são válidos
     * @param email parameto usado para vericar se existe no banco
     * @param password parametro usado para analisa se pertence ao email informado
     * @return retorna o id do usuario caso os dados de autenticação sejam validos ou null caso contrário
     */
    public Integer checkAuthentication(String email, String password){
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        Integer user_id = null;
        try {
            base_dados = this.getWritableDatabase();
            cursor = base_dados.rawQuery("SELECT user_id FROM user WHERE email = ? AND password = ?", new String[] {email, to256(password)});
            if (cursor != null && cursor.moveToFirst()) {
                user_id = cursor.getInt(0);
            }
        }catch (Exception e){
            Log.e("kariti","Erro de verificação de autenticação! "+e.getMessage());
            return null;
        }finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return user_id;
    }

    /**
     * Este método verifica se existe um determinada escola cadastrada no banco
     * @param nameSchool parametro usado para saber qual escola esta sendo pesquisada
     * @return restorna true se a escola já estiver cadastrada ou false caso contrario
     */
    public Boolean checkSchool(String nameSchool){
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT name FROM school WHERE name = ? AND user_id = ?", new String[]{nameSchool, DataBaseKariti.USER_ID.toString()});
            return cursor != null && cursor.moveToFirst();
        } catch (Exception e){
            Log.e("kariti","Erro ao tentar verificar existencia de escola no banco! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
    }

    public Boolean checkIfExistExam(String nameExam, String class_id) {
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getWritableDatabase();
            cursor = base_dados.rawQuery("SELECT name FROM exam WHERE name = ? and class_id = ?", new String[]{nameExam, class_id});
            return cursor != null && cursor.moveToFirst();
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar verificar existencia de prova no banco! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
    }

    public Boolean checkIfExistExam(Integer exam_id){
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT exam_id FROM exam WHERE exam_id = ?", new String[]{exam_id.toString()});
            return cursor != null && cursor.moveToFirst();
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar verificar existencia de prova por id no banco! "+e.getMessage());
            return null ;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }

    }
    public Boolean checkIfExistExams(){
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT exam_id FROM exam, class WHERE exam.class_id = class.class_id AND class.school_id = ?", new String[]{DataBaseKariti.ID_ESCOLA.toString()});
            return cursor != null && cursor.moveToFirst();
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar verificar existencia de provas cadastradas no banco! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
    }

    public Boolean checkCorrectedByClass(String class_id){
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT exam_id FROM exam WHERE class_id = ? AND exam_id IN (SELECT exam_id FROM result)", new String[]{class_id});
            return cursor != null && cursor.moveToFirst();
        } catch (Exception e){
            Log.e("kariti","Erro ao tentar verificar se existe provas corrigidas para essa turma! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
    }

    public Boolean checkSituationCorrected(Integer exam_id, Integer student_id, Integer state){
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT question FROM result WHERE exam_id = ? AND student_id = ? AND question = ?", new String[]{exam_id.toString(), student_id.toString(), state.toString()});
            return cursor != null && cursor.moveToFirst();
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar verificar situaçao de correção por aluno! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
    }

    public Boolean checkExistStudent(String name){
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT name FROM student WHERE name = ? AND school_id = ?", new String[]{name, DataBaseKariti.ID_ESCOLA.toString()});
            return cursor != null && cursor.moveToFirst();
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar verificar existencia de aluno! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
    }

    public Boolean checkExistStudent(Integer student_id){
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT student_id FROM student WHERE student_id = ? AND school_id = ?", new String[]{student_id.toString(), DataBaseKariti.ID_ESCOLA.toString()});
            return cursor != null && cursor.moveToFirst();
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar verificar existencia de aluno! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
    }

    public Boolean checkExistStudent(){
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT student_id FROM student WHERE school_id = ? AND status = 1", new String[]{DataBaseKariti.ID_ESCOLA.toString()});
            return cursor != null && cursor.moveToFirst();
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar verificar existencia de aluno! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
    }

    public Boolean checkExistClass(){
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT class_id FROM class WHERE school_id = ?", new String[]{String.valueOf(DataBaseKariti.ID_ESCOLA)});
            return cursor != null && cursor.moveToFirst();
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar verificar existencia de turma! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
    }

    public Boolean checkExistClass(String nameClass){
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT name FROM class WHERE name = ? and school_id = ?", new String[]{nameClass, String.valueOf(DataBaseKariti.ID_ESCOLA)});
            return cursor != null && cursor.moveToFirst();
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar verificar existencia de turma! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
    }

    public Boolean checkIfClassInExam(Integer class_id){
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT class_id FROM exam WHERE class_id = ?", new String[]{String.valueOf(class_id)});
            return cursor != null && cursor.moveToFirst();
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar verificar existencia de turma em prova! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }

    }

    public Boolean checkIfStudentInClass(Integer student_id){
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT student_id FROM student_class WHERE student_id = ?", new String[]{String.valueOf(student_id)});
            return cursor != null && cursor.moveToFirst();
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar verificar existencia de aluno em turma! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
    }

    public String getUserName() {
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        String userName = null;
        try {
            base_dados = this.getWritableDatabase();
            cursor = base_dados.rawQuery("SELECT name FROM user WHERE user_id = ?", new String[]{DataBaseKariti.USER_ID.toString()});
            if (cursor != null && cursor.moveToFirst()){
                userName = cursor.getString(0);
            }
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar pegar nome do usuario! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return userName;
    }

    public String getUserName(Integer user_id) {
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        String userName = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT name FROM user WHERE user_id = ?", new String[]{user_id.toString()});
            if (cursor != null && cursor.moveToFirst()){
                userName = cursor.getString(0);
            }
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar pegar nome de usuario! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return userName;
    }

    public String getUserEmail(Integer user_id) {
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        String userEmail = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT email FROM user WHERE user_id = ?", new String[]{user_id.toString()});
            if (cursor != null && cursor.moveToFirst()){
                userEmail = cursor.getString(0);
            }
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar pegar email do usuario! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return userEmail;
    }

    public String getSchoolName() {
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        String schoolName = null;
        try {
            base_dados = this.getWritableDatabase();
            cursor = base_dados.rawQuery("SELECT name FROM school WHERE school_id = ? AND user_id = ?", new String[]{DataBaseKariti.ID_ESCOLA.toString(), DataBaseKariti.USER_ID.toString()});
            if (cursor != null && cursor.moveToFirst()){
                schoolName = cursor.getString(0);
            }
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar pegar nome da escola! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return schoolName;
    }

    /**
     * Este método pega id de uma determinda escola
     * @param SchoolName parâmetro usado para identificar o id de qual escola esta sendo solicitada.
     * @return retorna o id da escola do tipo inteiro
     */
    public Integer getSchoolId(String SchoolName) {
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        Integer school_id = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT school_id FROM school WHERE name = ? AND user_id = ?", new String[]{SchoolName, DataBaseKariti.USER_ID.toString()});
            if (cursor != null && cursor.moveToFirst()) {
                school_id = cursor.getInt(0);
            }
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar pegar id da escola! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return school_id;
    }

    public Integer getStudentId(String studentName) {
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        Integer student_id = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT student_id FROM student WHERE name = ? AND school_id = ?", new String[]{studentName, DataBaseKariti.ID_ESCOLA.toString()});
            if (cursor != null && cursor.moveToFirst()) {
                student_id = cursor.getInt(0);
            }
        } catch (Exception e){
            Log.e("kariti","Erro ao tentar pegar id do aluno! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return student_id;
    }

    public String getStudentName(Integer student_id, int status) {
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        String studentName = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT name FROM student WHERE student_id = ? AND school_id = ? AND status = ?", new String[]{student_id.toString(), DataBaseKariti.ID_ESCOLA.toString(), String.valueOf(status)});
            if (cursor != null && cursor.moveToFirst()){
                studentName = cursor.getString(0);
            }
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar pegar nome do aluno! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return studentName;

    }

    public String getStudentName(Integer student_id) {
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        String studentName = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT name FROM student WHERE student_id = ? AND school_id = ?", new String[]{student_id.toString(), DataBaseKariti.ID_ESCOLA.toString()});
            if (cursor != null && cursor.moveToFirst()){
                studentName = cursor.getString(0);
            }
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar pegar nome do aluno! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return studentName;
    }

    public String getStudentEmail(Integer student_id) {
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        String studentEmail = "";
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT email FROM student WHERE student_id = ? AND status = ? AND school_id = ?", new String[]{student_id.toString(), "1", DataBaseKariti.ID_ESCOLA.toString()});
            if (cursor != null && cursor.moveToFirst()){
                studentEmail = cursor.getString(0);
            }
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar pegar email do aluno! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return studentEmail;
    }

    public Integer getStudentNumber(String class_id, Integer status) {
        int studentNumber = 0;
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados  = this.getReadableDatabase();
            cursor = base_dados .rawQuery("SELECT COUNT (DISTINCT student_id) FROM student WHERE student_id IN (SELECT student_id FROM student_class WHERE class_id = ?) AND status = ? AND school_id = ?", new String[]{class_id, status.toString(), DataBaseKariti.ID_ESCOLA.toString()});
            if (cursor != null && cursor.moveToFirst()) {
                studentNumber  = cursor.getInt(0);
            }
        } catch (Exception e){
            Log.e("kariti","Erro ao tentar pegar quantidade de alunos por status! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return studentNumber;
    }

    public String getClassName(String class_id){
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        String className = null;
        try {
            base_dados = this.getWritableDatabase();
            cursor = base_dados.rawQuery("SELECT name FROM class WHERE class_id = ? AND school_id = ?", new String[]{class_id, DataBaseKariti.ID_ESCOLA.toString()});
            if (cursor != null && cursor.moveToFirst()){
                className = cursor.getString(0);
            }
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar pegar nome da turma! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return className;

    }

    public Integer getClassId(String className) {
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        Integer class_id = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT class_id FROM class WHERE name = ? AND school_id = ?", new String[]{className, DataBaseKariti.ID_ESCOLA.toString()});
            if (cursor != null && cursor.moveToFirst()) {
                class_id = cursor.getInt(0);
            }
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar pegar id da turma! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return class_id;
    }

    public String getExamName(Integer exam_id) {
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        String examName = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT name FROM exam WHERE exam_id = ?", new String[]{exam_id.toString()});
            if (cursor != null && cursor.moveToFirst()){
                examName = cursor.getString(0);
            }
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar pegar nome da Prova! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return examName;
    }

    public Integer getExamId(String examName, Integer class_id) {
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        Integer exam_id = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT exam_id FROM exam WHERE name = ? AND class_id = ?", new String[]{examName, class_id.toString()});
            if (cursor != null && cursor.moveToFirst()){
                exam_id = cursor.getInt(0);
            }
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar pegar id da prova! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return exam_id;

    }

    public Integer getNumberQuestions(String exam_id){
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        Integer qtdQuestions = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT number_questions FROM exam WHERE exam_id = ?", new String[]{exam_id});
            if (cursor != null && cursor.moveToFirst()){
                qtdQuestions = cursor.getInt(0);
            }
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar pegar quantidade de questões! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return qtdQuestions;
    }

    public Double getExamGrade(String exam_id) {
        double examGrade = 0;
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT grade FROM answer_key WHERE exam_id = ?", new String[]{exam_id});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    double gradeI = cursor.getDouble(0);
                    examGrade = examGrade + gradeI;
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.e("kariti","Erro ao tentar pegar nota da prova! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }

        return examGrade;
    }

    public String[] getExamData(Integer exam_id) {
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        String[] x = new String[5];
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT name, class_id, date, number_questions, number_alternatives FROM exam WHERE exam_id = ?", new String[]{exam_id.toString()});
            if (cursor != null && cursor.moveToFirst()) {
                x[0] = cursor.getString(0);
                x[1] = cursor.getString(1);
                x[2] = cursor.getString(2);
                x[3] = cursor.getString(3);
                x[4] = cursor.getString(4);
            }
        } catch (Exception e) {
            Log.e("kariti", "Erro ao tentar pegar dados da Prova! " + e.getMessage());
            return null;
        } finally {
            if (base_dados != null && base_dados.isOpen()) {
                base_dados.close();
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return x;
    }

    public List<String> listStudentNames(int status) {
        List<String> studentNames = new ArrayList<>();
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT name FROM student WHERE status = ? AND school_id = ? ORDER BY name ASC", new String[]{String.valueOf(status), DataBaseKariti.ID_ESCOLA.toString()});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String studentName = cursor.getString(0);
                    studentNames.add(studentName);
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar listar nomes dos alunos! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return studentNames;
    }


    public List<String> listStudentNames(String class_id) {
        List<String>  studentNames = new ArrayList<>();
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT name FROM student WHERE school_id = ? AND student_id IN (SELECT student_id FROM student_class WHERE class_id = ?) ORDER BY name ASC", new String[]{DataBaseKariti.ID_ESCOLA.toString(), class_id});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String student = cursor.getString(0);
                    studentNames.add(student);
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.e("kariti","Erro ao tentar listar nomes dos alunos por turma! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return studentNames;
    }
    public List<String> listStudentNames(String class_id, Integer status) {
        List<String>  studentNames = new ArrayList<>();
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT name FROM student WHERE school_id = ? AND status = ? AND student_id IN (SELECT student_id FROM student_class WHERE class_id = ?) ORDER BY name ASC", new String[]{DataBaseKariti.ID_ESCOLA.toString(), status.toString(), class_id});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String student = cursor.getString(0);
                    studentNames.add(student);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.e("kariti","Erro ao tentar listar nomes dos alunos por status e turma! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return studentNames;
    }

    public List<Student> listStudentExamCorrected(Integer class_id) {
        List<Student> students = new ArrayList<>();
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT student_id, name, email FROM student WHERE " +
                    "student_id IN (SELECT student_id FROM student_class WHERE class_id = ?) AND " +
                    "student_id IN (SELECT student_id FROM result)", new String[]{class_id.toString()});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Integer id_student = cursor.getInt(0);
                    String nameStudent = cursor.getString(1);
                    String email = cursor.getString(2);
                    Student st = new Student(id_student, nameStudent, email);
                    students.add(st);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.e("kariti","Erro ao tentar listar respostas do gabarito de forma numérica! "+e.getMessage());
            return null;
        } finally {
            if (base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if (cursor != null){
                cursor.close();
            }
        }
        return students;
    }


    public List<String> listClassNames() {
        List<String> classNames = new ArrayList<>();
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT name FROM class WHERE school_id = ? ORDER BY class_id DESC", new String[]{String.valueOf(DataBaseKariti.ID_ESCOLA)});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String className = cursor.getString(0);
                    classNames.add(className);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.e("kariti","Erro ao tentar listar nomes das turma! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return classNames;
    }


    public List<String> listClassByExam() {
        List<String>  classNames = new ArrayList<>();
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT name FROM class WHERE school_id = ? AND class_id IN (SELECT class_id FROM exam) ORDER BY class_id DESC", new String[]{String.valueOf(DataBaseKariti.ID_ESCOLA)});
            if (cursor != null && cursor.moveToFirst()) {
                do{
                    String className = cursor.getString(0);
                    classNames.add(className);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.e("kariti","Erro ao tentar listar turmas por prova! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return classNames;
    }

    public List<String> listExamNames(String class_id) {
        List<String>  examNames = new ArrayList<>();
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT name FROM exam WHERE class_id = ? ORDER BY date", new String[]{class_id});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String examName = cursor.getString(0);
                    examNames.add(examName);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.e("kariti","Erro ao tentar listar nomes das provas por turma! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return examNames;
    }

    /**
     * Este método obtém as notas de cada questão de uma prova.
     * @param exam_id codigo da prova que se deseja saber as notas das questões.
     * @return lista com as notas.
     * */
    public List<Float> listGradeByQuestion(Integer exam_id) {
        List<Float> grades = new ArrayList<>();
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT grade FROM answer_key WHERE exam_id = ? ORDER BY question", new String[]{exam_id.toString()});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    float grade = cursor.getFloat(0);
                    grades.add(grade);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.e("kariti","Erro ao tentar listar notas por questão! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return grades;
    }


    /**
     * Este método lista todas as escolas do banco de dados pertecentes a um determinado usuário
     * @param status parametro que determina se as escolas listadas serão as ativas ou as desativadas
     * @return retorna uma lista de string contendo todas as escolas pertencentes ao usuario
     * logado caso não tenha, retorna uma lista vazia.
     */
    public List<String> listSchoolNames(Integer status) {
        List<String> schoolNames = new ArrayList<>();
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT name FROM school WHERE user_id = ? AND status = ?  ORDER BY name ASC", new String[]{DataBaseKariti.USER_ID.toString(), status.toString()});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String school = cursor.getString(0);
                    schoolNames.add(school);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.e("kariti","Erro ao tentar listar escolas! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return schoolNames;
    }

    public List<String> listAnswerKeyString(Integer exam_id) {
        ArrayList<String> answerKeys = new ArrayList<>();
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT answer FROM answer_key WHERE exam_id = ? ORDER BY question ASC", new String[]{exam_id.toString()});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String answer = cursor.getString(0);
                    char r = (char) (Integer.parseInt(answer)-1+'A');
                    answerKeys.add(String.valueOf(r));
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.e("kariti","Erro ao tentar listar gabarito! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return answerKeys;
    }

    public List<String> listAnswerGivenString(Integer exam_id, Integer student_id) {
        List<String> answer_givens = new ArrayList<>();
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT answer_given FROM result WHERE exam_id = ? AND student_id = ? ORDER BY question ASC", new String[]{exam_id.toString(), student_id.toString()});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String aux = "";
                    String answer = cursor.getString(0);
                    if(!answer.equals("-1")){
                        if (answer.equals("0")) {
                            aux = "-";
                        } else {
                            List<String> resp = new ArrayList<>();
                            for (int i = 0; i < answer.length(); i++) {
                                if (!String.valueOf(answer.charAt(i)).equals("0")){
                                    String dupli = String.valueOf((char) (Integer.parseInt(String.valueOf(answer.charAt(i))) - 1 + 'A'));
                                    resp.add(dupli);
                                }
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                resp.sort((a, b) -> a.compareTo(b));
                            }else{
                                Collections.sort(resp, new Comparator<String>() {
                                    @Override
                                    public int compare(String o1, String o2) {
                                        return o1.compareTo(o2);
                                    }
                                });
                            }
                            aux = String.join("",resp);
                        }
                        answer_givens.add(aux);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.e("kariti","Erro ao tentar listar respostas dadas! "+e.getMessage());
            return null;
        } finally {
            if (base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if (cursor != null){
                cursor.close();
            }
        }
        return answer_givens;
    }

    public String listAnswerKeyInt(String exam_id) {
        String answerKeys = "";
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT answer FROM answer_key WHERE exam_id = ? ORDER BY question ASC", new String[]{exam_id});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String answer = cursor.getString(0);
                    answerKeys += answer;
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.e("kariti","Erro ao tentar listar respostas do gabarito de forma numérica! "+e.getMessage());
            return null;
        } finally {
            if (base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if (cursor != null){
                cursor.close();
            }
        }
        return answerKeys;
    }

    public List<Student> listStudentsData(Integer class_id) {
        List<Student>  students = new ArrayList<>();
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT student_id, name, email FROM student WHERE " +
                    "student_id IN (SELECT student_id FROM student_class WHERE class_id = ?) ORDER BY name ASC", new String[]{class_id.toString()});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Integer id_student = cursor.getInt(0);
                    String nameStudent = cursor.getString(1);
                    String email = cursor.getString(2);
                    Student student = new Student(id_student, nameStudent, email);
                    students.add(student);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.e("kariti","Erro ao tentar listar ids dos alunos por e turma! "+e.getMessage());
            return null;
        } finally {
            if(base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if(cursor != null){
                cursor.close();
            }
        }
        return students;
    }

    public List<Answer_key> listAnswerKeyData(Integer exam_id) {
        List<Answer_key> answerKeys = new ArrayList<>();
        SQLiteDatabase base_dados = null;
        Cursor cursor = null;
        try {
            base_dados = this.getReadableDatabase();
            cursor = base_dados.rawQuery("SELECT question, answer, grade FROM answer_key WHERE exam_id = ? ORDER BY question ASC", new String[]{exam_id.toString()});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int question = cursor.getInt(0);
                    int answer = cursor.getInt(1);
                    float grade = cursor.getFloat(2);
                    Answer_key g = new Answer_key(question,answer, grade);
                    answerKeys.add(g);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.e("kariti","Erro ao tentar listar respostas do gabarito de forma numérica! "+e.getMessage());
            return null;
        } finally {
            if (base_dados != null && base_dados.isOpen()){
                base_dados.close();
            }
            if (cursor != null){
                cursor.close();
            }
        }
        return answerKeys;
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
}