package fr.wildcodeschool.vyfe.helper;

import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

public class ScrollHelper {
    public static void DownScroll(final ScrollView scrool){
        scrool.post(new Runnable() {
            public void run() {
                scrool.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    public static void RigthScroll(final HorizontalScrollView scrool){
        scrool.post(new Runnable() {
            public void run() {
                scrool.fullScroll(View.FOCUS_RIGHT);
            }
        });
    }
}
