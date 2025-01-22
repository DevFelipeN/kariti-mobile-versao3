package online.padev.kariti.correction;

import android.os.Build;
import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import online.padev.kariti.BancoDados;


public class Util {
    List<Point> squaresQuestions = new ArrayList<>();
    List<MatOfPoint> contours = new ArrayList<>();
    List<Point> squaresAltenatives = new ArrayList<>();
    Map<Integer, Integer> gabarito = new HashMap<>();
    List<SquaresCircles> squaresCircles = new ArrayList<>();
    List<Circle> markedCircles = new ArrayList<>();
    Integer id_provaBD, id_alunoBD;
    Mat mat;
    BancoDados bancoDados;
    int num_questionsBD, num_alternativesBD, height, width;
    private final double limit = 0.02;

    public Util(Mat mat, int num_questionsBD, int num_alternativesBD, BancoDados bancoDados, Integer id_provaBD, Integer id_alunoBD){
        this.mat = mat;
        this.num_questionsBD = num_questionsBD;
        this.num_alternativesBD = num_alternativesBD;
        this.id_provaBD = id_provaBD;
        this.id_alunoBD = id_alunoBD;
        this.bancoDados = bancoDados;
    }

    /**
     * Método para validação de imagens para correção baseado nos contornos referentes aos quadrados das questões e alternativas
     * @return retorna true caso a imagem seja valida para correção e false caso contrário
     */
    public boolean correctCard() {
        boolean isValid = squares();
        //Log.e("correcao", "Passei aqi: "+isValid);
        if (isValid){ //Válida para buscar as respostas (marcações dos alunos)
            boolean resp = getAnswers();
            return resp;
        }else {
            return false;
        }
    }
    private boolean getAnswers(){
        circlesOfInterest(); // Seleciona apenas os contornos dentro do limite definido
        sortCirclesOrder(); // Ordena a lista de circulos em ordem crescente em y
        compareSquaresAndCircles();
        boolean insertCorrectBD = bancoDados.cadastrarCorrecao(gabarito, id_provaBD, id_alunoBD);
        if(insertCorrectBD){
            Log.e("correcao", "Prova corrigida com sucesso!!");
        }
        paintCantos();
        enumerateQuestions();
        return true;
    }
    private boolean squares(){
        try {
            //Mat imgAux = mat.clone();
            Mat imgAux = mat;

            height = imgAux.rows();
            width = imgAux.cols();

            Mat matEnhanced = enhanceImage(imgAux);
            if(matEnhanced == null){
                return false;
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
                return false;
            }

            //Bitmap img = matToBitmap(binaryImage);
            //saveBitmapAndGetPath(img, "binaryTeste");

            // Encontrar contornos
            Mat hierarchy = new Mat();
            Imgproc.findContours(binaryImage, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

            for (MatOfPoint cnt : contours) {

                Moments moments = Imgproc.moments(cnt);

                double x = moments.m10 / moments.m00;
                double y = moments.m01 / moments.m00;

                if ( x < width * 0.09){
                    squaresQuestions.add(new Point(x,y));
                    //Imgproc.drawContours(imgAux, List.of(cnt), -1, new Scalar(255, 0, 0), 2);
                }
                if (y < height * 0.06){
                    squaresAltenatives.add(new Point(x,y));
                    //Imgproc.drawContours(imgAux, List.of(cnt), -1, new Scalar(0, 0, 255), 2);
                }
            }

            int quest = analysisQuestions(height, width);
            int alt = analysisAlternatives(height, width);

            //Imgproc.threshold(grayWarp, mat, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
            //Imgproc.cvtColor(mat, mat, Imgproc.COLOR_GRAY2BGR);

            return quest == num_questionsBD && alt == num_alternativesBD;

        }catch (Exception e){
            Log.e("correcao", "E1: "+e.toString());
            return false;
        }
    }
    private Mat enhanceImage(Mat matImage) {
        try {
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
        }catch (Exception e){
            Log.e("correcao", "E2: "+e.toString());
            return null;
        }
    }
    private int analysisQuestions(int height, int width){
        try {
            // Ordenar quadrados pela distancia em y e em ordem decrescente
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                squaresQuestions.sort((a, b) -> Double.compare(a.y, b.y));
            } else {
                Collections.sort(squaresQuestions, new Comparator<Point>() {
                    @Override
                    public int compare(Point o1, Point o2) {
                        return (int) (o1.y - o2.y);
                    }
                });
            }

            eliminateContourQuests();

            for (int p = squaresQuestions.size() - 1; p > 0; p--) {
                Point point1 = squaresQuestions.get(p);
                Point point2 = squaresQuestions.get(p - 1);
                boolean analysis = analysisDistanceY(point1, point2, height, width);
                if (analysis) {
                    squaresQuestions.remove(p);
                }
            }

            return squaresQuestions.size();
        }catch (Exception e){
            Log.e("correcao", "E3: "+e.toString());
            return 0;
        }
    }
    private int analysisAlternatives(int height, int width){
        try {
            // Ordenar círculos por tamanho do raio em ordem decrescente
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                squaresAltenatives.sort((a, b) -> Double.compare(a.x, b.x));
            } else {
                Collections.sort(squaresAltenatives, new Comparator<Point>() {
                    @Override
                    public int compare(Point o1, Point o2) {
                        return (int) (o1.x - o2.x);
                    }
                });
            }

            eliminateAlternatives();

            for (int p = squaresAltenatives.size() - 1; p > 0; p--) {
                Point point1 = squaresAltenatives.get(p);
                Point point2 = squaresAltenatives.get(p - 1);
                boolean analysis = analysisDistanceX(point1, point2, height, width);
                if (analysis) {
                    squaresAltenatives.remove(p);
                }
            }
            return squaresAltenatives.size();
        }catch (Exception e){
            Log.e("correcao", "E4: "+e.toString());
            return 0;
        }
    }
    private boolean analysisDistanceY(Point p1, Point p2, int height, int width){
        return (p1.y - p2.y > height * 0.048 || p1.y - p2.y < height * 0.027 || Math.abs(p1.x - p2.x) > width * 0.01);
    }
    private boolean analysisDistanceX(Point p1, Point p2, int height, int width){
        return (p1.x - p2.x > width * 0.116 || p1.x - p2.x < width * 0.095 || Math.abs(p1.y - p2.y) > height * 0.01);
    }
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
    private void circlesOfInterest(){
        try {
            Mat matToCircles = mat;
            Point p1 = squaresQuestions.get(squaresQuestions.size() - 1); // Point da ultima questão
            Point p2 = squaresAltenatives.get(0); // Point da primeira alternativa
            Point p3 = squaresAltenatives.get(squaresAltenatives.size() - 1); // Point da ultima alternativa

            double yP1 = p1.y + height * limit;
            double xP2 = p2.x - width * limit;
            double yP2 = p2.y + height * limit;
            double xP3 = p3.x + width * limit;

            for (MatOfPoint contour : contours) {
                Point center = new Point();
                float[] radius = new float[1];
                Imgproc.minEnclosingCircle(new MatOfPoint2f(contour.toArray()), center, radius);

                double areaContour = Imgproc.contourArea(contour);
                double areaCircle = Math.PI * Math.pow(radius[0], 2);

                if (areaCircle > 0) {
                    //double circularity = areaContour / areaCircle;
                    if (radius[0] < width * limit && center.y < yP1 && center.y > yP2 && center.x > xP2 && center.x < xP3) {
                        Log.e("test", "radius" + radius[0]);
                        Rect boundingRect = Imgproc.boundingRect(contour);
                        Circle circle = new Circle(center.x, center.y, radius[0], boundingRect.x, boundingRect.y, boundingRect.width, boundingRect.height, contour, Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true));
                        markedCircles.add(circle);
                        //Imgproc.drawContours(matToCircles, List.of(contour), -1, new Scalar(255, 0, 0), 2);
                    }
                }
            }
            Log.e("test", "Tot: " + markedCircles.size());
        }catch (Exception e){
            Log.e("correcao", "E5: "+e.toString());
        }
    }

    private void release() {
        if (mat != null) {
            mat.release();
        }
        for (MatOfPoint contour : contours) {
            contour.release();
        }
        contours.clear();
        //squaresQuestions.clear();
        //squaresAltenatives.clear();
        //markedCircles.clear();
    }

    private void sortCirclesOrder(){
        try {
            // Ordenar círculos por tamanho do raio em ordem decrescente
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                markedCircles.sort((a, b) -> Double.compare(a.y, b.y));
            } else {
                Collections.sort(markedCircles, new Comparator<Circle>() {
                    @Override
                    public int compare(Circle o1, Circle o2) {
                        //return (int) (o2.radius - o1.radius);
                        return Double.compare(o1.y, o2.y);
                    }
                });
            }
        }catch (Exception e){
            Log.e("correcao", "E6: "+e.toString());
        }
    }

    private void compareSquaresAndCircles(){
        try {
            double thresholdY = height * limit;
            double thresholdX = width * limit;
            for (int i = 0; i < squaresQuestions.size(); i++) { // Intera sobre todas as questoes
                List<Point> box_rows = new ArrayList<>();
                Point questionY = squaresQuestions.get(i);
                for (int j = 0; j < markedCircles.size(); j++) {
                    Circle circleY = markedCircles.get(j);
                    if (getProportion(questionY.y, circleY.y, thresholdY)) { // Se o quadrado e circulo atual estiverem na mesma linha aproximada em y
                        box_rows.add(new Point(circleY.x, circleY.y)); // Adiciona o centro de massa do circulo atual na lista
                    }
                }
                if (box_rows.isEmpty()) { // Caso nenhum circulo encontrado para a questao atual
                    box_rows.add(new Point(0, 0)); // Adiciona 0 para a questão
                }
                SquaresCircles sc = new SquaresCircles(squaresQuestions.get(i), box_rows);
                squaresCircles.add(sc);
            }
            int[] letters = new int[8];
            for (int l = 0; l < 8; l++) {
                letters[l] = l + 1;
            }
            for (int i = 0; i < squaresCircles.size(); i++) {
                SquaresCircles item = squaresCircles.get(i);
                List<Point> listCirc = item.listCircles;
                if (listCirc.size() > 1) {
                    String markings = "";
                    for (int j = 0; j < listCirc.size(); j++) {
                        Point circ = listCirc.get(j);
                        for (int a = 0; a < squaresAltenatives.size(); a++) {
                            Point alt = squaresAltenatives.get(a);
                            if (getProportion(alt.x, circ.x, thresholdX)) {
                                markings = markings + String.valueOf(letters[a]);
                                paintCircle(circ, letters[a], i);
                                break;
                            }
                        }
                    }
                    //Log.e("correcao", "q: "+listCirc.size());
                    //Log.e("correcao","markings: "+markings);
                    gabarito.put(i + 1, Integer.valueOf(markings));
                } else {
                    Point circ = listCirc.get(0);
                    if (circ.x != 0) {
                        for (int j = 0; j < squaresAltenatives.size(); j++) {
                            Point alt = squaresAltenatives.get(j);
                            if (getProportion(alt.x, circ.x, thresholdX)) {
                                gabarito.put(i + 1, letters[j]);
                                paintCircle(circ, letters[j], i);
                                break;
                            }
                        }
                    } else {
                        gabarito.put(i + 1, 0);
                    }
                }
            }
        }catch (Exception e){
            Log.e("correcao", "E7: "+e.toString());
        }
    }

    private boolean getProportion(double a, double b, double threshold){
        double min = a - threshold;
        double max = a + threshold;
        return b > min && b < max;
    }

    private void paintCircle(Point p, int mark, int quest){
        try {
            String gabaritoBD = bancoDados.listarRespostasGabaritoNumerico(id_provaBD.toString());
            //Log.e("correcao", "c: " + gabaritoBD);
            double x = p.x - (width * 0.01);
            double y = p.y + (height * 0.01);
            char d = (char) (Integer.parseInt(String.valueOf(mark)) - 1 + 'A');
            char r = (char) ('A' + Integer.parseInt(String.valueOf(gabaritoBD.charAt(quest))) - 1);
            if (d == r) {
                Imgproc.circle(mat, p, (int) (width * 0.016), new Scalar(0, 255, 0), -1);
                Imgproc.putText(mat, String.valueOf(d), new Point(x,y), Imgproc.FONT_HERSHEY_SIMPLEX, width * 0.0008, new Scalar(0, 0, 0), 2);
            } else {
                Imgproc.circle(mat, p, (int) (width * 0.016), new Scalar(255, 0, 0), -1);
                Imgproc.putText(mat, String.valueOf(d), new Point(x,y), Imgproc.FONT_HERSHEY_SIMPLEX, width * 0.0008, new Scalar(255, 255, 255), 2);
            }
            //Imgproc.putText(mat, String.valueOf(d), new Point(x,y), Imgproc.FONT_HERSHEY_SIMPLEX, width * 0.001, new Scalar(0, 0, 0), 2);



        }catch (Exception e){
            Log.e("correcao", "E8: "+e.toString());
        }
    }

    private void paintCantos(){
        try {
            double limit2 = 0.05;
            Imgproc.rectangle(mat, new Point(0, 0), new Point(width * limit2, height * limit2), new Scalar(0, 0, 0), -1); // Canto superior esquerdo
            Imgproc.rectangle(mat, new Point(width - width * limit2, 0), new Point(width, height * limit2), new Scalar(0, 0, 0), -1); // Canto superior esquerdo
            Imgproc.rectangle(mat, new Point(0, height - height * limit2), new Point(width * limit2, height), new Scalar(0, 0, 0), -1); // canto inferior esquerdo
            Imgproc.rectangle(mat, new Point(width - width * limit2, height - height * limit2), new Point(width, height), new Scalar(0, 0, 0), -1); // canto inferior direito
        }catch (Exception e){
            Log.e("correcao", "E9: "+e.toString());
        }
    }

    private void eliminateContourQuests(){
        try {
            Point p1 = new Point(width * 0.09, height * 0.06); // Canto superior esquerdo
            Point p3 = new Point(0, height - height * 0.05); // Inferior
            for (int i = squaresQuestions.size() - 1 ; i >= 0; i--){
                Point p = squaresQuestions.get(i);
                if (p.y < p1.y && p.x < p1.x){
                    squaresQuestions.remove(i);
                }
                if (p.y > p3.y){
                    squaresQuestions.remove(i);
                }
            }
        }catch (Exception e){
            Log.e("correcao", "E10: "+e.toString());
        }
    }

    private void eliminateAlternatives(){
        try {
            Point p1 = new Point(width * 0.09, height * 0.06); // Canto superior esquerdo
            Point p2 = new Point(width - width * 0.09, 0); // Lado direito
            for (int i = squaresAltenatives.size() - 1 ; i >= 0; i--){
                Point p = squaresAltenatives.get(i);
                if (p.y < p1.y && p.x < p1.x){
                    squaresAltenatives.remove(i);
                }
                if (p.x > p2.x){
                    squaresAltenatives.remove(i);
                }
            }
        }catch (Exception e){
            Log.e("correcao", "E10: "+e.toString());
        }
    }

    private void enumerateQuestions(){
        int i = 1;
        for (Point q : squaresQuestions){
            double x = q.x + (width * 0.02);
            double y = q.y + (height * 0.01);
            Imgproc.putText(mat, String.valueOf(i), new Point(x,y), Imgproc.FONT_HERSHEY_SIMPLEX, width * 0.0008, new Scalar(0, 0, 0), 2);
            i++;
        }
    }


    public class Circle {

        double x, y, radius, xR, yR, wR, hR, perimeter;
        MatOfPoint contour;

        public Circle(double x, double y, double radius, double xR, double yR, double wR, double hR, MatOfPoint contour, double perimeter) {
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

    public class SquaresCircles{
        Point quetions;
        List<Point> listCircles;
        SquaresCircles(Point quetions, List<Point> listCircles){
            this.quetions = quetions;
            this.listCircles = listCircles;
        }
    }

}
