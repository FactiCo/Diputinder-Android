package mx.com.factico.diputinder.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by zace3d on 28/05/15.
 */
public class CacheUtils {

    public static void unbindDrawables(View view) {
        if (view != null) {
            if (view.getBackground() != null) {
                view.getBackground().setCallback(null);
            }
            if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    unbindDrawables(((ViewGroup) view).getChildAt(i));
                }

                if (!isAdapterView(view)) {
                    ((ViewGroup) view).removeAllViews();
                }
            }
        }
    }

    private static boolean isAdapterView(View view) {
        return view instanceof AdapterView;
    }

    public static void clearMemoryCache() {
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().stop();
    }

    public static void clearDiskCache() {
        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().stop();
    }
}
