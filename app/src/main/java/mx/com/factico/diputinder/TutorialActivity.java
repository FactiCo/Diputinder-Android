package mx.com.factico.diputinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;

import mx.com.factico.diputinder.adapters.FragmentPagerAdapter;
import mx.com.factico.diputinder.beans.Text;
import mx.com.factico.diputinder.fragments.TextPageFragment;
import mx.com.factico.diputinder.transformers.ParallaxTutorialPagerTransformer;
import mx.com.factico.diputinder.utils.PreferencesUtils;

/**
 * Created by zace3d on 18/05/15.
 */
public class TutorialActivity extends ActionBarActivity implements OnClickListener {
    public static final String TAG_CLASS = TutorialActivity.class.getSimpleName();
    private View btnSkipTutorial;
    private int CURRENT_POSITION = 0;
    private int COUNT_PAGES = 5;
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

        mPagerAdapter.addFragment(TextPageFragment.newInstance(0,
                new Text("¡HOLA! Bienvenido a Ligue Político.", "Con Ligue Político podrás conocer quiénes son tus candidatos a diputados federales de acuerdo a tu ubicación geográfica, y saber si están genuinamente comprometidos con la transparencia y la rendición de cuentas.")));
        mPagerAdapter.addFragment(TextPageFragment.newInstance(1,
                new Text("", "Ligue Político reúne datos e indicadores de “Por el México que merecemos” y “Candidato Transparente”, iniciativas de la sociedad civil que buscan visualizar compromiso de los candidatos en torno a la transparencia y la rendición de cuentas.")));
        mPagerAdapter.addFragment(TextPageFragment.newInstance(2,
                new Text("", "Si tus candidatos no han presentado su declaración fiscal (si han pagado o no sus impuestos), su declaración de intereses (si han realizado actividades o tienen relaciones que podrían interferir con el ejercicio de sus funciones) y su declaración patrimonial (el valor de los bienes que posee), en Ligue Político podrás exigírselas. ")));
        mPagerAdapter.addFragment(TextPageFragment.newInstance(3,
                new Text("", "Ligue Político es una iniciativa ciudadana promovida por: \n" +
                        "Sociedad en Movimiento\n" +
                        "Fáctico\n" +
                        "Coparmex\n" +
                        "IMCO\n")));
        mPagerAdapter.addFragment(TextPageFragment.newInstance(4,
                new Text("", "Si un candidato te late, desliza a la derecha. ¡Pero aguas! Si no ha presentado su 3de3, ¡pídeselos!")));

        // Setting the PagerAdapter object to the viewPager object
        mViewPager.setAdapter(mPagerAdapter);
        //mViewPager.setOffscreenPageLimit(mPagerAdapter.getCount());
        mViewPager.setPageTransformer(false, new ParallaxTutorialPagerTransformer(R.id.text_iv_bgr));

        mViewPager.setOnPageChangeListener(VerticalOnPageChangeListener);

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