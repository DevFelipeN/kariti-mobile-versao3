package online.padev.kariti.cards;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfDocument.PageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import online.padev.kariti.BancoDados;
import online.padev.kariti.utilities.Prova;

public class CreatCard {
    Context context;
    Integer id_provaBD;
    Prova prova;
    private double nota;
    private String className, teacher;
    Map<Integer, String> students;

    public CreatCard(Integer id_provaBD, String className, BancoDados bancoDados, Context context){
        this.id_provaBD = id_provaBD;
        this.className = className;
        this.teacher = bancoDados.pegarNomeUsuario(BancoDados.USER_ID);
        prova = new Prova(id_provaBD, bancoDados);
        students = bancoDados.listarAlunosPorTurma(prova.getId_turma());
        nota = bancoDados.pegarNotaProva(id_provaBD.toString());
        this.context = context;
    }

    public CreatCard(int numQuests, int numAlternatives){
        prova = new Prova();
        prova.setNumQuestoes(numQuests);
        prova.setNumAlternativas(numAlternatives);

    }


    public boolean creatPdfCard(){

        try {

            //Criar documento PDF
            PdfDocument pdfDocument = new PdfDocument();

            for (Map.Entry<Integer, String> student : students.entrySet()) {

                Bitmap qrCode = LibQr.createQrCode(id_provaBD+"."+student.getKey(), context);

                // ============ Cria uma página A4 (1240 x 1240 px) ===========================================================
                PageInfo pageInfo = new PageInfo.Builder(1240, 1754, 1).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                Canvas canvas = page.getCanvas();

                // =============== desenha o QrCode no pdf =========================================================
                canvas.drawBitmap(qrCode, 900, 45, null);

                // =============== fonte do cabeçalho ==========================================================================
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setTypeface(Typeface.create("DejaVu Sans", Typeface.NORMAL));
                paint.setColor(Color.BLACK);
                paint.setTextSize(26);

                // ================ Monta o Cabeçalho da Prova ====================================================================
                canvas.drawText("Aluno(a): " + student.getValue(), 80, 85, paint);
                canvas.drawText("Professor(a): " + teacher, 80, 115, paint);
                canvas.drawText("Prova: " + prova.getNomeProva(), 80, 145, paint);
                canvas.drawText("Turma: " + className, 80, 175, paint);
                canvas.drawText("Data: " + prova.dateToDisplay(), 80, 205, paint);

                // ================== fonte das linhas =============================================
                Paint paintLine = new Paint();
                paintLine.setColor(Color.BLACK);
                paintLine.setStrokeWidth(1);

                // ======================== Desenha a primeira linha ==========================================
                canvas.drawLine(80, 280, 890, 280, paintLine);

                // ========================= Desenha a segunda linha ==========================================
                paintLine.setStrokeWidth(2); // Aumenta a espessura da linha
                canvas.drawLine(80, 560, 1160, 560, paintLine);

                // ======================== Adiciona 'Peso Total' abaixo do primeiro retângulo =================
                canvas.drawText("Peso total da prova", 330, 475, paint);

                // ======================== Adiciona 'Nota do Aluno' abaixo do segundo retângulo =================
                canvas.drawText("Nota do Aluno", 660, 475, paint);

                // ========================= Adiciona o texto abaixo da primeira linha =========================
                paint.setTextSize(16);
                canvas.drawText("Nome do Aluno", 80, 300, paint);

                // ======================== Adiciona mensagem de atenção ========================================
                canvas.drawText("ATENÇÃO: NÃO RASURE ESTE CARTÃO", 80, 550, paint);

                // ===================== fonte dos retângulos ==================================================
                Paint paintRectangle = new Paint();
                paintRectangle.setColor(Color.BLACK);
                paintRectangle.setStyle(Paint.Style.STROKE);
                paintRectangle.setStrokeWidth(2);

                // ==================== retângulo do peso total da prova ========================================
                canvas.drawRect(330, 320, 560, 450, paintRectangle);

                // ==================== Adiciona a nota total dentro do retângulo =====================================
                paint.setTextSize(72);
                if (nota >= 100) {
                    canvas.drawText(String.format("%.2f", nota), 340, 410, paint);
                } else if (nota >= 10) {
                    canvas.drawText(String.format("%.2f", nota), 360, 410, paint);
                } else {
                    canvas.drawText(String.format("%.2f", nota), 380, 410, paint);
                }

                // ===================== retângulo para a nota do aluno =========================================
                canvas.drawRect(660, 320, 890, 450, paintRectangle);


                // Adicionar linhas e questões (Para 20 questões ou menos)
                int startQuestY = 720; // Onde começa as questões em Y
                int questionSpacing = 43;  // Espaçamento entre as questões
                int alternativeSpacing = 114;
                int startQueresAltX = 272; // onde começa o quadrado das alternativas em x
                int startOptionX = 274; // onde começa as alternativas em x
                // Define o raio do círculo
                float circleRadius = 13;

                Paint paintLetters = new Paint(); // Cor das letras e numeros
                paintLetters.setColor(Color.LTGRAY);
                paintLetters.setTextSize(20);

                // ================= fonte dos circulos das alternativas =======================================
                Paint paintCircle = new Paint();
                paintCircle.setColor(Color.LTGRAY);
                paintCircle.setStyle(Paint.Style.STROKE); // Apenas contorno
                paintCircle.setStrokeWidth(2); // Espessura da borda do círculo

                int questsAtual = 0;
                if (prova.getNumQuestoes() > 20) {
                    questsAtual = 20;
                    alternativeSpacing = 60;
                    startQueresAltX = 242;
                    startOptionX = 244;
                } else {
                    questsAtual = prova.getNumQuestoes();
                }

                // ===================== Adiciona os quadrados das alternativas ====================================
                for (int alt = 1; alt <= prova.getNumAlternativas(); alt++) {
                    canvas.drawRect(startQueresAltX, 653, startQueresAltX + 20, 673, paint);
                    startQueresAltX += alternativeSpacing;
                }

                // ========== cria um array contendo letras ====================
                char[] letters = {'A', 'B', 'C', 'D', 'E', 'F'};

                // ===================== Adiciona os quadrados das questões, numeros e alternativas ========================
                for (int i = 1; i <= questsAtual; i++) {
                    canvas.drawRect(140, startQuestY - 20, 160, startQuestY, paint); //Quadrado da questão

                    canvas.drawText(i + "", 170, startQuestY, paintLetters); // Número da questão

                    int optionX = startOptionX;
                    for (int a = 0; a < prova.getNumAlternativas(); a++) {
                        canvas.drawText(letters[a] + "", optionX, startQuestY, paintLetters); // Alternativas

                        // Ajusta a posição do círculo para centralizar a letra
                        float circleX = optionX + 7;
                        float circleY = startQuestY - 7;

                        // Desenha o círculo ao redor da letra
                        canvas.drawCircle(circleX, circleY, circleRadius, paintCircle);

                        optionX += alternativeSpacing;
                    }

                    startQuestY += questionSpacing;
                }

                // =========== Caso o número de questões seja maior que 20 questões =========================================
                if (prova.getNumQuestoes() > 20) {

                    startQueresAltX = 753;

                    // ===================== Adiciona os quadrados das alternativas na direita da página  ====================================
                    for (int alt = 1; alt <= prova.getNumAlternativas(); alt++) {
                        canvas.drawRect(startQueresAltX, 653, startQueresAltX + 20, 673, paint);
                        startQueresAltX += alternativeSpacing;
                    }

                    // ===================== Adiciona os quadrados das questões, numeros e alternativas na direita da página ========================
                    startQuestY = 720; // Onde começa as questões em Y
                    for (int i = 21; i <= prova.getNumQuestoes(); i++) {
                        canvas.drawRect(651, startQuestY - 20, 671, startQuestY, paint);

                        canvas.drawText(i + "", 681, startQuestY, paintLetters); // Número da questão

                        startOptionX = 756;
                        for (int a = 0; a < prova.getNumAlternativas(); a++) {
                            canvas.drawText(letters[a] + "", startOptionX, startQuestY, paintLetters); // Alternativas

                            // Ajusta a posição do círculo para centralizar a letra
                            float circleX = startOptionX + 7;
                            float circleY = startQuestY - 7;

                            // Desenha o círculo ao redor da letra
                            canvas.drawCircle(circleX, circleY, circleRadius, paintCircle);

                            startOptionX += alternativeSpacing;
                        }

                        startQuestY += questionSpacing;
                    }
                }

                // =============== Adiciona os 4 marcadores na página (circulo dentro de circulo) ============================

                // =============== fonte do circulo interno ===================
                Paint paintCircleInt = new Paint();
                paintCircleInt.setColor(Color.BLACK);
                paintCircleInt.setStyle(Paint.Style.FILL);
                float radiousInt = 10;


                // =============== fonte do circulo externo ===================
                Paint paintCircleExt = new Paint();
                paintCircleExt.setColor(Color.BLACK);
                paintCircleExt.setStyle(Paint.Style.STROKE);
                paintCircleExt.setStrokeWidth(8);
                float radiusExt = 25;

                // =============== esquerda superior ==================
                canvas.drawCircle(107, 630, radiusExt, paintCircleExt);
                canvas.drawCircle(107, 630, radiousInt, paintCircleInt);

                // =============== direita superior ===================
                canvas.drawCircle(1129, 630, radiusExt, paintCircleExt);
                canvas.drawCircle(1129, 630, radiousInt, paintCircleInt);

                // =============== esquerda inferior ==================
                canvas.drawCircle(107, 1598, radiusExt, paintCircleExt);
                canvas.drawCircle(107, 1598, radiousInt, paintCircleInt);

                // =============== direita inferior  ==================
                canvas.drawCircle(1129, 1598, radiusExt, paintCircleExt);
                canvas.drawCircle(1129, 1598, radiousInt, paintCircleInt);

                pdfDocument.finishPage(page);
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                // Salvar arquivo
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "prova-teste.pdf");
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    pdfDocument.writeTo(fos);
                    pdfDocument.close();
                } catch (IOException e) {
                    Log.e("card", "Erro: " + e.toString());
                }
            } else {
                ContentResolver resolver = context.getContentResolver(); // Usando o contexto fornecido
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Downloads.DISPLAY_NAME, "prova-teste.pdf");
                contentValues.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);


                Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);

                try {
                    if (uri != null) {
                        OutputStream outputStream = resolver.openOutputStream(uri);
                        pdfDocument.writeTo(outputStream);
                        pdfDocument.close();
                        notifyDownloadComplete("prova-teste.pdf", uri);
                    }
                } catch (Exception e) {
                    Log.e("kariti", e.getMessage());
                    return false;
                }
            }
            return true;
        }catch (Exception e){
            Log.e("kariti", e.toString());
            return false;
        }
    }
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

}
