package online.padev.kariti.cards;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;

import online.padev.kariti.R;

public class LibQr {
    /*
    public static Bitmap createQrCode2(String text) {
        text = formatTextToQr(text);
        int width = 270;
        int height = 270;
        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;

        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
     */

    public static Bitmap createQrCode(String text, int typeQr, Context context ) {
        text = formatTextToQr(text, typeQr);
        int width = 270;
        int height = 270;
        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }


            int logoSize = width / 8;

            Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_qr);

            Bitmap scaledLogo = Bitmap.createScaledBitmap(logo, logoSize, logoSize, false);

            // Desenha a logo no centro do QR Code
            Canvas canvas = new Canvas(bitmap);
            int centerX = (width - scaledLogo.getWidth()) / 2;
            int centerY = (height - scaledLogo.getHeight()) / 2;
            canvas.drawBitmap(scaledLogo, centerX, centerY, null);

            return bitmap;

        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatTextToQr(String text, int typeQr){
        if (text == null) return null;
        char symbol = '#';
        if (typeQr == 1) symbol = '$';
        return String.format("%1$" + 40 + "s", text).replace(' ', symbol); // Monta uma string com no minimo 40 caracteres
    }
}
