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
import java.util.List;


public class Util {
    List<Point> squaresQuestions = new ArrayList<>();
    List<MatOfPoint> contours = new ArrayList<>();
    List<Point> squaresAltenatives = new ArrayList<>();
    //List<Point> markedCircles = new ArrayList<>();
    List<Circle> markedCircles = new ArrayList<>();
    Mat mat;
    int num_questionsBD, num_alternativesBD, height, width;

    public Util(Mat mat, int num_questionsBD, int num_alternativesBD){
        this.mat = mat;
        this.num_questionsBD = num_questionsBD;
        this.num_alternativesBD = num_alternativesBD;
    }

    /**
     * Método para validação de imagens para correção baseado nos contornos referentes aos quadrados das questões e alternativas
     * @return retorna true caso a imagem seja valida para correção e false caso contrário
     */
    public boolean correctCard() {
        boolean isValid = squares();
        if (isValid){ //Válida para buscar as respostas (marcações dos alunos)
            boolean resp = getAnswers();
            return resp;
        }else {
            return false;
        }
    }
    private boolean getAnswers(){
        circlesOfInterest();
        return true;
    }
    private boolean squares(){
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
                Imgproc.drawContours(imgAux, List.of(cnt), -1, new Scalar(0, 255, 0), 2);
            }
            if (y < height * 0.06){
                squaresAltenatives.add(new Point(x,y));
                Imgproc.drawContours(imgAux, List.of(cnt), -1, new Scalar(0, 255, 0), 2);
            }
        }
        int quest = analysisQuestions(height, width);
        int alt = analysisAlternatives(height, width);

        return quest == num_questionsBD && alt == num_alternativesBD;

        //return squaresQuestionsValid.size() + squaresAltenativesValid.size();
    }
    private Mat enhanceImage(Mat matImage) {
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
    private int analysisQuestions(int height, int width){
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

        for (int p = squaresQuestions.size() - 1; p > 0; p--){
            Point point1 = squaresQuestions.get(p);
            Point point2 = squaresQuestions.get(p-1);
            boolean analysis = analysisDistanceY(point1, point2, height, width);
            if (analysis){
                squaresQuestions.remove(p);
            }
        }

        return squaresQuestions.size();
    }
    private int analysisAlternatives(int height, int width){
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
        for (int p = squaresAltenatives.size() - 1; p > 0; p--){
            Point point1 = squaresAltenatives.get(p);
            Point point2 = squaresAltenatives.get(p-1);
            boolean analysis = analysisDistanceX(point1, point2, height, width);
            if (analysis){
                squaresAltenatives.remove(p);
            }
        }
        return squaresAltenatives.size();
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
        Mat matToCircles = mat;
        Point p1 = squaresQuestions.get(squaresQuestions.size() - 1); // Point da ultima questão
        Point p2 = squaresAltenatives.get(0); // Point da primeira alternativa
        Point p3 = squaresAltenatives.get(squaresAltenatives.size() - 1); // Point da ultima alternativa

        double limit = 0.02;

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
                    Log.e("test", "radius"+radius[0]);
                    Rect boundingRect = Imgproc.boundingRect(contour);
                    Circle circle = new Circle(center.x, center.y, radius[0], boundingRect.x, boundingRect.y, boundingRect.width, boundingRect.height, contour, Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true));
                    markedCircles.add(circle);
                    Imgproc.drawContours(matToCircles, List.of(contour), -1, new Scalar(255, 0, 0), 2);
                }
            }
        }
        Log.e("test", "Tot: "+markedCircles.size());
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
}
