package online.padev.kariti;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ViewImage extends AppCompatActivity {

    private ImageView imageProcessada;
    private Button nao, sim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        imageProcessada = findViewById(R.id.ImagemProcessada);
        nao = findViewById(R.id.buttonNao);
        sim = findViewById(R.id.buttonSim);

        String filePath = getIntent().getStringExtra("filePath");
        if (filePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            imageProcessada.setImageBitmap(bitmap);
        }
        String fileImg = getIntent().getStringExtra("nameImag");
        sim.setOnClickListener(v -> confirmaCartao(fileImg));
        nao.setOnClickListener(v -> { Toast.makeText(this, "Imagem não adicionada a fila!!", Toast.LENGTH_SHORT).show();
            newIntent();});
    }
    private void newIntent(){
        Intent intent = new Intent(this, CameraxAndOpencv.class);
        startActivity(intent);
        finish();
    }
    private void confirmaCartao(String nameCartao){
        String n = nameCartao+".png";
        if(!Compactador.listCartoes.contains(n)) {
            Compactador.listCartoes.add(n);
            Toast.makeText(this, "Imagem adicionada a fila!!", Toast.LENGTH_SHORT).show();
            newIntent();
        }else{
            Toast.makeText(this, "Imagem já adicionada a fila!!", Toast.LENGTH_SHORT).show();
            newIntent();
        }
    }

}