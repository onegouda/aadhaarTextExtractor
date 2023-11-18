package com.example.aadhaartextextractor.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraUtils {
    public static File createImagefile(Context context) throws IOException {
        //create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG" + timeStamp + "_" ;
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        //save file path for use ACTION_VIEW intent

        return image;
    }
    public static Bitmap getBitmap(String path){
        try{
            File file =new File(path);
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            return myBitmap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
