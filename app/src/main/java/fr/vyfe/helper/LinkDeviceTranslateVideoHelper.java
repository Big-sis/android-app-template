package fr.vyfe.helper;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class LinkDeviceTranslateVideoHelper {
    private static byte[] inputData = new byte[0];
    private static InputStream iStream = null;



    public static byte[] getVideo(String fromFile, Context context) {
        File file = new File(fromFile);
        final long length = file.length();
        HashMap<String,byte[]> hashMap = null;

        //transformation du lien de stockage en vidéo
        try {
            iStream = context.getContentResolver().openInputStream(Uri.fromFile(file));
            inputData = getBytes(iStream);
           return inputData;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("t", "onClick: "+e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("tt", "onClick: "+e.getMessage());
        }
        return null;
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        int k = byteBuffer.size();

        return byteBuffer.toByteArray();
    }

}
