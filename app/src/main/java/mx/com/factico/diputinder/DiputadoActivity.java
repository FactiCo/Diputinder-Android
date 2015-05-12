package mx.com.factico.diputinder;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.Locale;

import mx.com.factico.diputinder.beans.Diputado;
import mx.com.factico.diputinder.dialogues.Dialogues;
import mx.com.factico.diputinder.httpconnection.HttpConnection;

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
    }

    protected void initUI() {
        options = new DisplayImageOptions.Builder()
                //.showImageOnLoading(R.drawable.ic_profile_orange)
                .showImageOnLoading(null)
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .showImageOnFail(R.drawable.ic_launcher)
                .resetViewBeforeLoading(true)
                //.cacheInMemory(false)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
    }

    protected void fillDiputado() {
        TextView tvName = (TextView) findViewById(R.id.diputado_tv_name);
        tvName.setText(String.format(Locale.getDefault(), "%s %s %s", diputado.getNombres(), diputado.getApellidoPaterno(), diputado.getApellidoMaterno()));

        ImageView ivProfile = (ImageView) findViewById(R.id.diputado_iv_profile);
        if (diputado.getTwitter() != null && !diputado.getTwitter().equals("")) {
            String twitter = diputado.getTwitter().replaceAll("\\s+", "");
            ImageLoader.getInstance().displayImage(String.format(Locale.getDefault(), HttpConnection.TWITTER_IMAGE_URL, twitter), ivProfile, options);
        } else {
            if (diputado.getGnero() != null) {
                if (diputado.getGnero().equals("F"))
                    ivProfile.setImageResource(R.drawable.ic_profile_women);
                else if (diputado.getGnero().equals("M"))
                    ivProfile.setImageResource(R.drawable.ic_profile_men);
            }
        }
    }
}
