package mx.com.factico.diputinder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.List;
import java.util.Locale;

import mx.com.factico.diputinder.beans.Candidate;
import mx.com.factico.diputinder.beans.CandidateInfo;
import mx.com.factico.diputinder.beans.Indicator;
import mx.com.factico.diputinder.beans.PartidoType;
import mx.com.factico.diputinder.beans.Party;
import mx.com.factico.diputinder.dialogues.Dialogues;
import mx.com.factico.diputinder.httpconnection.HttpConnection;
import mx.com.factico.diputinder.utils.CacheUtils;
import mx.com.factico.diputinder.utils.ScreenUtils;
import mx.com.factico.diputinder.views.CustomTextView;

/**
 * Created by zace3d on 4/30/15.
 */
public class CandidateActivity extends ActionBarActivity {
    public static final String TAG_CLASS = CandidateActivity.class.getSimpleName();

    public static final String TAG_CANDIDATE = "candidate";
    private CandidateInfo candidateInfo;
    private DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diputado);

        setSupportActionBar();
        initUI();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            candidateInfo = (CandidateInfo) bundle.getSerializable(TAG_CANDIDATE);

            if (candidateInfo != null) {
                //Dialogues.Toast(getBaseContext(), "DIPUTADO: " + candidateInfo.getNombres(), Toast.LENGTH_SHORT);
                fillDiputado();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        CacheUtils.unbindDrawables(findViewById(R.id.container));
    }

    @Override
    protected void onPause() {
        super.onPause();

        CacheUtils.clearMemoryCache();
    }

    protected void setSupportActionBar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.actionbar);
        mToolbar.setTitle("");
        mToolbar.getBackground().setAlpha(255);
        TextView actionbarTitle = (TextView) mToolbar.findViewById(R.id.actionbar_title);
        actionbarTitle.setText(getResources().getString(R.string.app_name));

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void initUI() {
        options = new DisplayImageOptions.Builder()
                //.showImageOnLoading(null)
                .showImageForEmptyUri(R.drawable.ic_avatar_no)
                .showImageOnFail(R.drawable.ic_avatar_no)
                .resetViewBeforeLoading(true)
                //.cacheInMemory(false)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                //.displayer(new FadeInBitmapDisplayer(300))
                .build();
    }

    protected void fillDiputado() {

        Candidate candidate = candidateInfo.getCandidate().getCandidate();
        if (candidate != null) {
            String nombres =  candidate.getNombres() != null ? candidate.getNombres() : "";
            String apellidoPaterno = candidate.getApellidoPaterno() != null ? candidate.getApellidoPaterno() : "";
            String apellidoMaterno = candidate.getApellidoMaterno() != null ? candidate.getApellidoMaterno() : "";

            CustomTextView tvName = (CustomTextView) findViewById(R.id.diputado_tv_name);
            tvName.setText(String.format(Locale.getDefault(), "%s %s %s", nombres, apellidoPaterno, apellidoMaterno));

            Point point = ScreenUtils.getScreenSize(getBaseContext());
            int width = point.x / 3;
            int height = point.y / 5;

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(width / 2, 0, 0, 0);
            View vgInfo = findViewById(R.id.diputado_vg_info);
            vgInfo.setMinimumHeight(width);
            vgInfo.setPadding(width / 2, 0, 0, 0);
            vgInfo.setLayoutParams(params);

            int sizeIcons = point.x / 5;

            ImageView ivProfile = (ImageView) findViewById(R.id.diputado_iv_profile);
            ivProfile.setLayoutParams(new RelativeLayout.LayoutParams(width, width));

            if (candidate.getTwitter() != null && !candidate.getTwitter().equals("") && !candidate.getTwitter().equals("no se identificó")) {
                String twitter = candidate.getTwitter().replaceAll("\\s+", "");
                ImageLoader.getInstance().displayImage(String.format(Locale.getDefault(), HttpConnection.TWITTER_IMAGE_URL, twitter), ivProfile, options);
            } else {
                ivProfile.setImageResource(R.drawable.ic_avatar_men_square);
            }

            // Cargo
            CustomTextView tvCargo = (CustomTextView) findViewById(R.id.diputado_tv_cargo);
            tvCargo.setText(candidateInfo.getPosition());

            // Entidad
            CustomTextView tvEntidad = (CustomTextView) findViewById(R.id.diputado_tv_entidad);
            tvEntidad.setText("TerritoryID: " + candidate.getTerritoryId());

            if (candidateInfo.getCandidate() != null) {
                // Partido
                ImageView ivIcon = (ImageView) findViewById(R.id.diputado_iv_partido);
                LinearLayout.LayoutParams paramsPartido = new LinearLayout.LayoutParams(width / 2, width / 2);
                paramsPartido.gravity = Gravity.CENTER_HORIZONTAL;
                ivIcon.setLayoutParams(paramsPartido);
                if (candidateInfo.getCandidate() != null) {
                    List<Party> parties = candidateInfo.getCandidate().getParty();
                    if (parties != null && parties.size() > 0) {
                        ImageLoader.getInstance().displayImage(parties.get(0).getImage(), ivIcon, options);
                    }
                }

                List<Indicator> indicators = candidateInfo.getCandidate().getIndicators();
                if (indicators != null && indicators.size() > 0) {
                    LinearLayout vgIndicators = (LinearLayout) findViewById(R.id.diputado_vg_indicators);

                    for (Indicator indicator : indicators) {
                        View indicatorView = createIndicatorView(indicator, sizeIcons);

                        if (indicatorView != null)
                            vgIndicators.addView(indicatorView);

                    }
                }
            }
        }

        /*
        // City
        CustomTextView tvCity = (CustomTextView) findViewById(R.id.diputado_tv_city);

        String city = null;
        if (candidateInfo.getMunicipioDelegacin() != null) {
            city = candidateInfo.getMunicipioDelegacin();
        } else if (candidateInfo.getDistritoElectoral() != null) {
            city = "Distrito " + candidateInfo.getDistritoElectoral();
        }

        if (city != null)
            tvCity.setText(city);

        LinearLayout partidosContainer = (LinearLayout) findViewById(R.id.diputado_vg_partidos_container);
        if (candidateInfo.getAlianza() != null && !candidateInfo.getAlianza().equals("")) {
            if (isNumeric(candidateInfo.getAlianza())) {

                if (Integer.parseInt(candidateInfo.getAlianza()) == 1) {

                    String partidoEnAlianza = candidateInfo.getPartidosEnAlianza();
                    if (partidoEnAlianza != null) {
                        String[] partidosAlianza = partidoEnAlianza.replaceAll("\\s+", "").split(",");
                        boolean aliados = false;
                        for (String partidoAlianza : partidosAlianza) {
                            View view = createPartidoImage(partidoAlianza, width);

                            if (view != null) {
                                partidosContainer.addView(view);

                                aliados = true;
                            }
                        }

                        if (aliados)
                            findViewById(R.id.diputado_vg_alianzas_container).setVisibility(View.VISIBLE);
                    }
                }
            }
        }*/
    }

    protected View createIndicatorView(Indicator indicator, int sizeIcons) {
        RelativeLayout view = (RelativeLayout) getLayoutInflater().inflate(R.layout.item_indicator, null, false);
        view.setOnClickListener(WebViewOnClickListener);

        RelativeLayout.LayoutParams paramsIconsStatus = new RelativeLayout.LayoutParams(sizeIcons / 2, sizeIcons / 2);
        paramsIconsStatus.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        paramsIconsStatus.addRule(RelativeLayout.CENTER_VERTICAL);

        RelativeLayout.LayoutParams paramsIcons = new RelativeLayout.LayoutParams(sizeIcons, sizeIcons);
        paramsIcons.setMargins(5, 10, 5, 10);
        paramsIcons.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        CustomTextView tvName = (CustomTextView) view.findViewById(R.id.indicator_tv_name);
        tvName.setText(indicator.getName());

        ImageView ivIndicator = (ImageView) view.findViewById(R.id.indicator_iv_icon);
        ivIndicator.setLayoutParams(paramsIcons);

        ImageView ivIndicatorStatus = (ImageView) view.findViewById(R.id.indicator_iv_status);
        ivIndicatorStatus.setLayoutParams(paramsIconsStatus);

        if (indicator.getDocument() != null && !indicator.getDocument().equals("")) {
            view.setTag(indicator.getDocument());

            //ivIndicator.setImageResource(PartidoType.getIconPartidoPatrimonial(PartidoType.getPartidoType("PT")));

            ivIndicatorStatus.setImageResource(R.drawable.ic_btn_declaro);
        }

        return view;
    }

    private View.OnClickListener WebViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null) {
                String url = v.getTag().toString();

                if (url != null && !url.equals(""))
                    startWebViewIntent(url);
                else
                    Dialogues.Toast(getBaseContext(), "No ha presentado documento.", Toast.LENGTH_SHORT);
            } else {
                Dialogues.Toast(getBaseContext(), "No ha presentado documento.", Toast.LENGTH_SHORT);
            }
        }
    };

    private void startWebViewIntent(String url) {
        Intent intent = new Intent(getBaseContext(), PdfViewerActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    public static boolean isNumeric(String str) {
        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);
        return str.length() == pos.getIndex();
    }

    protected View createPartidoImage(String partido, int width) {
        ImageView ivIcon = new ImageView(getBaseContext());
        ivIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        //ivIcon.setLayoutParams(new LinearLayout.LayoutParams((int) px, (int) px));
        LinearLayout.LayoutParams paramsPartido = new LinearLayout.LayoutParams(width / 2, width / 2);
        ivIcon.setLayoutParams(paramsPartido);
        ivIcon.setImageResource(PartidoType.getIconPartido(PartidoType.getPartidoType(partido)));

        return ivIcon;
    }
}
