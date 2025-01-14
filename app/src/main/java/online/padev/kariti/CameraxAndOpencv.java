package online.padev.kariti;

import static androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY;
import static online.padev.kariti.Compactador.listCartoes;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import online.padev.kariti.Correction.Util;

public class CameraxAndOpencv extends AppCompatActivity {

    private PreviewView camera;
    private ExecutorService cameraExecutor;
    private ProcessCameraProvider cameraProvider;
    private ImageView edgeImageView;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    BancoDados bancoDados;
    ImageAnalysis imageAnalysis;
    Integer id_provaBD, id_provaCaptured, id_alunoBD;
    private boolean isActivityFinishing = false;
    private boolean isQrCodePositive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerax_and_opencv);

        Button encerrar = findViewById(R.id.buttonEncerrar);
        camera = findViewById(R.id.previewCameraX);
        cameraExecutor = Executors.newSingleThreadExecutor();
        edgeImageView = findViewById(R.id.edgeImageView);

        if(listCartoes.isEmpty()){
            encerrar.setVisibility(View.INVISIBLE);
        }

        bancoDados = new BancoDados(this);

        requestCameraPermission();

        startCamera();
        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "Falha ao carregar o OpenCV!");
        }else{
            Log.e("OpenCV", "OpenCV carregado com sucesso!");
        }
        encerrar.setOnClickListener(v -> {
            encerrar.setEnabled(false);
            if(listCartoes.isEmpty()){
                finish();
            }
            Handler handler = new Handler(Looper.getMainLooper());
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        File fileZip = creatDirectoreZip();
                        File directoreImgs = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CameraXopenCV");
                        if(Compactador.compactador(directoreImgs, fileZip.getAbsolutePath())){
                            listCartoes.clear();
                            try {
                                File dir = getCacheDir();
                                File fileJson = getOutputJson(dir);
                                UploadEjson.enviarArquivosP(fileZip, new FileOutputStream(fileJson), dir, bancoDados);
                            } catch (Exception e){
                                Log.e("Kariti", "(Erro ao tentar enviar arquivo zip para correção ou baixar Json) "+e.getMessage());
                                finish();
                            }
                        }else{
                            Log.e("kariti", "Erro de compactação!!!");
                        }
                        mensagem(handler, "Correção finalizada!");
                    }catch (Exception e){
                        Log.e("kariti",e.getMessage());
                        finish();
                    }
                }
            }.start();
            iniciaAnimacaoCorrecao();
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(Compactador.listCartoes.isEmpty()){
                    Compactador.id_provaOpenCV = 0;
                    finish();
                }else{
                    avidoDeCancelamento();
                }
            }
        });

    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder()
                        //.setTargetResolution(new android.util.Size(1920, 1080))
                        //.setTargetAspectRatio(AspectRatio.RATIO_16_9)

                        .build();
                ImageCapture imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .build();

                preview.setSurfaceProvider(camera.getSurfaceProvider());

                imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
                imageAnalysis.setAnalyzer(cameraExecutor, this::processImage);

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
                //cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
            } catch (Exception e) {
                Log.e("CameraX", "Erro ao abrir a câmera: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void processImage(@NonNull ImageProxy imageProxy) {
        try {
            processamento(imageProxy); //threshold OTSU
        }catch(Exception e){
            Log.e("kariti", e.toString());
        }
    }

    public void processamento(ImageProxy imageProxy){
        try {
            Mat mat = imageProxyToMat(imageProxy);
            if (mat == null) {
                imageProxy.close(); // Libera o frame se não for válido
                return;
            }
            // Corrige a orientação da imagem
            int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
            if (rotationDegrees != 0) {
                Mat rotatedMat = new Mat();
                Core.rotate(mat, rotatedMat, rotationDegrees == 90 ? Core.ROTATE_90_CLOCKWISE :
                        rotationDegrees == 180 ? Core.ROTATE_180 :
                                rotationDegrees == 270 ? Core.ROTATE_90_COUNTERCLOCKWISE :
                                        Core.ROTATE_90_CLOCKWISE);
                mat.release();
                mat = rotatedMat;
            }

            Mat matToWarp = mat.clone();//Imagem para pintar os circulos encontrados de branco e aplicar o corte
            Mat matAux = mat.clone();//Imagem para ser desenhado os contornos

            //Log.e("matToWarp", "Channels: " + matToWarp.channels() + ", Type: " + matToWarp.type());

            //Aumenta o brilho e contranste da imagem
            Mat matEnhanced = enhanceImage(matAux);
            if(matEnhanced == null){
                imageProxy.close(); // Libera o frame se não for válido
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
                        Imgproc.drawContours(matAux, Collections.singletonList(contour), -1, new Scalar(0, 255, 0), 1);
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
                        Imgproc.drawContours(matAux, Collections.singletonList(circInt.contour), -1, new Scalar(0, 0, 255), 1);
                        used[j] = true;
                        contador++;
                    }
                }
                if (contador >= 1) {
                    circlesInterest.add(new Point(circExt.x, circExt.y));
                    circ++;
                    Imgproc.drawContours(matAux, Collections.singletonList(circExt.contour), -1, new Scalar(255, 0, 0), -1);
                    Point center = new Point(circExt.x, circExt.y);
                    Imgproc.circle(matToWarp, center, (int) (circExt.radius + circExt.radius * 0.8), new Scalar(255, 255, 255), -1);
                }
            }

            List<Point> listOrganized = new ArrayList<>();
            Mat matWarp = new Mat(); //Objeto para Imagem cortada
            String filePath = "";
            String resultQrCode = "";
            String nameCartao = "";

            //Desenha um retangulo com base nos pontos de interesse encontrados
            if(circ == 4){
                listOrganized = organize(circlesInterest);
                Point previous = listOrganized.get(0);
                for(Point p : listOrganized){
                    Point start = new Point(previous.x, previous.y);
                    Point end = new Point(p.x, p.y);
                    Imgproc.line(matAux, start, end, new Scalar(0, 0, 255), 1);
                    previous = p;
                }

                Point start = new Point(previous.x, previous.y);
                Point end = new Point(listOrganized.get(0).x, listOrganized.get(0).y);
                Imgproc.line(matAux, start, end, new Scalar(0, 0, 255), 1);
            }
            //Converte imagem para ser mostrada na tela
            Bitmap imgBitmap = matToBitmap(matAux);

            if (circ == 4){
                int squares = 0;
                int totAltQuestBD = 0;
                int questionsBD = 0;
                int alternativesBD = 0;
                Bitmap imgToQrCode = matToBitmap(mat);
                String textQrCode = scanQRCodeFromBitmap(imgToQrCode);
                if(textQrCode != null){
                    matWarp = warp(matToWarp, listOrganized); //realiza o corte da imagem
                    resultQrCode = processeQrCode(textQrCode);
                    String[] a = resultQrCode.split("_");
                    id_provaCaptured = Integer.parseInt(a[0]);
                    if(!id_provaCaptured.equals(id_provaBD)){
                        if(!bancoDados.verificaExisteProvaPId(id_provaCaptured)){
                            //runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Prova não cadastrada!", Toast.LENGTH_SHORT).show());
                            Log.e("kariti","Prova não cadastrada!!");
                            imageProxy.close();
                            mat.release();
                            return;
                        }
                    }
                    id_provaBD = id_provaCaptured;
                    Integer[] questAlt = bancoDados.pegarQuestsAndAlts(id_provaBD);
                    if(questAlt == null){
                        imageProxy.close();
                        mat.release();
                        return;
                    }
                    questionsBD = questAlt[0];
                    alternativesBD = questAlt[1];
                    totAltQuestBD = questionsBD + alternativesBD;
                    squares = squares(matWarp);
                }
                if(totAltQuestBD > 0 && squares == totAltQuestBD){
                    Bitmap imgWarp = matToBitmap(matWarp);
                    isQrCodePositive = true;
                    nameCartao = resultQrCode+"_"+questionsBD+"_"+alternativesBD;
                    filePath = saveBitmapAndGetPath(imgWarp, nameCartao); //Salva a imagem cortada
                    //saveBitmapAndGetPath(matToBitmap(mat), "Original_"+resultQrCode+"_"+questionsBD+"_"+alternativesBD); //Salva a imagem original
                }
            }
            if(!isActivityFinishing && isQrCodePositive){
                Compactador.id_provaOpenCV = id_provaBD;
                isActivityFinishing = true;
                cameraExecutor.shutdown();
                Intent intent = new Intent(this, ViewImage.class);
                intent.putExtra("filePath", filePath);
                intent.putExtra("nameImag", nameCartao);
                startActivity(intent);
                finish();
            }
            runOnUiThread(() -> edgeImageView.setImageBitmap(imgBitmap));

            mat.release();
            imageProxy.close();

        }catch (Exception e){
            e.printStackTrace();
            Log.e("ERRO", e.toString());
        }

    }

    class Circle {
        double x, y, radius, xR, yR, wR, hR, perimeter;
        MatOfPoint contour;

        Circle(double x, double y, double radius, double xR, double yR, double wR, double hR, MatOfPoint contour, double perimeter) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.xR = xR;
            this.yR = yR;
            this.wR = wR;
            this.hR = hR;
            this.contour = contour;
            this.perimeter = perimeter;
        }
    }

    class Squares {
        double x, y;
        MatOfPoint cnt;

        Squares(double x, double y, MatOfPoint cnt){
            this.x = x;
            this.y = y;
            this.cnt = cnt;
        }

    }


    public static boolean isInside(Circle circExt, Circle circInt) {
        double xInt = circInt.x, yInt = circInt.y;
        double xExtI = circExt.xR, yExtI = circExt.yR;
        double xExtF = xExtI + circExt.wR, yExtF = yExtI + circExt.hR;

        return !(xInt < xExtI || xInt > xExtF || yInt < yExtI || yInt > yExtF);
    }

    private Mat imageProxyToMat(@NonNull ImageProxy image) {
        try {
            // Verifica o formato da imagem
            if (image.getFormat() != ImageFormat.YUV_420_888) {
                Log.e("ProxToMat", "Formato de imagem não suportado: " + image.getFormat());
                return null;
            }

            // Obtém os planos Y, U e V
            ImageProxy.PlaneProxy yPlane = image.getPlanes()[0];
            ImageProxy.PlaneProxy uPlane = image.getPlanes()[1];
            ImageProxy.PlaneProxy vPlane = image.getPlanes()[2];

            // Obtenha dimensões e stride
            int width = image.getWidth();
            int height = image.getHeight();

            int yRowStride = yPlane.getRowStride();
            int uvRowStride = uPlane.getRowStride();
            int uvPixelStride = uPlane.getPixelStride();

            // Buffer NV21 (YUV format)
            byte[] nv21 = new byte[width * height + (width * height) / 2];

            // Preencha o plano Y
            ByteBuffer yBuffer = yPlane.getBuffer();
            for (int row = 0; row < height; row++) {
                yBuffer.position(row * yRowStride);
                yBuffer.get(nv21, row * width, width);
            }

            // Preencha os planos UV (intercalados)
            ByteBuffer uBuffer = uPlane.getBuffer();
            ByteBuffer vBuffer = vPlane.getBuffer();
            int uvHeight = height / 2; // UV é metade da altura de Y
            for (int row = 0; row < uvHeight; row++) {
                for (int col = 0; col < width / 2; col++) {
                    int uvIndex = width * height + row * width + col * 2;
                    nv21[uvIndex] = vBuffer.get(row * uvRowStride + col * uvPixelStride); // V
                    nv21[uvIndex + 1] = uBuffer.get(row * uvRowStride + col * uvPixelStride); // U
                }
            }

            // Cria um Mat YUV e converte para RGB
            Mat yuvMat = new Mat(height + height / 2, width, CvType.CV_8UC1);
            yuvMat.put(0, 0, nv21);

            Mat rgbMat = new Mat();
            Imgproc.cvtColor(yuvMat, rgbMat, Imgproc.COLOR_YUV2RGB_NV21);
            yuvMat.release();

            return rgbMat;

        } catch (Exception e) {
            Log.e("ProxToMat", "Erro ao converter ImageProxy para Mat: " + e.getMessage());
            return null;
        }
    }
    private String processeQrCode(String qrCode){
        String qrCodeConteudo = qrCode.replaceAll("#", "");
        String[] partes = qrCodeConteudo.split("\\."); // partes do valor do QRCODE
        String id_prova = partes[0];
        String id_aluno = partes[1];
        return id_prova+"_"+id_aluno;
    }

    private Bitmap matToBitmap(Mat mat) {
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        org.opencv.android.Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        shutdownExecutorService();
    }
    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA}, 101);
        } else {
            startCamera();
        }
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
            e.printStackTrace();
            return null;
        }

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

    public void iniciaAnimacaoCorrecao(){
        Intent intent = new Intent(getApplicationContext(), AnimacaoCorrecao.class);
        startActivity(intent);
    }

    public File creatDirectoreZip() {
        try {
            File fileZip = new File(getCacheDir(), "saida.zip");
            if (!fileZip.exists()) {
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
    private void shutdownExecutorService() {
        // Chama shutdown para não aceitar novas tarefas
        executor.shutdown();
        try {
            // Aguarda a finalização das tarefas em execução
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)){
                executor.shutdownNow(); // Força o encerramento se não terminar no tempo especificado
            }
        } catch (InterruptedException e) {
            executor.shutdownNow(); // Força o encerramento se a espera for interrompida
            Thread.currentThread().interrupt(); // Restaura o estado de interrupção
        }
    }
    private double distance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
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

    public void avidoDeCancelamento(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENÇÃO!")
                .setMessage("Caso confirme essa ação, o processo de correção em andamento, será cancelado!\n\n" +
                        "Deseja realmente voltar")
                .setPositiveButton("SIM", (dialog, which) -> {
                    Compactador.listCartoes.clear();
                    Compactador.id_provaOpenCV = 0;
                    finish();
                })
                .setNegativeButton("NÃO", (dialog, which) -> {
                    // Código para lidar com o clique no botão Cancelar, se necessário
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private static Mat enhanceImage(Mat matImage) {
        if (matImage.empty()) {
            System.out.println("Erro ao carregar a imagem.");
            return null;
        }
        // Aumentar o brilho
        Mat brighterImage = new Mat();
        Core.add(matImage, new Scalar(50, 50, 50), brighterImage); // Aumenta o brilho

        // Aumentar o contraste
        Mat enhancedImage = new Mat();
        brighterImage.convertTo(enhancedImage, -1, 1.2, 0); // 1.2 é o fator de contraste

        // Converter de RGB para BGR (se necessário)
        if (enhancedImage.channels() == 3) {
            Imgproc.cvtColor(enhancedImage, enhancedImage, Imgproc.COLOR_RGB2BGR);
        }

        return enhancedImage;
    }

    private Integer squares(Mat imgWarp){
        Mat imgAux = imgWarp.clone();
        List<Point> squaresQuestions = new ArrayList<>();
        List<Point> squaresQuestionsValid;
        List<Point> squaresAltenatives = new ArrayList<>();
        List<Point> squaresAltenativesValid;
        int height = imgAux.rows();
        int width = imgAux.cols();

        Mat matEnhanced = enhanceImage(imgAux);
        if(matEnhanced == null){
            return 0;
        }

        Mat grayWarp = new Mat();
        Imgproc.cvtColor(matEnhanced, grayWarp, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(grayWarp, grayWarp, new Size(5, 5), 0);

        // Binarização
        Mat binaryImage = new Mat();
        //Imgproc.threshold(grayWarp, binaryImage, 128, 255, Imgproc.THRESH_BINARY_INV);
        Imgproc.threshold(grayWarp, binaryImage, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

        boolean qualityImage = analysisImage(binaryImage);

        if (!qualityImage){ //Se a imagem apresentar cor preta maior que 3%, desconsiderar imagem
            return 0;
        }

        //Bitmap img = matToBitmap(binaryImage);
        //saveBitmapAndGetPath(img, "binaryTeste");

        // Encontrar contornos
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(binaryImage, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        for (MatOfPoint cnt : contours) {

            Moments moments = Imgproc.moments(cnt);

            double x = moments.m10 / moments.m00;
            double y = moments.m01 / moments.m00;

            if ( x < width * 0.09){
                squaresQuestions.add(new Point(x,y));
                //Imgproc.drawContours(imgAux, List.of(cnt), -1, new Scalar(0, 255, 0), 2);
            }
            if (y < height * 0.06){
                squaresAltenatives.add(new Point(x,y));
                //Imgproc.drawContours(imgAux, List.of(cnt), -1, new Scalar(0, 255, 0), 2);
            }
        }
        squaresQuestionsValid = analysisQuestions(squaresQuestions, height, width);
        squaresAltenativesValid = analysisAlternatives(squaresAltenatives, height, width);

        return squaresQuestionsValid.size() + squaresAltenativesValid.size();
    }

    private List<Point> analysisQuestions(List<Point> listQuestions, int height, int width){
        // Ordenar círculos por tamanho do raio em ordem decrescente
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            listQuestions.sort((a, b) -> Double.compare(b.y, a.y));
        } else {
            Collections.sort(listQuestions, new Comparator<Point>() {
                @Override
                public int compare(Point o1, Point o2) {
                    return (int) (o2.y - o1.y);
                }
            });
        }
        for (int p = listQuestions.size() - 1; p > 0; p--){
            Point point1 = listQuestions.get(p-1);
            Point point2 = listQuestions.get(p);
            boolean analysis = analysisDistanceY(point1, point2, height, width);
            if (analysis){
                listQuestions.remove(p);
            }
        }

        return listQuestions;
    }
    private List<Squares> analysisQuestions2(List<Squares> listQuestions, int height, int width){
        // Ordenar círculos por tamanho do raio em ordem decrescente
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            listQuestions.sort((a, b) -> Double.compare(b.y, a.y));
        } else {
            Collections.sort(listQuestions, new Comparator<Squares>() {
                @Override
                public int compare(Squares o1, Squares o2) {
                    return (int) (o2.y - o1.y);
                }
            });
        }

        for (int p = listQuestions.size() - 1; p > 0; p--){
            Squares square1 = listQuestions.get(p-1);
            Squares square2 = listQuestions.get(p);
            Point point1 = new Point(square1.x, square1.y);
            Point point2 = new Point(square2.x, square2.y);
            boolean analysis = analysisDistanceY(point1, point2, height, width);
            if (analysis){
                listQuestions.remove(p);
            }
        }

        return listQuestions;
    }

    private boolean analysisDistanceY(Point p1, Point p2, int height, int width){
        return (p1.y - p2.y > height * 0.048 || p1.y - p2.y < height * 0.027 || Math.abs(p1.x - p2.x) > width * 0.01);
    }

    private List<Point> analysisAlternatives(List<Point> listAlternatives,int height, int width){
        // Ordenar círculos por tamanho do raio em ordem decrescente
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            listAlternatives.sort((a, b) -> Double.compare(b.x, a.x));
        } else {
            Collections.sort(listAlternatives, new Comparator<Point>() {
                @Override
                public int compare(Point o1, Point o2) {
                    return (int) (o2.x - o1.x);
                }
            });
        }
        for (int p = listAlternatives.size() - 1; p > 0; p--){
            Point point1 = listAlternatives.get(p-1);
            Point point2 = listAlternatives.get(p);
            boolean analysis = analysisDistanceX(point1, point2, height, width);
            if (analysis){
                listAlternatives.remove(p);
            }
        }
        return listAlternatives;
    }

    private List<Squares> analysisAlternatives2(List<Squares> listAlternatives,int height, int width){
        // Ordenar círculos por tamanho do raio em ordem decrescente
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            listAlternatives.sort((a, b) -> Double.compare(b.x, a.x));
        } else {
            Collections.sort(listAlternatives, new Comparator<Squares>() {
                @Override
                public int compare(Squares o1, Squares o2) {
                    return (int) (o2.x - o1.x);
                }
            });
        }
        for (int p = listAlternatives.size() - 1; p > 0; p--){
            Squares square1 = listAlternatives.get(p-1);
            Squares square2 = listAlternatives.get(p);
            Point point1 = new Point(square1.x, square1.y);
            Point point2 = new Point(square2.x, square2.y);
            boolean analysis = analysisDistanceX(point1, point2, height, width);
            if (analysis){
                listAlternatives.remove(p);
            }
        }
        return listAlternatives;
    }

    private boolean analysisDistanceX(Point p1, Point p2, int height, int width){
        return (p1.x - p2.x > width * 0.116 || p1.x - p2.x < width * 0.095 || Math.abs(p1.y - p2.y) > height * 0.01);
    }

    /**
     * Dada uma imagem cortada e binarizada, este metodo calcula a porcentagem de cor preta da imagem
     * @param matWarpOtsu imagem cortada e binarizada para extração das cores
     * @return true se porcentagem estiver dentro esperado, caso contrario, false.
     */
    private boolean analysisImage(Mat matWarpOtsu){
        int height = matWarpOtsu.rows();
        int width = matWarpOtsu.cols();
        int totPixels = height * width;
        int whitePixels = Core.countNonZero(matWarpOtsu);
        int blackPixels = totPixels - whitePixels;

        Log.e("porcentagem", "tot: "+totPixels+" black: "+blackPixels);
        Log.e("porcentagem", "%: "+ (blackPixels / (double) totPixels) * 100);

        return ((blackPixels / (double) totPixels) * 100 <= 3.0);
    }

    private List<Point> delimiter(List<Point> list){
        //MatOfPoint2f cnt2f = new MatOfPoint2f(cnt.toArray());
        //MatOfPoint2f approx = new MatOfPoint2f();
        //double epsilon = 0.05 * Imgproc.arcLength(cnt2f, true);  // Epsilon é a precisão (tamanho da aproximação)
        //Imgproc.approxPolyDP(cnt2f, approx, epsilon, true);
        return list;
    }

    private void mensagem(Handler handler, String msg){
        if (!isFinishing() && !isDestroyed()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(ProvaActivity.this, msg, Toast.LENGTH_SHORT).show();
                    // Inflar o layout customizado
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.open_correction_details, null);

                    // Inicializar os elementos do layout
                    TextView inform = dialogView.findViewById(R.id.tituloInform);
                    Button buttonYes = dialogView.findViewById(R.id.buttonYesOpen);
                    Button buttonNot = dialogView.findViewById(R.id.buttonNotOpen);

                    inform.setText("Prova(s) corrigida(s).\n  Deseja visualizar correção?");

                    // Criar o AlertDialog
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(CameraxAndOpencv.this);
                    builder.setCancelable(false);
                    builder.setView(dialogView);
                    // Mostrar o diálogo
                    androidx.appcompat.app.AlertDialog dialog = builder.create();
                    dialog.show();

                    buttonYes.setOnClickListener(v -> {
                        String[] x = bancoDados.pegarDadosProva(Compactador.id_provaOpenCV);
                        String nameProva = x[0];
                        String id_turma = x[1];
                        String nameTurma = bancoDados.pegarNomeTurma(id_turma);
                        Intent intent = new Intent(getApplicationContext(), VisualProvaCorrigidaActivity.class);
                        intent.putExtra("id_prova", Compactador.id_provaOpenCV);
                        intent.putExtra("prova", nameProva);
                        intent.putExtra("turma", nameTurma);
                        startActivity(intent);
                        Compactador.id_provaOpenCV = 0;
                        dialog.dismiss();
                        finish();
                    });

                    buttonNot.setOnClickListener(v -> {
                        dialog.dismiss();
                        finish();
                    });
                }
            });
        }
    }
}