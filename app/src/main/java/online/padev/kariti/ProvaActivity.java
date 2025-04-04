package online.padev.kariti;

import static online.padev.kariti.correction.CoreKariti.listCartoes;
import static online.padev.kariti.utils.EnhanceImage.enhanceImage;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import online.padev.kariti.utils.BitmapLuminanceSource;
import online.padev.kariti.correction.Circle;
import online.padev.kariti.correction.CoreKariti;
import online.padev.kariti.entity.Prova;
import online.padev.kariti.database.DataBaseKariti;

public class ProvaActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_OPEN_DOCUMENT = 100;
    ImageButton back, iconHelp;
    Button btnRegistrationProva, btnGenerateCard, btnToCorrectProva, btnViewProvas, btnEditProva;
    DataBaseKariti dataBaseKariti;
    TextView textViewTitle;
    Integer id_provaCaptured;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova);

        back = findViewById(R.id.imgBtnVoltaDescola);
        iconHelp = findViewById(R.id.iconHelp);
        btnRegistrationProva = findViewById(R.id.buttonCadProva);
        btnGenerateCard = findViewById(R.id.buttonGerarCatao);
        btnToCorrectProva = findViewById(R.id.buttonCorrigirProva);
        btnViewProvas = findViewById(R.id.buttonVisuProva);
        btnEditProva = findViewById(R.id.buttonEdicaoProva);
        textViewTitle = findViewById(R.id.toolbar_title);

        dataBaseKariti = new DataBaseKariti(this);

        textViewTitle.setText(String.format("%s","Prova"));

        iconHelp.setOnClickListener(v -> dialogHelp());
        btnRegistrationProva.setOnClickListener(v -> startRegistrationProva());
        btnGenerateCard.setOnClickListener(v -> startGenerateCard());
        btnViewProvas.setOnClickListener(v -> startViewProvas());

        btnToCorrectProva.setOnClickListener(v -> {
            btnToCorrectProva.setEnabled(false);
            try {
                Boolean checkExistsProva = dataBaseKariti.verificaExisteProvaCadastrada();
                if (checkExistsProva == null) {
                    Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (checkExistsProva) {
                    displayOptionsToCorrect();
                } else {
                    notice("provas cadastradas");
                }
            } catch (Exception e){
                Log.e("kariti", e.toString());
            } finally {
                btnToCorrectProva.setEnabled(true);
            }
        });
        back.setOnClickListener(v -> finish());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }
    private void startRegistrationProva(){
        btnRegistrationProva.setEnabled(false);
        try {
            Boolean checkExistsClass = dataBaseKariti.verificaExisteTurmas();
            if (checkExistsClass == null) {
                Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                return;
            }
            if (checkExistsClass) {
                Intent intent = new Intent(this, ProvaRegistrationActivity.class);
                startActivity(intent);
            } else notice("turmas cadastradas");
        } catch (Exception e) {
            Log.e("kariti", e.toString());
        } finally {
            btnRegistrationProva.setEnabled(true);
        }
    }
    private void startGenerateCard(){
        btnGenerateCard.setEnabled(false);
        try {
            Boolean checkExistsProva = dataBaseKariti.verificaExisteProvaCadastrada();
            if (checkExistsProva == null) {
                Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                return;
            }
            if (checkExistsProva) {
                Intent intent = new Intent(this, ProvaGenerateCardRegisteredActivity.class);
                intent.putExtra("endereco", 2);
                startActivity(intent);
            } else {
                notice("provas cadastradas");
            }
        } catch (Exception e){
            Log.e("kariti", e.toString());
        } finally {
            btnGenerateCard.setEnabled(true);
        }
    }
    private void startViewProvas(){
        btnViewProvas.setEnabled(false);
        try {
            Boolean checkExistsProva = dataBaseKariti.verificaExisteProvaCadastrada();
            if (checkExistsProva) {
                Intent intent = new Intent(this, ProvaViewActivity.class);
                startActivity(intent);
            } else {
                notice("provas cadastradas");
            }
        } catch (Exception e){
            Log.e("kariti", e.toString());
        } finally {
            btnViewProvas.setEnabled(true);
        }
    }
    private void notice(String descricao){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Atenção!");
        builder.setMessage("Não encontramos "+descricao+" para essa escola. Para ter acesso a essa opção é necessário ter "+descricao+".");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void dialogHelp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setMessage("Tela principal de prova.\n\n" +
                "• Cadastrar Prova - Selecionando essa opção, será derecionado a tela que solicita as informações necessárias para elaboração da prova e, em seguida solicita o preenchimento do gabarito.\n\n" +
                "• Gerar Cartões - Nesta opção é realizado o download dos cartões resposta de uma prova já cadastrada na opção anterior.\n\n" +
                "• Corrigir Prova - Após selecionada essa opção, basta realizar os passos sugeriodos pelo KARITI, iniciar correção clicando no botão 'Scannear Cartão', capturar o QrCode da prova e capturar a imagem do cartão resposta, em seguida são listadas as provas capuradas na próxima tela, onde, são sugeridas duas opções, continuar capturando mais provas ou finalizar a correção.\n\n" +
                "• Visualizar Prova - Nesta opção pode ser visualizado o resultado da correção das provas informando a quantidade de acertos e nota de cada aluno.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void displayOptionsToCorrect() {
        // Inflar o layout customizado
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.correction_with_opencv, null);

        // Inicializar os elementos do layout
        FloatingActionButton closedCorrect = dialogView.findViewById(R.id.btnvoltarOpencv);
        Button buttonCameraOpenCV = dialogView.findViewById(R.id.buttonCameraopenCv);
        Button buttonFileDevice = dialogView.findViewById(R.id.buttonDispositivo);

        // Criar o AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(dialogView);
        // Mostrar o diálogo
        AlertDialog dialog = builder.create();
        dialog.show();

        buttonCameraOpenCV.setOnClickListener(v -> {
            notifyCorrectionOrganization();
            dialog.dismiss();
        });

        buttonFileDevice.setOnClickListener(v -> {
            openFiles();
            dialog.dismiss();});

        closedCorrect.setOnClickListener(v -> dialog.dismiss());
    }

    private void openFiles(){
        try {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
                    "image/*",
                    "application/pdf",
                    "application/zip",
            });
            intent.addCategory(Intent.CATEGORY_OPENABLE); // Garante que apenas arquivos abertos sejam exibidos
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, REQUEST_CODE_OPEN_DOCUMENT);
        }catch (Exception e){
            Log.e("ERRO", "ERRO AQUI11!!: "+e.toString());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("tempo", "Inicio");
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(){
            @Override
            public void run() {
                super.run();
                //mensagem(handler, "Correção em andamento...");
                processFiles(requestCode, resultCode, data, handler);
            }
        }.start();
        if(data != null){
            startAnimationCorrect();
        }
    }
    private void processFiles(int requestCode, int resultCode, @Nullable Intent data, Handler handler){
        try {
            if (requestCode == REQUEST_CODE_OPEN_DOCUMENT && resultCode == RESULT_OK) {
                if (data != null) {
                    //Caso mais de um arquivo seja selecionado
                    if (data.getClipData() != null) { // Múltiplos arquivos
                        int count = data.getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            Uri uri = data.getClipData().getItemAt(i).getUri();
                            analysisFileType(uri);
                        }
                    }
                    //Caso somente um arquivo seja selecionado
                    if (data.getData() != null) {
                        Uri uri = data.getData();
                        analysisFileType(uri);
                    }
                }else{
                    return;
                }
            }else{
                return;
            }
            AnimationCorrectionActivity.encerra("Correcao finalizada");
            notifyFinallyCorrection(handler);
        }catch (Exception e){
            Log.e("ERRO", "ERRO AQUI44!!: "+e);
        }
    }
    private void notifyFinallyCorrection(Handler handler){
        if (!isFinishing() && !isDestroyed()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProvaActivity.this);
                    builder.setTitle("Correção Finalizada");
                    builder.setMessage("O Resultado da correção pode ser visualizado na opção 'Visualizar Provas'");
                    builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                    builder.show();
                }
            });
        }
    }

    public void processImage(Bitmap bitmap){
        try {
            //Converte para Mat
            Mat mat = new Mat();
            org.opencv.android.Utils.bitmapToMat(bitmap, mat);

            Mat matToWarp = mat.clone();//Imagem para pintar os circulos encontrados de branco e aplicar o corte
            Mat matAux = mat.clone();//Imagem para ser desenhado os contornos

            if (matToWarp.channels() != 3) {
                Imgproc.cvtColor(matToWarp, matToWarp, Imgproc.COLOR_RGBA2RGB);
                Imgproc.cvtColor(matAux, matAux, Imgproc.COLOR_RGBA2RGB);
            }

            //Aumenta o brilho e contranste da imagem
            Mat matEnhanced = enhanceImage(matAux);
            if(matEnhanced == null){
                return;
            }


            // Converte para escala de cinza e aplica o desfoque
            Mat gray = new Mat();
            Imgproc.cvtColor(matEnhanced, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.GaussianBlur(gray, gray, new Size(5, 5), 0);

            // Binariza a imagem
            Mat binaryImage = new Mat();
            Imgproc.threshold(gray, binaryImage, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

            // Encontrar contornos na imagem
            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(binaryImage, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

            List<Circle> circulos = new ArrayList<>();

            //Seleciona os circulos encontrados
            for (MatOfPoint contour : contours) {
                Point center = new Point();
                float[] radius = new float[1];
                Imgproc.minEnclosingCircle(new MatOfPoint2f(contour.toArray()), center, radius);

                double areaContour = Imgproc.contourArea(contour);
                double areaCircle = Math.PI * Math.pow(radius[0], 2);

                if (areaCircle > 0) {
                    double circularity = areaContour / areaCircle;
                    if (circularity >= 0.85) {
                        Rect boundingRect = Imgproc.boundingRect(contour);
                        Circle circle = new Circle(center.x, center.y, radius[0], boundingRect.x, boundingRect.y, boundingRect.width, boundingRect.height, contour, Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true));
                        circulos.add(circle);
                        //Imgproc.drawContours(matAux, Collections.singletonList(contour), -1, new Scalar(0, 255, 0), 1);
                    }
                }
            }

            // Ordenar círculos por tamanho do raio em ordem decrescente
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                circulos.sort((a, b) -> Double.compare(b.radius, a.radius));
            }else{
                Collections.sort(circulos, new Comparator<Circle>() {
                    @Override
                    public int compare(Circle o1, Circle o2) {
                        //return (int) (o2.radius - o1.radius);
                        return Double.compare(o2.radius, o1.radius);
                    }
                });
            }

            int circ = 0;
            List<Point> circlesInterest = new ArrayList<>();

            //Seleciona os circulos de interesse (circulos que possuam outro circulo dentro)
            boolean[] used = new boolean[circulos.size()];
            for (int i = 0; i < circulos.size() - 1; i++) {
                if (used[i]){
                    continue;
                }
                used[i] = true;
                Circle circExt = circulos.get(i);
                int contador = 0;
                for (int j = i + 1; j < circulos.size(); j++){
                    if (used[j]){
                        continue;
                    }
                    Circle circInt = circulos.get(j);
                    if (isInside(circExt, circInt)) {
                        //Imgproc.drawContours(matAux, Collections.singletonList(circInt.contour), -1, new Scalar(0, 0, 255), 1);
                        used[j] = true;
                        contador++;
                    }
                }
                if (contador >= 1) {
                    circlesInterest.add(new Point(circExt.x, circExt.y));
                    circ++;
                    //Imgproc.drawContours(matAux, Collections.singletonList(circExt.contour), -1, new Scalar(255, 0, 0), -1);
                    //Point center = new Point(circExt.x, circExt.y);
                    //Imgproc.circle(matToWarp, center, (int) (circExt.radius + circExt.radius * 0.8), new Scalar(255, 255, 255), -1);
                }
            }

            String resultQrCode = "";
            String nameCartao = "";

            if (circ == 4){
                List<Point> listOrganized = organize(circlesInterest);
                //Bitmap imgToQrCode = matToBitmap(mat);
                String textQrCode = scanQRCodeFromBitmap(bitmap);
                if(textQrCode != null && String.valueOf(textQrCode.charAt(0)).equals("#")){
                    Mat matWarp = warp(matToWarp, listOrganized); //realiza o corte da imagem
                    resultQrCode = processeQrCode(textQrCode);
                    String[] a = resultQrCode.split("_");
                    id_provaCaptured = Integer.parseInt(a[0]);

                    if(!dataBaseKariti.verificaExisteProvaPId(id_provaCaptured)){
                        //runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Prova não cadastrada!", Toast.LENGTH_SHORT).show());
                        Log.e("kariti","Prova não cadastrada!!");
                        return;
                    }

                    Prova prova = new Prova(id_provaCaptured, dataBaseKariti);

                    //Versão 3
                    CoreKariti core = new CoreKariti(matWarp, prova, dataBaseKariti, Integer.parseInt(a[1]));
                    HashMap<Integer, Integer> isCorrect = core.correctCard(); // Versão 3: corrigindo com o Kariti Mobile

                    if (isCorrect != null) {
                        nameCartao = resultQrCode + "_" + prova.getNumQuestions() + "_" + prova.getNumAlternatives();
                        String n = nameCartao + ".png";
                        if (!listCartoes.contains(n)) {
                            listCartoes.add(n);
                        }
                    }
                }
            }
        }catch (Exception e){
            Log.e("ERRO", "ERRO AQUI55!!: "+e);
        }

    }

    public static boolean isInside(Circle circExt, Circle circInt) {
        double xInt = circInt.x, yInt = circInt.y;
        double xExtI = circExt.xR, yExtI = circExt.yR;
        double xExtF = xExtI + circExt.wR, yExtF = yExtI + circExt.hR;

        return !(xInt < xExtI || xInt > xExtF || yInt < yExtI || yInt > yExtF);
    }
    private List<Point> organize(List<Point> listaInteresse){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            listaInteresse.sort((a, b) -> Double.compare(b.y, a.y));
        }else{
            Collections.sort(listaInteresse, new Comparator<Point>() {
                @Override
                public int compare(Point o1, Point o2) {
                    return (int) (o2.y - o1.y);
                }
            });
        }
        if(listaInteresse.get(0).x < listaInteresse.get(1).x){
            Collections.swap(listaInteresse, 0, 1);
        }
        if(listaInteresse.get(2).x > listaInteresse.get(3).x){
            Collections.swap(listaInteresse, 2, 3);
        }
        return listaInteresse;
    }
    private String scanQRCodeFromBitmap(Bitmap bitmap) {
        String qrCodeResult = null;
        try {
            // Converte o Bitmap para um BinaryBitmap
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BitmapLuminanceSource(bitmap)));

            // Inicializa o leitor de QR Code
            MultiFormatReader reader = new MultiFormatReader();
            Result result = reader.decode(binaryBitmap);

            // Extrai o texto do QR Code, caso encontrado
            qrCodeResult = result.getText();

        } catch (Exception e) {
            Log.e("QRcode", e.toString());
        }
        return qrCodeResult;
    }
    private Mat warp(Mat imgMat, List<Point> pontosInteresse){
        Point[] pointsOrigin = new Point[] {
                new Point(pontosInteresse.get(2).x, pontosInteresse.get(2).y),  // canto superior esquerdo
                new Point(pontosInteresse.get(3).x, pontosInteresse.get(3).y),  // canto superior direito
                new Point(pontosInteresse.get(1).x, pontosInteresse.get(1).y),  // canto inferior esquerdo
                new Point(pontosInteresse.get(0).x, pontosInteresse.get(0).y)   // canto inferior direito
        };

        double width = distance(pointsOrigin[0], pointsOrigin[1]); // Distância entre o ponto superior esquerdo e superior direito
        double height = distance(pointsOrigin[0], pointsOrigin[2]); // Distância entre o ponto superior esquerdo e inferior esquerdo

        Point[] pointsDestin = new Point[] {
                new Point(0, 0),       // canto superior esquerdo na nova imagem
                new Point(width, 0),      // canto superior direito
                new Point(0, height),     // canto inferior esquerdo
                new Point(width, height)     // canto inferior direito
        };

        MatOfPoint2f matOrigin = new MatOfPoint2f(pointsOrigin);
        MatOfPoint2f matDestin = new MatOfPoint2f(pointsDestin);

        Mat transfPerspective = Imgproc.getPerspectiveTransform(matOrigin, matDestin);

        Mat outPutImgMat = new Mat();

        Imgproc.warpPerspective(imgMat, outPutImgMat, transfPerspective, new Size(width, height));


        return outPutImgMat;
    }
    private double distance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    private String processeQrCode(String qrCode){
        String qrCodeConteudo = qrCode.replaceAll("[#$]", "");
        String[] partes = qrCodeConteudo.split("\\."); // partes do valor do QRCODE
        String id_prova = partes[0];
        String id_aluno = partes[1];
        return id_prova+"_"+id_aluno;
    }

    public String saveBitmapAndGetPath(Bitmap bitmap, String name) {
        File externalDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CameraXopenCV");

        // Cria o diretório se não existir
        if (!externalDir.exists()) {
            externalDir.mkdirs();
        }

        File imageFile = new File(externalDir, name+".png");
        try (FileOutputStream outputStream = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e("ERRO", "ERRO AQUI!!000: "+e.toString());
            return null;
        }

    }

    private void startAnimationCorrect(){
        Intent intent = new Intent(getApplicationContext(), AnimationCorrectionActivity.class);
        startActivity(intent);
    }

    private File getOutputJson(File dir){
        File fileJson = new File(dir, "json.json");
        if (!fileJson.exists()) {
            try {
                // Tenta criar o arquivo
                if (fileJson.createNewFile()) {
                    Log.e("kariti","Diretorio criado");
                } else {
                    Log.i("kariti", "Arquivo já existe.");
                }
            } catch (IOException e) {
                Log.e("kariti", "Erro ao criar diretorio!");
            }
        }
        return fileJson;
    }

    public File creatDirectoreZip() {
        try {
            File fileZip = new File(getCacheDir(), "saida.zip");
            if (!fileZip.exists()){
                try {
                    // Tenta criar o arquivo
                    if (fileZip.createNewFile()) {
                        Log.e("kariti","Diretorio criado");
                    } else {
                        Log.i("kariti", "Arquivo já existe.");
                    }
                } catch (IOException e) {
                    Log.e("kariti", "Erro ao criar diretorio!");
                }
            }
            return fileZip;
        }catch (Exception e){
            Log.e("circles", e.toString());
            return null;
        }
    }

    private void getImage(Uri uri){
        InputStream inputStream = null;
        try {
            inputStream = getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            Log.e("ERRO", "ERRO: "+e.toString());
            return;
        }
        if (inputStream != null) {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap != null) {
                processImage(bitmap);
            }
        }
    }
    private void extractPdf(Uri uri){
        try {
            ContentResolver contentResolver = getContentResolver();
            ParcelFileDescriptor fileDescriptor = contentResolver.openFileDescriptor(uri, "r");
            if (fileDescriptor == null) {
                throw new IOException("Unable to open file descriptor.");
            }
            PdfRenderer renderer = new PdfRenderer(fileDescriptor);
            for (int i = 0; i < renderer.getPageCount(); i++) {
                PdfRenderer.Page page = renderer.openPage(i);

                // Criar um Bitmap do tamanho da página
                int width = page.getWidth();
                int height = page.getHeight();
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                // Renderizar a página no Bitmap
                try {
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                }catch (Exception e){
                    page.close();
                    Log.e("ERRO", "ERRO AQUI!!99999: "+e.toString());
                    continue;
                }
                processImage(bitmap);
                // Fechar a página
                page.close();
            }
            renderer.close();
        } catch (IOException e) {
            Log.e("ERRO", "ERRO AQUI77!!: "+e.toString());
        }
    }
    private void extractZip(Uri uri){
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);

            if (inputStream != null) {
                ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
                ZipEntry zipEntry;

                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    String fileName = zipEntry.getName();
                    //Garante que seja processado apenas um arquivo (Imagem), diretorios são ignorados(pasta dentro do zip)
                    if (!zipEntry.isDirectory() && isImageFile(fileName)) {

                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int count;

                        while ((count = zipInputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, count);
                        }

                        // Converter os bytes em um Bitmap
                        byte[] imageData = outputStream.toByteArray();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                        if (bitmap != null) {
                            processImage(bitmap);
                        }
                        outputStream.close();
                    } else {
                        Log.e("ERRO", "Não é um arquivo");
                    }

                    zipInputStream.closeEntry();
                }

                zipInputStream.close();
            } else {
                Log.e("ERRO", "Erro ao tentar abrir arquivo zip");
            }
        } catch (IOException e) {
            Log.e("ERRO", "ERRO AQUI88!!: "+e.toString());
        }
    }

    private void analysisFileType(Uri uri){
        String mimeType = getContentResolver().getType(uri);
        //Caso o arquivo selecionado seja uma IMAGEM
        if (mimeType != null && mimeType.startsWith("image/")){
            getImage(uri);
        }
        //Caso o arquivo selecionado seja um PDF
        if(mimeType != null && mimeType.startsWith("application/pdf")){
            extractPdf(uri);
        }
        //Caso o arquivo selecionado seja um ZIP
        if(mimeType != null && mimeType.startsWith("application/zip")){
            extractZip(uri);
        }
    }

    private Bitmap matToBitmap(Mat mat) {
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        org.opencv.android.Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }
    private boolean isImageFile(String fileName) {
        String[] imageExtensions = {".jpg", ".jpeg", ".png", ".bmp", ".webp"};
        for (String extension : imageExtensions) {
            if (fileName.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private void notifyCorrectionOrganization(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("ATENÇÃO");
        builder.setMessage("Capture a imagem do cartão de cima, sobre superfície plana e com boa luminosidade\n");
        builder.setPositiveButton("OK", (dialog, which) -> startCamera());
        builder.show();
    }
    private void startCamera(){
        Intent intent = new Intent(this, CameraxAndOpencvActivity.class);
        startActivity(intent);
    }
}