package com.hellowo.teamfinder.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class ViewUtil {

    /**
     * 뷰가 그려진 후에 콜백 실행해줌
     * @param view
     */
    public static void runCallbackAfterViewDrawed(final View view, final Runnable callback){
        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        callback.run();
                    }
                });
    }

    public static float dpToPx(Context c, float dp) {
        float density = c.getResources().getDisplayMetrics().density;
        return dp * density;
    }

    public static void hideKeyPad(Activity activity, EditText editText) {
        InputMethodManager imm
                = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void disableParentsClip(@NonNull View view) {
        while (view.getParent() != null &&
                view.getParent() instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            viewGroup.setClipChildren(false);
            viewGroup.setClipToPadding(false);
            view = viewGroup;
        }
    }

    public static void enableParentsClip(@NonNull View view) {
        while (view.getParent() != null &&
                view.getParent() instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            viewGroup.setClipChildren(true);
            viewGroup.setClipToPadding(true);
            view = viewGroup;
        }
    }
}
