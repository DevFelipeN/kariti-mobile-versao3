package online.padev.kariti;

import static online.padev.kariti.Compactador.listCartoes;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
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
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ProvaActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_OPEN_DOCUMENT = 100;
    ImageButton voltar, iconeAjuda;
    Button btnCadastrarProva, btnGerarCartao, btnCorrigirProva, btnProvasCorrigida, editarProva;
    BancoDados bancoDados;
    TextView textViewTitulo;
    Integer id_provaBD, id_provaCaptured;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova);

        voltar = findViewById(R.id.imgBtnVoltaDescola);
        iconeAjuda = findViewById(R.id.iconHelp);
        btnCadastrarProva = findViewById(R.id.buttonCadProva);
        btnGerarCartao = findViewById(R.id.buttonGerarCatao);
        btnCorrigirProva = findViewById(R.id.buttonCorrigirProva);
        btnProvasCorrigida = findViewById(R.id.buttonVisuProva);
        editarProva = findViewById(R.id.buttonEdicaoProva);
        textViewTitulo = findViewById(R.id.toolbar_title);

        bancoDados = new BancoDados(this);

        textViewTitulo.setText(String.format("%s","Prova"));

        iconeAjuda.setOnClickListener(v -> ajuda());
        btnCadastrarProva.setOnClickListener(v -> carregarCadastroProva());
        btnGerarCartao.setOnClickListener(v -> carregarTelaGerarCartao());
        btnProvasCorrigida.setOnClickListener(v -> carregarTelaProvasCorrigida());
        btnCorrigirProva.setOnClickListener(v -> {
            Boolean verificaProva = bancoDados.verificaExisteProvaCadastrada();
            if (verificaProva == null){
                Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                return;
            }
            if(verificaProva){
                //carregaEtapaCorrecao();//Versao 1
                carregaEtapaCorrecao2();//Versao 2
            }else{
                aviso("provas cadastradas");
            }
        });
        voltar.setOnClickListener(v -> {
            getOnBackPressedDispatcher();
            finish();
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }
    private void carregarCadastroProva(){
        Boolean verificaTurma = bancoDados.verificaExisteTurmas();
        if (verificaTurma == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
            return;
        }
        if(verificaTurma){
            Intent intent = new Intent(this, CadProvaActivity.class);
            startActivity(intent);
        }else aviso("turmas cadastradas");
    }
    private void carregarTelaGerarCartao(){
        Boolean verificaProvaCad = bancoDados.verificaExisteProvaCadastrada();
        if (verificaProvaCad == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
            return;
        }
        if (verificaProvaCad) {
            Intent intent = new Intent(this, ProvaCartoesActivity.class);
            intent.putExtra("endereco", 2);
            startActivity(intent);
        }else{
            aviso("provas cadastradas");
        }
    }
    private void carregarTelaProvasCorrigida(){
        Boolean verificaExisteProvaCorrigida = bancoDados.verificaExisteProvaCorrigida();
        if (verificaExisteProvaCorrigida == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
            return;
        }
        if(verificaExisteProvaCorrigida){
            Intent intent = new Intent(this, VisualProvaActivity2.class);
            startActivity(intent);
        }else{
            aviso("provas corrigidas");
        }
    }
    private void aviso(String descricao){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Atenção!");
        builder.setMessage("Não encontramos "+descricao+" para essa escola. Para ter acesso a essa opção é necessário ter "+descricao+".");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void ajuda() {
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
    private void carregaEtapaCorrecao2() {
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
            Intent intent = new Intent(getApplicationContext(), CameraxAndOpencv.class);
            startActivity(intent);
            dialog.dismiss();
        });

        buttonFileDevice.setOnClickListener(v -> {
            abrirArquivos();
            dialog.dismiss();});

        closedCorrect.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private void abrirArquivos(){
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
                processarArquivos(requestCode, resultCode, data, handler);
            }
        }.start();
        if(data != null){
            iniciaAnimacaoCorrecao();
        }
    }
    private void processarArquivos(int requestCode, int resultCode, @Nullable Intent data, Handler handler){
        try {
            if (requestCode == REQUEST_CODE_OPEN_DOCUMENT && resultCode == RESULT_OK) {
                if (data != null) {
                    //Caso mais de um arquivo seja selecionado
                    if (data.getClipData() != null) { // Múltiplos arquivos
                        int count = data.getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            Uri uri = data.getClipData().getItemAt(i).getUri();
                            analisesFileType(uri);
                        }
                    }
                    //Caso somente um arquivo seja selecionado
                    if (data.getData() != null) {
                        Uri uri = data.getData();
                        analisesFileType(uri);
                    }
                }else{
                    return;
                }
            }else{
                return;
            }
            if (!Compactador.listCartoes.isEmpty()) {
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
                            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }else Toast.makeText(this, "Erro de Compactação", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Log.e("kariti",e.getMessage());
                    Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            mensagem(handler, "Correção finalizada!!");
        }catch (Exception e){
            Log.e("ERRO", "ERRO AQUI44!!: "+e.toString());
        }
    }
    private void mensagem(Handler handler, String msg){
        if (!isFinishing() && !isDestroyed()) {
            Log.e("tempo", "Fim");
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProvaActivity.this);
                    builder.setCancelable(false);
                    builder.setView(dialogView);
                    // Mostrar o diálogo
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    buttonYes.setOnClickListener(v -> {
                        String[] x = bancoDados.pegarDadosProva(id_provaBD);
                        String nameProva = x[0];
                        String id_turma = x[1];
                        String nameTurma = bancoDados.pegarNomeTurma(id_turma);
                        Intent intent = new Intent(getApplicationContext(), VisualProvaCorrigidaActivity.class);
                        intent.putExtra("id_prova", id_provaBD);
                        intent.putExtra("prova", nameProva);
                        intent.putExtra("turma", nameTurma);
                        startActivity(intent);
                        dialog.dismiss();
                    });

                    buttonNot.setOnClickListener(v -> {
                        dialog.dismiss();
                    });
                }
            });
        }
    }

    public void processesImage(Bitmap bitmap){
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
                    Point center = new Point(circExt.x, circExt.y);
                    Imgproc.circle(matToWarp, center, (int) (circExt.radius + circExt.radius * 0.8), new Scalar(255, 255, 255), -1);
                }
            }

            String resultQrCode = "";
            String nameCartao = "";

            if (circ == 4){
                List<Point> listOrganized = organize(circlesInterest);
                int questionsBD = 0;
                int alternativesBD = 0;
                Bitmap imgToQrCode = matToBitmap(mat);
                String textQrCode = scanQRCodeFromBitmap(imgToQrCode);
                if(textQrCode != null){
                    Mat matWarp = warp(matToWarp, listOrganized); //realiza o corte da imagem
                    resultQrCode = processeQrCode(textQrCode);
                    String[] a = resultQrCode.split("_");
                    id_provaCaptured = Integer.parseInt(a[0]);
                    if(!id_provaCaptured.equals(id_provaBD)){
                        if(!bancoDados.verificaExisteProvaPId(id_provaCaptured)){
                            //runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Prova não cadastrada!", Toast.LENGTH_SHORT).show());
                            Log.e("kariti","Prova não cadastrada!!");
                            return;
                        }
                    }
                    id_provaBD = id_provaCaptured;
                    Integer[] questAlt = bancoDados.pegarQuestsAndAlts(id_provaBD);
                    if(questAlt == null){
                        return;
                    }
                    questionsBD = questAlt[0];
                    alternativesBD = questAlt[1];

                    Bitmap imgWarp = matToBitmap(matWarp);
                    nameCartao = resultQrCode+"_"+questionsBD+"_"+alternativesBD;
                    saveBitmapAndGetPath(imgWarp, nameCartao); //Salva a imagem cortada
                    saveBitmapAndGetPath(matToBitmap(mat), "Original_"+resultQrCode+"_"+questionsBD+"_"+alternativesBD); //Salva a imagem original

                    String n = nameCartao+".png";
                    if(!Compactador.listCartoes.contains(n)) {
                        Compactador.listCartoes.add(n);
                    }
                }
            }
        }catch (Exception e){
            Log.e("ERRO", "ERRO AQUI55!!: "+e.toString());
        }

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
        String qrCodeConteudo = qrCode.replaceAll("#", "");
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

    public void iniciaAnimacaoCorrecao(){
        Intent intent = new Intent(getApplicationContext(), AnimacaoCorrecao.class);
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
                processesImage(bitmap);
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
                processesImage(bitmap);
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
                            processesImage(bitmap);
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

    private void analisesFileType(Uri uri){
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

}