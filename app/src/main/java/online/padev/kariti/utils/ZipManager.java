package online.padev.kariti.utils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ZipManager {
    private static final int PART_SIZE = 10 * 1024 * 1024; // 10MB
    public static void splitFile(String sourceFilePath, String destFolder) throws IOException {
        File sourceFile = new File(sourceFilePath);
        if (!sourceFile.exists()) {
            throw new FileNotFoundException("Arquivo não encontrado: " + sourceFilePath);
        }

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile))) {
            byte[] buffer = new byte[PART_SIZE];
            int partNumber = 1;
            int bytesRead;

            while ((bytesRead = bis.read(buffer)) > 0) {
                File partFile = new File(destFolder, sourceFile.getName() + "." + String.format("%03d", partNumber));
                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(partFile))) {
                    bos.write(buffer, 0, bytesRead);
                }
                partNumber++;
            }
        }
    }

    public static void main(String[] args) {
        String sourceFile = "/storage/emulated/0/Download/arquivo.zip"; // Caminho do arquivo ZIP
        String destFolder = "/storage/emulated/0/Download/partes/"; // Pasta de destino

        new File(destFolder).mkdirs(); // Criar diretório se não existir

        try {
            splitFile(sourceFile, destFolder);
            System.out.println("Arquivo dividido com sucesso!");
        } catch (IOException e) {
            Log.e("kariti", e.toString());
        }
    }
}
