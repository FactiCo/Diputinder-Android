package mx.com.factico.diputinder.helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by Edgar Z. on 04/07/16.
 */
public class RequestPermissionsHelper extends Fragment {
    public static final String TAG = RequestPermissionsHelper.class.getName();

    private static final int REQUEST_PERMISSIONS = 101;
    private static final int REQUEST_CHECK_SETTINGS = 102;

    private static final String PERMISSIONS_KEY = "permissions";

    private String[] PERMISSIONS;

    private PermissionCallback mCallback;
    private static boolean sPermissionDenied;


    public static Fragment newInstance(Bundle args) {
        Fragment fragment = new RequestPermissionsHelper();
        fragment.setArguments(args);
        return fragment;
    }

    public RequestPermissionsHelper() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        PERMISSIONS = getArguments() != null ? getArguments().getStringArray(PERMISSIONS_KEY) : null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        checkPermissions();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PermissionCallback) {
            mCallback = (PermissionCallback) context;
        } else {
            throw new IllegalArgumentException(
                    "activity must extend BaseActivity and " +
                            "implement RequestPermissionsHelper.PermissionCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @SuppressLint("NewApi")
    public void checkPermissions() {
        if (hasPermissionsGranted(getActivity(), PERMISSIONS)) {
            mCallback.onPermissionResult(true);
        } else {
            if (!sPermissionDenied) {
                requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
            }
        }
    }

    public static boolean shouldShowRequestPermissionRationale(Activity activity, String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasPermissionsGranted(Activity activity, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        if (mCallback != null) {
                            mCallback.onPermissionResult(true);
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        if (mCallback != null) {
                            mCallback.onPermissionResult(false);
                        }
                        break;
                    default:
                        if (mCallback != null) {
                            mCallback.onPermissionResult(false);
                        }
                        break;
                }
                break;
        }
    }

    public void setPermissionDenied(boolean permissionDenied) {
        this.sPermissionDenied = permissionDenied;
    }

    public static boolean isPermissionDenied() {
        return sPermissionDenied;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        boolean isGranted = true;
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length == PERMISSIONS.length) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        isGranted = false;
                        break;
                    }
                }
            } else {
                isGranted = false;
            }

            if (isGranted) {
                mCallback.onPermissionResult(true);
            } else {
                Log.i(TAG, "Permissions was NOT granted.");
                mCallback.onPermissionResult(false);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public static Fragment attach(FragmentManager fragmentManager, String[] permissions) {
        Fragment cameraHelper = fragmentManager.findFragmentByTag(TAG);
        if (cameraHelper == null) {
            Bundle args = new Bundle();
            args.putStringArray(PERMISSIONS_KEY, permissions);
            cameraHelper = RequestPermissionsHelper.newInstance(args);
            fragmentManager.beginTransaction().add(cameraHelper, TAG).commit();
        }
        return cameraHelper;
    }

    public interface PermissionCallback {
        void onPermissionResult(boolean successful);
    }
}
