package mx.com.factico.diputinder;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;

import com.viewpagerindicator.CirclePageIndicator;

import mx.com.factico.diputinder.adapters.FragmentPagerAdapter;
import mx.com.factico.diputinder.beans.Text;
import mx.com.factico.diputinder.fragments.TextImagePageFragment;
import mx.com.factico.diputinder.fragments.TextPageFragment;
import mx.com.factico.diputinder.transformers.ParallaxTutorialPagerTransformer;
import mx.com.factico.diputinder.utils.PreferencesUtils;

/**
 * Created by zace3d on 18/05/15.
 */
public class TutorialActivity extends AppCompatActivity implements OnClickListener {
    public static final String TAG_CLASS = TutorialActivity.class.getSimpleName();
    private View btnSkipTutorial;
    private int CURRENT_POSITION = 0;
    private int COUNT_PAGES = 3;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        boolean isFirstTime = PreferencesUtils.getBooleanPreference(getApplication(), PreferencesUtils.TUTORIAL);
        if (isFirstTime) {
            initUI();
        } else {
            startMainIntent();
        }
    }

    private void startMainIntent() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
        // overridePendingTransition(0, 0);
        finish();
    }

    private void initUI() {
        mViewPager = (ViewPager) findViewById(R.id.tutorial_pager);

        // Creating an instance of PagerAdapter
        FragmentPagerAdapter mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager());

        Resources res = getResources();

        mPagerAdapter.addFragment(TextPageFragment.newInstance(0,
                new Text(res.getString(R.string.tutorial_title_1), res.getString(R.string.tutorial_message_1))));
        mPagerAdapter.addFragment(TextImagePageFragment.newInstance(1,
                new Text("", res.getString(R.string.tutorial_message_2))));
        mPagerAdapter.addFragment(TextPageFragment.newInstance(2,
                new Text("", res.getString(R.string.tutorial_message_3))));

        // Setting the PagerAdapter object to the viewPager object
        mViewPager.setAdapter(mPagerAdapter);
        //mViewPager.setOffscreenPageLimit(mPagerAdapter.getCount());
        mViewPager.setPageTransformer(false, new ParallaxTutorialPagerTransformer(R.id.text_iv_bgr));

        mViewPager.addOnPageChangeListener(VerticalOnPageChangeListener);

        //Bind the title indicator to the adapter
        CirclePageIndicator pagerIndicator = (CirclePageIndicator)findViewById(R.id.tutorial_pager_indicator);
        pagerIndicator.setViewPager(mViewPager);
        //pagerIndicator.setOnPageChangeListener(VerticalOnPageChangeListener);

        btnSkipTutorial = findViewById(R.id.tutorial_btn_skip);
        btnSkipTutorial.setOnClickListener(this);
    }

    ViewPager.OnPageChangeListener VerticalOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        private int previousState;
        private boolean userScrollChange;

        @Override
        public void onPageSelected(int position) {
            if (position == COUNT_PAGES - 1) {
                if (CURRENT_POSITION < position) {
                    btnSkipTutorial.setVisibility(View.VISIBLE);
                    btnSkipTutorial.setClickable(true);
                    btnSkipTutorial.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_right2left));
                } else {
                    btnSkipTutorial.setVisibility(View.GONE);
                    btnSkipTutorial.setClickable(false);
                }
            } else if (position == COUNT_PAGES - 2) {
                btnSkipTutorial.setVisibility(View.GONE);
                btnSkipTutorial.setClickable(false);
                if (CURRENT_POSITION > position) {
                    btnSkipTutorial.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_left2right));
                }
            } else {
                btnSkipTutorial.setVisibility(View.GONE);
                btnSkipTutorial.setClickable(false);
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
                PreferencesUtils.putBooleanPreference(getApplication(), PreferencesUtils.TUTORIAL, false);
                startMainIntent();
                break;
        }
    }
}