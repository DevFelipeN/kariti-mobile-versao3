package online.padev.kariti.utils;

import android.graphics.Bitmap;

import org.opencv.core.Mat;

public class MatToBitmap {
    public static Bitmap toBitmap(Mat mat) {
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        org.opencv.android.Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }
}
