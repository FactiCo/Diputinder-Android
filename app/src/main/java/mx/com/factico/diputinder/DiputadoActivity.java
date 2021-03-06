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
import java.util.Locale;

import mx.com.factico.diputinder.beans.Diputado;
import mx.com.factico.diputinder.beans.PartidoType;
import mx.com.factico.diputinder.dialogues.Dialogues;
import mx.com.factico.diputinder.httpconnection.HttpConnection;
import mx.com.factico.diputinder.utils.CacheUtils;
import mx.com.factico.diputinder.utils.ScreenUtils;
import mx.com.factico.diputinder.views.CustomTextView;

/**
 * Created by zace3d on 4/30/15.
 */
public class DiputadoActivity extends ActionBarActivity {
    public static final String TAG_CLASS = DiputadoActivity.class.getSimpleName();

    public static final String TAG_DIPUTADO = "diputado";
    private Diputado diputado;
    private DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diputado);

        setSupportActionBar();
        initUI();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            diputado = (Diputado) bundle.getSerializable(TAG_DIPUTADO);

            if (diputado != null) {
                //Dialogues.Toast(getBaseContext(), "DIPUTADO: " + diputado.getNombres(), Toast.LENGTH_SHORT);
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
        String nombres = diputado.getNombres() != null ? diputado.getNombres() : "";
        String apellidoPaterno = diputado.getApellidoPaterno() != null ? diputado.getApellidoPaterno() : "";
        String apellidoMaterno = diputado.getApellidoMaterno() != null ? diputado.getApellidoMaterno() : "";
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

        RelativeLayout.LayoutParams paramsIconsStatus = new RelativeLayout.LayoutParams(sizeIcons / 2, sizeIcons / 2);
        paramsIconsStatus.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        paramsIconsStatus.addRule(RelativeLayout.CENTER_VERTICAL);

        RelativeLayout.LayoutParams paramsIcons = new RelativeLayout.LayoutParams(sizeIcons, sizeIcons);
        paramsIcons.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        ImageView ivProfile = (ImageView) findViewById(R.id.diputado_iv_profile);
        ivProfile.setLayoutParams(new RelativeLayout.LayoutParams(width, width));
        if (diputado.getTwitter() != null && !diputado.getTwitter().equals("") && !diputado.getTwitter().equals("no se identificó")) {
            String twitter = diputado.getTwitter().replaceAll("\\s+", "");
            ImageLoader.getInstance().displayImage(String.format(Locale.getDefault(), HttpConnection.TWITTER_IMAGE_URL, twitter), ivProfile, options);
        } else {
            if (diputado.getGnero() != null) {
                if (diputado.getGnero().equals("F"))
                    ivProfile.setImageResource(R.drawable.ic_avatar_women);
                else if (diputado.getGnero().equals("M"))
                    ivProfile.setImageResource(R.drawable.ic_avatar_men);
            }
        }

        // Cargo
        CustomTextView tvCargo = (CustomTextView) findViewById(R.id.diputado_tv_cargo);
        tvCargo.setText(diputado.getPuesto());

        // Entidad
        CustomTextView tvEntidad = (CustomTextView) findViewById(R.id.diputado_tv_entidad);
        tvEntidad.setText(diputado.getEntidadFederativa());

        // City
        CustomTextView tvCity = (CustomTextView) findViewById(R.id.diputado_tv_city);

        String city = null;
        if (diputado.getMunicipioDelegacin() != null) {
            city = diputado.getMunicipioDelegacin();
        } else if (diputado.getDistritoElectoral() != null) {
            city = "Distrito " + diputado.getDistritoElectoral();
        }

        if (city != null)
            tvCity.setText(city);

        // Partido
        ImageView ivIcon = (ImageView) findViewById(R.id.diputado_iv_partido);
        LinearLayout.LayoutParams paramsPartido = new LinearLayout.LayoutParams(width / 2, width / 2);
        paramsPartido.gravity = Gravity.CENTER_HORIZONTAL;
        ivIcon.setLayoutParams(paramsPartido);
        ivIcon.setImageResource(PartidoType.getIconPartido(PartidoType.getPartidoType(diputado.getPartido())));

        // Tiene 3 de 3
        ImageView ivPatrimonial = (ImageView) findViewById(R.id.diputado_iv_patrimonial);
        ivPatrimonial.setLayoutParams(paramsIcons);
        ivPatrimonial.setOnClickListener(WebViewOnClickListener);

        ImageView ivIntereses = (ImageView) findViewById(R.id.diputado_iv_intereses);
        ivIntereses.setLayoutParams(paramsIcons);
        ivIntereses.setOnClickListener(WebViewOnClickListener);

        ImageView ivFiscal = (ImageView) findViewById(R.id.diputado_iv_fiscal);
        ivFiscal.setLayoutParams(paramsIcons);
        ivFiscal.setOnClickListener(WebViewOnClickListener);

        ImageView ivPatrimonialStatus = (ImageView) findViewById(R.id.diputado_iv_patrimonial_status);
        ivPatrimonialStatus.setLayoutParams(paramsIconsStatus);

        ImageView ivInteresesStatus = (ImageView) findViewById(R.id.diputado_iv_intereses_status);
        ivInteresesStatus.setLayoutParams(paramsIconsStatus);

        ImageView ivFiscalStatus = (ImageView) findViewById(R.id.diputado_iv_fiscal_status);
        ivFiscalStatus.setLayoutParams(paramsIconsStatus);

        if (diputado.getPatrimonialPDF() != null && !diputado.getPatrimonialPDF().equals("")) {
            ivPatrimonial.setTag(diputado.getPatrimonialPDF());
            ivPatrimonial.setImageResource(PartidoType.getIconPartidoPatrimonial(PartidoType.getPartidoType(diputado.getPartido())));

            ivPatrimonialStatus.setImageResource(R.drawable.ic_btn_declaro);
        }

        if (diputado.getInteresesPDF() != null && !diputado.getInteresesPDF().equals("")) {
            ivIntereses.setTag(diputado.getInteresesPDF());
            ivIntereses.setImageResource(PartidoType.getIconPartidoIntereses(PartidoType.getPartidoType(diputado.getPartido())));

            ivInteresesStatus.setImageResource(R.drawable.ic_btn_declaro);
        }

        if (diputado.getFiscalPDF() != null && !diputado.getFiscalPDF().equals("")) {
            ivFiscal.setTag(diputado.getFiscalPDF());
            ivFiscal.setImageResource(PartidoType.getIconPartidoFiscal(PartidoType.getPartidoType(diputado.getPartido())));

            ivFiscalStatus.setImageResource(R.drawable.ic_btn_declaro);
        }

        LinearLayout partidosContainer = (LinearLayout) findViewById(R.id.diputado_vg_partidos_container);
        if (diputado.getAlianza() != null && !diputado.getAlianza().equals("")) {
            if (isNumeric(diputado.getAlianza())) {

                if (Integer.parseInt(diputado.getAlianza()) == 1) {

                    String partidoEnAlianza = diputado.getPartidosEnAlianza();
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
        }
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
