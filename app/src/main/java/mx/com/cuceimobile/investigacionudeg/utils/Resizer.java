package mx.com.cuceimobile.investigacionudeg.utils;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * Created by donluigimx on 15/11/16.
 */

public class Resizer {

    static public void image_button(ImageButton bttn, int width, int height) {
        ViewGroup.LayoutParams params;
        params = bttn.getLayoutParams();
        params.width = width;
        params.height = height;
    }
}
