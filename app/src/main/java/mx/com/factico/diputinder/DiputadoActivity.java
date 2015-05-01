package mx.com.factico.diputinder;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import mx.com.factico.diputinder.beans.Diputado;
import mx.com.factico.diputinder.dialogues.Dialogues;

/**
 * Created by zace3d on 4/30/15.
 */
public class DiputadoActivity extends ActionBarActivity {
    public static final String TAG_CLASS = DiputadoActivity.class.getSimpleName();

    public static final String TAG_DIPUTADO = "diputado";
    private Diputado diputado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diputado);

        setSupportActionBar();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            diputado = (Diputado) bundle.getSerializable(TAG_DIPUTADO);

            if (diputado != null) {
                Dialogues.Toast(getBaseContext(), "DIPUTADO: " + diputado.nombres, Toast.LENGTH_SHORT);
            }
        }
        initUI();
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

    }
}
