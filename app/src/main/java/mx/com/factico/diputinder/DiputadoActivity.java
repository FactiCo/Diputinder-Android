package mx.com.factico.diputinder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
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
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

import mx.com.factico.diputinder.beans.Diputado;
import mx.com.factico.diputinder.beans.PartidoType;
import mx.com.factico.diputinder.dialogues.Dialogues;
import mx.com.factico.diputinder.httpconnection.HttpConnection;
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
                Dialogues.Toast(getBaseContext(), "DIPUTADO: " + diputado.getNombres(), Toast.LENGTH_SHORT);
                fillDiputado();
            }
        }
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
        CustomTextView tvName = (CustomTextView) findViewById(R.id.diputado_tv_name);
        tvName.setText(String.format(Locale.getDefault(), "%s %s %s", diputado.getNombres(), diputado.getApellidoPaterno(), diputado.getApellidoMaterno()));

        Point point = ScreenUtils.getScreenSize(getBaseContext());
        int width = point.x / 3;
        int height = point.y / 5;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(width / 2, 0, 0, 0);
        View vgInfo = findViewById(R.id.diputado_vg_info);
        vgInfo.setMinimumHeight(width);
        vgInfo.setPadding(width / 2, 0, 0, 0);
        vgInfo.setLayoutParams(params);

        ImageView ivProfile = (ImageView) findViewById(R.id.diputado_iv_profile);
        ivProfile.setLayoutParams(new RelativeLayout.LayoutParams(width, width));
        if (diputado.getTwitter() != null && !diputado.getTwitter().equals("")) {
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

        CustomTextView tvCargo = (CustomTextView) findViewById(R.id.diputado_tv_cargo);
        tvCargo.setText(diputado.getPuesto());

        CustomTextView tvEntidad = (CustomTextView) findViewById(R.id.diputado_tv_entidad);
        tvEntidad.setText(diputado.getEntidadFederativa());

        ImageView ivIcon = (ImageView) findViewById(R.id.diputado_iv_partido);
        LinearLayout.LayoutParams paramsPartido = new LinearLayout.LayoutParams(width / 2, width / 2);
        paramsPartido.gravity = Gravity.CENTER_HORIZONTAL;
        ivIcon.setLayoutParams(paramsPartido);
        ivIcon.setImageResource(PartidoType.getIconPartido(PartidoType.getPartidoType(diputado.getPartido())));

        if (diputado.getPatrimonialPDF() != null && !diputado.getPatrimonialPDF().equals("")) {
            ImageView ivPatrimonial = (ImageView) findViewById(R.id.diputado_iv_patrimonial);
            ivPatrimonial.setTag(diputado.getPatrimonialPDF());
            ivPatrimonial.setImageResource(PartidoType.getIconPartidoPatrimonial(PartidoType.getPartidoType(diputado.getPartido())));
            ivPatrimonial.setOnClickListener(WebViewOnClickListener);
        }

        if (diputado.getInteresesPDF() != null && !diputado.getInteresesPDF().equals("")) {
            ImageView ivIntereses = (ImageView) findViewById(R.id.diputado_iv_intereses);
            ivIntereses.setTag(diputado.getInteresesPDF());
            ivIntereses.setImageResource(PartidoType.getIconPartidoIntereses(PartidoType.getPartidoType(diputado.getPartido())));
            ivIntereses.setOnClickListener(WebViewOnClickListener);
        }

        if (diputado.getFiscalPDF() != null && !diputado.getFiscalPDF().equals("")) {
            ImageView ivFiscal = (ImageView) findViewById(R.id.diputado_iv_fiscal);
            ivFiscal.setTag(diputado.getFiscalPDF());
            ivFiscal.setImageResource(PartidoType.getIconPartidoFiscal(PartidoType.getPartidoType(diputado.getPartido())));
            ivFiscal.setOnClickListener(WebViewOnClickListener);
        }

        LinearLayout partidosContainer = (LinearLayout) findViewById(R.id.diputado_vg_partidos_container);
        if (diputado.getAlianza() != null && !diputado.getAlianza().equals("")) {
            if (isNumeric(diputado.getAlianza())) {
                if (Integer.parseInt(diputado.getAlianza()) == 1) {
                    String[] partidosAlianza = diputado.getPartidosEnAlianza().split(",");
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

        /*View vgPatrimonial = findViewById(R.id.diputado_vg_patrimonial);
        vgPatrimonial.setLayoutParams(new LinearLayout.LayoutParams(0, height, 1));

        View vgIntereses = findViewById(R.id.diputado_vg_intereses);
        vgIntereses.setLayoutParams(new LinearLayout.LayoutParams(0, height, 1));

        View vgFiscal = findViewById(R.id.diputado_vg_fiscal);
        LinearLayout.LayoutParams paramsFiscal = new LinearLayout.LayoutParams(height, height, 1);
        paramsFiscal.gravity = Gravity.CENTER_HORIZONTAL;
        vgFiscal.setLayoutParams(paramsFiscal);*/
    }

    private View.OnClickListener WebViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String url = v.getTag().toString();
            startWebViewIntent(url);
        }
    };

    private void startWebViewIntent(String url) {
        Intent intent = new Intent(getBaseContext(), WebViewActivity.class);
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
