package online.padev.kariti.utils;

import static online.padev.kariti.utils.ZIpDirectory.createDirectoryZip;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import online.padev.kariti.emails.SendImageTester;

public class ZipManager {
    private static final int PART_SIZE = 15 * 1024 * 1024; // 10MB
    public static boolean controllerZip(File sourceFile, Context context) throws IOException {

        if (!sourceFile.exists()) {
            throw new FileNotFoundException("Arquivo não encontrado: " + sourceFile);
        }

        long fileSizeInBytes = sourceFile.length();
        long fileSizeInKB = fileSizeInBytes / 1024;
        long fileSizeInMB = fileSizeInKB / 1024;

        Log.e("kariti", "TamanhoZIp: "+fileSizeInMB);

        List<File> filesZIp = new ArrayList<>();

        if (fileSizeInMB > 15) {
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile))) {
                byte[] buffer = new byte[PART_SIZE];
                int partNumber = 1;
                int bytesRead;

                while ((bytesRead = bis.read(buffer)) > 0) {
                    String nameZipOrig = sourceFile.getName().replaceAll(".zip", "");
                    File destFolder = createDirectoryZip("Parte"+partNumber+"_"+nameZipOrig, context);
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFolder))) {
                        bos.write(buffer, 0, bytesRead);
                    }
                    partNumber++;
                    filesZIp.add(destFolder);
                }
            }
        }else{
            filesZIp.add(sourceFile);
        }

        boolean controllerSend = true;

        for (File fileZip : filesZIp) {
            boolean isSend = SendImageTester.sendInZip(fileZip);
            if (!isSend){
                controllerSend = false;
            }
        }
        return controllerSend;
    }
}
