package online.padev.kariti.cards;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import online.padev.kariti.database.DataBaseKariti;
import online.padev.kariti.R;
import online.padev.kariti.download.DownloadPDF;
import online.padev.kariti.entity.Answer_key;
import online.padev.kariti.entity.Exam;
import online.padev.kariti.entity.Student;

public class CorrectionReportCard {

    Context context;
    DataBaseKariti dataBase;
    List<Answer_key> answerkey;
    List<Student> students;
    Exam exam;

    public CorrectionReportCard(Context context, DataBaseKariti dataBase, Integer id_provaBD) {
        this.context = context;
        this.dataBase = dataBase;
        exam = new Exam(id_provaBD, dataBase);
        answerkey = dataBase.listAnswerKeyData(id_provaBD);
    }

    public boolean generateCorrectionReport(int typeReport){
        try {
            if (typeReport == 0) {
                // ========== Lista todos os aluno pertencentes a turma (independente se prova corrigida ou não) ==========================
                students = dataBase.listStudentsData(exam.getClass_id());
            } else {
                // ========== Lista apenas alunos com a prova corrigida pertencentes a turma ==============================
                students = dataBase.listStudentExamCorrected(exam.getClass_id());
            }

            // =========== Lista auxiliar para armazenar alunos com provas não corrigidas ===================================
            List<Student> studentsNotCorrect = new ArrayList<>();

            int studentI = 0; // Controla o fluxo da lista 'student'
            int studentO = 0; // Controla o fluxo da lista 'studentsNotCorrect'

            //Criar um novo documento PDF
            PdfDocument pdfDocument = new PdfDocument();

            // ============= Tamanho da página ================================================

            int pageWidth = 1754;
            int pageHeight = 1240;
            int limitPage = 20; // limite de alunos por páginas

            if (exam.getNumQuestions() <= 10) {
                pageWidth = 1240;
                pageHeight = 1754;
                limitPage = 30;
            }

            // ============== Cálculo para controle de página (cada página só comporta o resultado de 20 alunos)
            int numPages = students.size() / limitPage;
            if (students.size() % limitPage != 0) {
                numPages++;
            }

            // =================== Carrega os icones ======================================================
            Bitmap bitmap_icon_correct = getIconCorrect(R.drawable.correct_icon);
            Bitmap bitmap_iconIncorrect = getIconCorrect(R.drawable.incorrect_icon);
            Bitmap icon_resized_correct = Bitmap.createScaledBitmap(bitmap_icon_correct, 20, 20, false);
            Bitmap icon_resized_incorrect = Bitmap.createScaledBitmap(bitmap_iconIncorrect, 20, 20, false);

            for (int i = 0; i < numPages; i++) {
                int controllerPage = 0; // Controla o fluxo de cada página (garante a inserção do resultado de limitPage alunos por página)

                // ============== Inicia uma nova página ==============================================
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                Canvas canvas = page.getCanvas();

                // =============== Fonte do cabeçalho ==========================================================================
                Paint paintText = new Paint();
                paintText.setAntiAlias(true);
                paintText.setTypeface(Typeface.create("DejaVu Sans", Typeface.NORMAL));
                paintText.setColor(Color.BLACK);
                paintText.setTextSize(23);

                if (i == 0) { // Garante que o cabeçalho seja desenhado apenas na primeira página

                    // ================ Monta o Cabeçalho da Prova ====================================================================
                    canvas.drawText("Prova(a): " + exam.getNameExam(), 80, 85, paintText);
                    canvas.drawText("Turma: " + dataBase.getClassName(exam.getClass_id().toString()), 80, 115, paintText);
                    canvas.drawText("Professor(a): " + dataBase.getUserName(), 80, 145, paintText);
                }

                // ================== Fonte das linhas da tabela =============================================
                Paint paintLine = new Paint();
                paintLine.setColor(Color.BLACK);
                paintLine.setStrokeWidth(1);

                // ======================== Iniciando cabeçalho da tabela ==========================================
                int startX = 80;
                int startY = 180;
                int stopX = 480 + (exam.getNumQuestions() * 50);

                // ======================= Desenhando campos do cabeçalho =========================
                canvas.drawLine(startX, startY, stopX, startY, paintLine); // desenha primeira linha na horizontal
                canvas.drawLine(480, startY + 40, stopX, startY + 40, paintLine); // desenha a segunda linha na horizontal
                canvas.drawLine(startX, startY + 80, stopX, startY + 80, paintLine); // desenha a terceira linha na horizontal
                canvas.drawLine(startX, startY, startX, startY + 80, paintLine); // desenha primeira linha na vestical
                canvas.drawLine(380, startY, startX + 300, startY + 80, paintLine); // desenha segunda linha na vestical
                canvas.drawLine(480, startY, 480, startY + 80, paintLine); // desenha a terceira linha na vertical
                canvas.drawLine(stopX, startY, stopX, startY + 80, paintLine); // desenha a quarta linha na vestical

                // ==================== Desenhando informações no cabeçalho desenhado =============================
                canvas.drawText("ALUNO(A)", 130, startY + 50, paintText);
                canvas.drawText("NOTA", 400, startY + 50, paintText);
                canvas.drawText("RESUMO", (float) (430 + ((stopX - 480) / 2)), startY + 30, paintText);

                // =================== Desenha o número das questões no cabeçalho ==============================
                paintText.setTextSize(20);
                startX = 480;
                startY = 220;
                for (int q = 1; q <= exam.getNumQuestions(); q++) {
                    canvas.drawText("Q" + q, startX + 7, startY + 25, paintText);
                    startX += 50;
                    canvas.drawLine(startX, startY, startX, startY + 40, paintLine);
                }

                // =================== Desenha resultado de correção por aluno ===================================
                startX = 80;
                startY = 260;

                if (studentO == 0) {
                    for (int al = studentI; al < students.size(); al++) {
                        if (controllerPage == limitPage) break;
                        studentI += 1;
                        boolean checkIsCorrect = dataBase.checkIfExamStudentCorrected(exam.getExam_id(), students.get(al).getId_student());
                        if (!checkIsCorrect) {
                            studentsNotCorrect.add(students.get(al));
                            continue;
                        }

                        String studentName = students.get(al).getNameStudent();
                        canvas.drawLine(startX, startY, startX, startY + 40, paintLine); // desenha primeira linha na vestical
                        canvas.drawText(formatNameStudent(studentName), startX + 10, startY + 30, paintText);
                        canvas.drawLine(380, startY, 380, startY + 40, paintLine); // desenha segunda linha na vestical
                        canvas.drawLine(480, startY, 480, startY + 40, paintLine);
                        List<String> studentResponses = dataBase.listAnswerGivenString(exam.getExam_id(), students.get(al).getId_student());
                        int startAnswersX = 480;
                        float note = 0;
                        for (int r = 0; r < exam.getNumQuestions(); r++) {
                            String resp = formatResponse(studentResponses.get(r));
                            canvas.drawText(resp, startAnswersX + 5, startY + 30, paintText);
                            boolean isCorrect = isCorrectResponse(resp, r);
                            if (isCorrect) {
                                canvas.drawBitmap(icon_resized_correct, startAnswersX + 30, startY, null);
                                note += answerkey.get(r).getNote();
                            } else {
                                canvas.drawBitmap(icon_resized_incorrect, startAnswersX + 30, startY, null);
                            }
                            startAnswersX += 50;
                            canvas.drawLine(startAnswersX, startY, startAnswersX, startY + 40, paintLine);
                        }
                        canvas.drawText(String.format("%.2f", note), 390, startY + 30, paintText);
                        startY += 40;
                        canvas.drawLine(startX, startY, stopX, startY, paintLine);
                        controllerPage += 1;
                    }
                }

                // ============== Insere os alunos com provas não corrigidas ========================================
                if (!studentsNotCorrect.isEmpty() && studentI >= students.size()) { // Entra na estrutura apenas se houver alunos com provas não corrigidas e o laço anterior de inserção de alunos com provas corrigidas tiver encerrado
                    for (int al = studentO; al < studentsNotCorrect.size(); al++) {
                        if (controllerPage >= limitPage) break;
                        canvas.drawLine(startX, startY, startX, startY + 40, paintLine); // desenha primeira linha na vestical
                        canvas.drawText(studentsNotCorrect.get(al).getNameStudent(), startX + 10, startY + 30, paintText);
                        canvas.drawLine(380, startY, 380, startY + 40, paintLine); // desenha segunda linha na vestical
                        canvas.drawText(" - ", 420, startY + 30, paintText);
                        canvas.drawLine(480, startY, 480, startY + 40, paintLine);
                        int startAnswersX = 480;
                        for (int r = 0; r < exam.getNumQuestions(); r++) {
                            canvas.drawText("-", startAnswersX + 20, startY + 28, paintText);
                            startAnswersX += 50;
                            canvas.drawLine(startAnswersX, startY, startAnswersX, startY + 40, paintLine);
                        }
                        startY += 40;
                        canvas.drawLine(startX, startY, stopX, startY, paintLine);
                        controllerPage += 1;
                        studentO += 1;
                    }

                }

                // =============== Desenha a tabela do gabarito =================================
                startY = startY + 40;
                startX = 300;
                canvas.drawLine(startX, startY, stopX, startY, paintLine); // desenha a primeira linha na horizontal
                canvas.drawLine(startX, startY, startX, startY + 80, paintLine); // desenha a primeira linha na vestical
                canvas.drawLine(startX, startY + 40, stopX, startY + 40, paintLine); // desenha a segunda linha na horizontal
                canvas.drawLine(startX, startY + 80, stopX, startY + 80, paintLine); // desenha a terceira linha na horizontal
                canvas.drawLine(startX + 180, startY, startX + 180, startY + 80, paintLine); // desenha a segunda linha na vestical

                // =============== Desenha os daddos do Gabarito =================================
                canvas.drawText("GABARITO", startX + 20, startY + 28, paintText);
                canvas.drawText("NOTA", startX + 20, startY + 68, paintText);

                startX = 480;

                Paint paintQuest = new Paint();
                paintQuest.setTextSize(14);

                for (Answer_key g : answerkey) {
                    char resp = (char) ('A' + g.getResponse() - 1);
                    float n = g.getNote();
                    canvas.drawText(String.valueOf(resp), startX + 5, startY + 30, paintText);
                    canvas.drawText(String.valueOf(n), startX + 5, startY + 70, paintText);
                    canvas.drawText(String.valueOf(g.getQuestion()), startX + 30, startY + 35, paintQuest);
                    canvas.drawLine(startX + 50, startY, startX + 50, startY + 80, paintLine);
                    startX += 50;
                }

                // ========================= Adiciona texto no final da pagina ==================================
                paintText.setTextSize(16);
                canvas.drawText("Gerado por: Kariti", 60, pageHeight - 34, paintText);

                // ======================== Finaliza a pagina ==================================================
                pdfDocument.finishPage(page);
            }

            String fileName = "Relatorio_"+ exam.getNameExam()+"_"+dataHoraAtual()+".pdf";

            DownloadPDF downloadPDF = new DownloadPDF(context);
            downloadPDF.newDownload(pdfDocument, fileName);

            /*

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                // Salvar arquivo
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    pdfDocument.writeTo(fos);
                    pdfDocument.close();
                } catch (IOException e) {
                    Log.e("card", "Erro: " + e);
                    return false;
                }
            } else {
                ContentResolver resolver = context.getContentResolver(); // Usando o contexto fornecido
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);


                Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);

                try {
                    if (uri != null) {
                        OutputStream outputStream = resolver.openOutputStream(uri);
                        pdfDocument.writeTo(outputStream);
                        pdfDocument.close();
                        notifyDownloadComplete(fileName, uri);
                    }
                } catch (Exception e) {
                    Log.e("kariti", e.getMessage());
                    return false;
                }
            }

             */
            return true;
        } catch (Exception e){
            Log.e("kariti", e.toString());
            return false;
        }
    }
    /*
    private void notifyDownloadComplete(String fileName, Uri fileUri) {
        // Criar um canal de notificação (Android 8.0 e superior)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            String channelId = "download_channel";
            CharSequence name = "Download Notifications";
            String description = "Notificações sobre downloads";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Criar PendingIntent para abrir o arquivo PDF
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        // Criar e exibir a notificação
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "download_channel")
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle("Download Completo")
                .setContentText("O arquivo " + fileName + " foi baixado com sucesso!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true).setContentIntent(pendingIntent); // A notificação desaparece quando o usuário a toca

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }

     */
    private String dataHoraAtual(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        Date date = new Date();
        return sdf.format(date);
    }
    // Método para converter um drawable em um Bitmap
    private Bitmap getIconCorrect(int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable == null) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
    private String formatResponse(String response){
        if (response.length() > 2) {
            response = response.substring(0, 2) + "+";
        }
        return response;
    }

    private boolean isCorrectResponse(String resp, int position){
        Answer_key g = answerkey.get(position);
        char respCorrect = (char) ('A' + g.getResponse() -1);
        return resp.equals(String.valueOf(respCorrect));
    }

    private String formatNameStudent(String name){
        int nameLength = name.length();
        if (nameLength > 30){
            name = name.substring(0 , 27) + "...";
        }
        return name;
    }
}