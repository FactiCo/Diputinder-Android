package mx.com.factico.diputinder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import mx.com.factico.diputinder.fragments.MainFragment;
import mx.com.factico.diputinder.fragments.NoLocationFragment;
import mx.com.factico.diputinder.fragments.NoPermissionsFragment;
import mx.com.factico.diputinder.helpers.RequestPermissionsHelper;
import mx.com.factico.diputinder.location.LocationUtils;
import mx.com.factico.diputinder.utils.CacheUtils;
import mx.com.factico.diputinder.utils.Constants;

/**
 * Created by zace3d on 18/05/15.
 */
public class MainActivity extends AppCompatActivity implements RequestPermissionsHelper.PermissionCallback {
    public static final String TAG = MainActivity.class.getName();

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildActionBar();

        validatePermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (hasPermissionsGranted())
            validateLocationServices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        CacheUtils.clearMemoryCache();
    }

    protected void buildActionBar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.actionbar);
        mToolbar.setTitle("");
        TextView actionbarTitle = (TextView) mToolbar.findViewById(R.id.actionbar_title);
        actionbarTitle.setText(getResources().getString(R.string.app_name));

        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_action_info));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.startActivity(getBaseContext());
            }
        });
    }

    private void validatePermissions() {
        if (!hasPermissionsGranted())
            requestLocationPermissions();
        else
            validateLocationServices();
    }

    private boolean hasPermissionsGranted() {
        return RequestPermissionsHelper.hasPermissionsGranted(this, Constants.LOCATION_PERMISSIONS);
    }

    public void requestLocationPermissions() {
        if (RequestPermissionsHelper.shouldShowRequestPermissionRationale(this, Constants.LOCATION_PERMISSIONS)) {
            showConfirmationDialog();
        } else {
            RequestPermissionsHelper.attach(getSupportFragmentManager(), Constants.LOCATION_PERMISSIONS);
        }
    }

    private void validateLocationServices() {
        if (!LocationUtils.isGpsOrNetworkProviderEnabled(getBaseContext())) {
            updateNoLocationActivatedFragment();
        } else {
            updateFragment();
        }
    }

    @Override
    public void onPermissionResult(boolean successful) {
        if (successful) {
            if (hasPermissionsGranted()) {
                validatePermissions();
                Snackbar.make(findViewById(R.id.content_frame), "Permisos otorgados", Snackbar.LENGTH_LONG).show();
            }
        } else {
            updatePermissionsFragment();
            Snackbar.make(findViewById(R.id.content_frame), "Permisos no otorgados", Snackbar.LENGTH_LONG).show();
        }
    }

    private AlertDialog showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permisos");
        builder.setMessage("Se necesitan permisos de ubicación para mostrarte los candidatos que te corresponden.");
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                onBackPressed();
            }
        });
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                RequestPermissionsHelper.attach(getSupportFragmentManager(), Constants.LOCATION_PERMISSIONS);
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onBackPressed();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        return dialog;
    }

    private void updatePermissionsFragment() {
        Fragment fragment = NoPermissionsFragment.newInstance();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commitAllowingStateLoss();
    }

    private void updateNoLocationActivatedFragment() {
        Fragment fragment = NoLocationFragment.newInstance();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commitAllowingStateLoss();
    }

    private void updateFragment() {
        Fragment fragment = MainFragment.newInstance();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }
}
