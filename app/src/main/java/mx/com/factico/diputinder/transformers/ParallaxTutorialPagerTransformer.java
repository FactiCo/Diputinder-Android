package mx.com.factico.diputinder.transformers;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;

import mx.com.factico.diputinder.R;

/**
 * Created by zace3d on 3/19/15.
 */
public class ParallaxTutorialPagerTransformer implements ViewPager.PageTransformer {

    private int id;
    private int border = 0;
    private float speed = 0.5f;

    public ParallaxTutorialPagerTransformer(int id) {
        this.id = id;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth() / 2;

        View parallaxView = view.findViewById(id);
        View tvTitle = view.findViewById(R.id.text_tv_title);
        View tvContent = view.findViewById(R.id.text_tv_content);
        View ivImage = view.findViewById(R.id.text_image_logo);

        if (parallaxView != null && Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            if (position > -1 && position < 1) {
                float height = parallaxView.getHeight();
                //parallaxView.setTranslationY(-(position * height * speed));
                float sc = ((float) view.getHeight() - border) / view.getHeight();
                if (position == 0) {
                    view.setScaleX(1);
                    view.setScaleY(1);
                } else {
                    view.setScaleX(sc);
                    view.setScaleY(sc);
                }

                if (tvTitle != null && tvContent != null) {
                    ivImage.setTranslationY(position * (pageWidth / 2));

                    tvTitle.setTranslationY(position * (pageWidth / 3));

                    tvContent.setTranslationY(position * (pageWidth / 2));

                    // The 0.5, 1.5, 1.7 values you see here are what makes the view move in a different speed.
                    // The bigger the number, the faster the view will translate.
                    // The result float is preceded by a minus because the views travel in the opposite direction of the movement.
                }
            }
        }
    }

    public void setBorder(int px) {
        border = px;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}