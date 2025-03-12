package online.padev.kariti.utils;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class EnhanceImage {
    public static Mat enhanceImage(Mat matImage) {
        try {
            if (matImage.empty()) {
                System.out.println("Erro ao carregar a imagem.");
                return null;
            }
            // Aumentar o brilho
            Mat brighterImage = new Mat();
            org.opencv.core.Core.add(matImage, new Scalar(50, 50, 50), brighterImage); // Aumenta o brilho

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
}
