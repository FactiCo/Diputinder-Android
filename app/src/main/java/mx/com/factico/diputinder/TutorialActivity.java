package mx.com.factico.diputinder;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.viewpagerindicator.CirclePageIndicator;

import mx.com.factico.diputinder.adapters.FragmentPagerAdapter;
import mx.com.factico.diputinder.beans.Text;
import mx.com.factico.diputinder.fragments.TextImagePageFragment;
import mx.com.factico.diputinder.fragments.TextPageFragment;
import mx.com.factico.diputinder.transformers.ParallaxTutorialPagerTransformer;
import mx.com.factico.diputinder.preferences.PreferencesManager;
import mx.com.factico.diputinder.utils.CacheUtils;

/**
 * Created by zace3d on 18/05/15.
 */
public class TutorialActivity extends AppCompatActivity implements OnClickListener {
    public static final String TAG = TutorialActivity.class.getName();

    private static final int COUNT_PAGES = 3;
    private int CURRENT_POSITION = 0;

    private ViewPager mViewPager;
    private CirclePageIndicator mPageIndicator;
    private Button mSkipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        boolean isFirstTime = PreferencesManager.getBooleanPreference(getApplication(), PreferencesManager.TUTORIAL);
        if (isFirstTime) {
            createViews();
            initViews();
        } else {
            MainActivity.startActivity(this);
            supportFinishAfterTransition();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        CacheUtils.unbindDrawables(findViewById(R.id.container));
        Runtime.getRuntime().gc();
    }

    private void createViews() {
        mViewPager = (ViewPager) findViewById(R.id.tutorial_pager);
        mPageIndicator = (CirclePageIndicator) findViewById(R.id.tutorial_pager_indicator);
        mSkipButton = (Button) findViewById(R.id.tutorial_btn_skip);
    }

    private void initViews() {
        FragmentPagerAdapter mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager());

        mPagerAdapter.addFragment(TextPageFragment.newInstance(0,
                new Text(getString(R.string.tutorial_title_1), getString(R.string.tutorial_message_1))));
        mPagerAdapter.addFragment(TextImagePageFragment.newInstance(1,
                new Text("", getString(R.string.tutorial_message_2))));
        mPagerAdapter.addFragment(TextPageFragment.newInstance(2,
                new Text("", getString(R.string.tutorial_message_3))));

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageTransformer(false, new ParallaxTutorialPagerTransformer(R.id.text_iv_bgr));
        mViewPager.addOnPageChangeListener(VerticalOnPageChangeListener);

        mPageIndicator.setViewPager(mViewPager);

        mSkipButton.setOnClickListener(this);
    }

    ViewPager.OnPageChangeListener VerticalOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        private int previousState;
        private boolean userScrollChange;

        @Override
        public void onPageSelected(int position) {
            if (position == COUNT_PAGES - 1) {
                if (CURRENT_POSITION < position) {
                    mSkipButton.setVisibility(View.VISIBLE);
                    mSkipButton.setClickable(true);
                    mSkipButton.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_right2left));
                } else {
                    mSkipButton.setVisibility(View.GONE);
                    mSkipButton.setClickable(false);
                }
            } else if (position == COUNT_PAGES - 2) {
                mSkipButton.setVisibility(View.GONE);
                mSkipButton.setClickable(false);
                if (CURRENT_POSITION > position) {
                    mSkipButton.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_left2right));
                }
            } else {
                mSkipButton.setVisibility(View.GONE);
                mSkipButton.setClickable(false);
            }

            CURRENT_POSITION = position;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (userScrollChange) {

            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (previousState == ViewPager.SCROLL_STATE_DRAGGING && state == ViewPager.SCROLL_STATE_SETTLING)
                userScrollChange = true;
            else if (previousState == ViewPager.SCROLL_STATE_SETTLING && state == ViewPager.SCROLL_STATE_IDLE)
                userScrollChange = false;

            previousState = state;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tutorial_btn_skip:
                PreferencesManager.putBooleanPreference(getApplication(), PreferencesManager.TUTORIAL, false);

                MainActivity.startActivity(this);
                supportFinishAfterTransition();
                break;
        }
    }
}