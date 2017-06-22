package com.hellowo.teamfinder.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.IOException;

public class BitmapUtil {
    public static Bitmap makeProfileBitmapFromFile(String filePath) throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        float widthScale = options.outWidth / 128;
        float heightScale = options.outHeight / 128;
        float scale = widthScale > heightScale ? widthScale : heightScale;

        /*if(scale >= 16) {
            options.inSampleSize = 16;
        }else if(scale >= 14) {
            options.inSampleSize = 14;
        }else if(scale >= 12) {
            options.inSampleSize = 12;
        }else if(scale >= 10) {
            options.inSampleSize = 10;
        }else */if(scale >= 8) {
            options.inSampleSize = 8;
        }else if(scale >= 4) {
            options.inSampleSize = 4;
        }else if(scale >= 2) {
            options.inSampleSize = 2;
        }else {
            options.inSampleSize = 1;
        }

        options.inJustDecodeBounds = false;

        int degree = GetExifOrientation(filePath);
        return GetRotatedBitmap(BitmapFactory.decodeFile(filePath, options), degree);
    }

    public synchronized static int GetExifOrientation(String filepath)
    {
        int degree = 0;
        ExifInterface exif = null;

        try
        {
            exif = new ExifInterface(filepath);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if (exif != null)
        {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

            if (orientation != -1)
            {
                // We only recognize a subset of orientation tag values.
                switch(orientation)
                {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }

        return degree;
    }

    public synchronized static Bitmap GetRotatedBitmap(Bitmap bitmap, int degrees)
    {
        if ( degrees != 0 && bitmap != null )
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2 );
            try
            {
                Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != b2)
                {
                    bitmap.recycle();
                    bitmap = b2;
                }
            }
            catch (OutOfMemoryError ex)
            {
                // We have no memory to rotate. Return the original bitmap.
            }
        }

        return bitmap;
    }
}
