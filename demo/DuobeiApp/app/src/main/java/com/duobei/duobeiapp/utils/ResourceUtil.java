package com.duobei.duobeiapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.util.TypedValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author wangning
 *         Date: 2015-06-23
 *         desc:
 */
public class ResourceUtil {

    public static float getFloatFromResource(Context context, int resId) {
        TypedValue outValue = new TypedValue();
        context.getResources().getValue(resId, outValue, true);

        return outValue.getFloat();
    }

    public static int getDimensionPixelFromResource(Context context, int resID) {
        return context.getResources().getDimensionPixelSize(resID);
    }

    public static int pixelToDp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public static int dpToPixel(Context context, int dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int dpToPixel(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 获取视频缩略图
     *
     * @param videoPath
     * @param width
     * @param height
     * @param kind
     * @return
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

   

    public static String loadTextFromFile(File file) {
        final StringBuilder sb = new StringBuilder();
        if (file != null && file.exists()) {
            BufferedReader fr = null;
            try {
                fr = new BufferedReader(new FileReader(file));
                String s;
                while ((s = fr.readLine()) != null) {
                    sb.append(s);
                }
            } catch (FileNotFoundException e1) {
                // 不会发生.
            } catch (IOException e) {
                return "";
            } finally {
                try {
                    if (fr != null)
                        fr.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sb.toString();
    }


}
