package mx.com.factico.diputinder.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by Edgar Z. on 21/04/17.
 */

public class ImageUtils {

    public static DisplayImageOptions buildDisplayImageOptions() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(null)
                .showImageOnLoading(null)
                .showImageForEmptyUri(null)
                .showImageOnFail(null)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                //.displayer(new FadeInBitmapDisplayer(200))
                .build();
    }
}
