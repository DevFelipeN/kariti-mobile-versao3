package online.padev.kariti.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class ZIpDirectory {
    public static File createDirectoryZip(String nameFile, Context context) {
        try {
            File externalDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CameraXopenCV");
            File fileZip = new File(externalDir, nameFile+".zip");
            if (!fileZip.exists()) {
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
}
