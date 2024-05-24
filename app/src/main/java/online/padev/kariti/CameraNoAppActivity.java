package online.padev.kariti;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import online.padev.kariti.R;

import java.io.File;
import java.io.FileOutputStream;

public class CameraNoAppActivity extends AppCompatActivity {
    private Camera camera;
    private CameraPreview cameraPreview;
    private Button btnCapturar;
    String nomeImagem;
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
                intent.putExtra("nomeImagem", nomeImagem);
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
        if (!mediaDir.exists()){
            if (!mediaDir.mkdirs()){
                Log.d("Error File:", "Falha ao criar o diretório");
                return null;
            }
        }
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
    public void onBackPressed() {
        Compactador.listCartoes.clear();
        super.onBackPressed();
    }
}