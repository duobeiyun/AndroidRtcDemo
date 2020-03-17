package com.duobei.duobeiapp.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    public static void showLongToast(Context context, String message){
        Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
