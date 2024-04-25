package com.example.kariti;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageDialog extends Dialog {
    //private Bitmap bitmap;
    private String imagePath;

    public ImageDialog(Context context, String imagePath) {
        super(context);
        this.imagePath = imagePath;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_image);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        Window window = getWindow();
//        if (window != null) {
//            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//            layoutParams.copyFrom(window.getAttributes());
//            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT; // Largura personalizada
//            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Altura personalizada
//            window.setAttributes(layoutParams);
//        }
        ImageView imageView = findViewById(R.id.imageViewDialog);
        //imageView.setImageBitmap(bitmap);

        // Carregar a imagem usando Glide a partir do caminho do arquivo
        Glide.with(getContext())
                .load(imagePath)
                .into(imageView);
    }
}
