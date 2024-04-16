package com.example.kariti;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraNoAppActivity extends AppCompatActivity {
    private Camera camera;
    private CameraPreview cameraPreview;
    private Button btnCapturar;
    String nomeImagem, caminho;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_no_app);

        camera = getCameraInstace();
        cameraPreview = new CameraPreview(this, camera);
        btnCapturar = findViewById(R.id.buttonCameraFoto);
        FrameLayout previewFrame = (FrameLayout) findViewById(R.id.frameCamera);
        previewFrame.addView(cameraPreview);

        nomeImagem = getIntent().getExtras().getString("nomeImagem");

        btnCapturar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                camera.takePicture(null, null, pictureCallback);
            }
        });
    }
    //Iniciando a camera
    public static Camera getCameraInstace() {
        Camera c = null;
        try{
            c = Camera.open();
            Camera.Parameters params = c.getParameters();
            c.setDisplayOrientation(90);
            c.setParameters(params);
        }catch (Exception e){
            Log.e("Error: ", e.getMessage());
        }
        return c;
    }

    //salvanco Imagem
    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            File pictureFile = getOutPutMediaFile();
            if (pictureFile == null){
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(bytes);
                fos.close();
                Toast.makeText(CameraNoAppActivity.this, "Imagem Capturada", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), GaleriaActivity.class);
                intent.putExtra("caminho", caminho);
                startActivity(intent);
                finish();
                //camera.startPreview();
            }catch (Exception e){
                Log.e("Error:", e.getMessage());
            }
        }
    };

    //Criando diretorio
    private File getOutPutMediaFile(){
        File mediaFile = null;
        File mediaDir = new File(getExternalFilesDir(null), "/Cartoes");
        caminho = mediaDir.getPath();

        if (!mediaDir.exists()){
            if (!mediaDir.mkdirs()){
                Log.d("Error File:", "Falha ao criar o diretório");
                return null;
            }
        }

        //String nomeImagem = new SimpleDateFormat("HH_mm_ss").format(new Date());
        mediaFile = new File(mediaDir.getPath() + File.separator + nomeImagem);
        return mediaFile;
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera(){
        if (camera != null){
            camera.release();
            camera = null;
        }
    }
}