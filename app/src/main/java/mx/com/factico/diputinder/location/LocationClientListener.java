package mx.com.factico.diputinder.location;

/**
 * Created by zace3d on 1/26/15.
 */

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

import mx.com.factico.diputinder.R;
import mx.com.factico.diputinder.dialogues.Dialogues;

/**
 * Created by zace3d on 26/05/15.
 */
public class LocationClientListener implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final String TAG_CLASS = LocationClientListener.class.getSimpleName();
    private final Activity activity;

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    private OnLocationClientListener onLocationClientListener;
    private Location mLastLocation;

    public LocationClientListener(Activity activity) {
        this.activity = activity;

        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void connect() {
        mGoogleApiClient.connect();
    }

    public void disconnect() {
        mGoogleApiClient.disconnect();
    }

    protected void createLocationRequest() {
        // Get last known location
        // LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second
        mLocationRequest.setFastestInterval(5000);

        if (LocationUtils.isGpsOrNetworkProviderEnabled(activity)) {
            startLocationUpdates();
        } else {
            Dialogues.Toast(activity, activity.getResources().getString(R.string.no_gps_enabled), Toast.LENGTH_LONG);

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                setOnLocationChanged(mLastLocation);
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Dialogues.Log(TAG_CLASS, "GoogleApiClient connection was successful", Log.INFO);
        // Dialogues.Toast(activity, "GoogleApiClient connection was successful", Toast.LENGTH_LONG);

        createLocationRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Dialogues.Log(TAG_CLASS, "GoogleApiClient connection has been suspend", Log.INFO);
        // Dialogues.Toast(activity, "GoogleApiClient connection has been suspend", Toast.LENGTH_LONG);

        stopLocationUpdates();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Dialogues.Log(TAG_CLASS, "GoogleApiClient connection has failed", Log.INFO);
        // Dialogues.Toast(activity, "GoogleApiClient connection has failed", Toast.LENGTH_LONG);
    }

    @Override
    public void onLocationChanged(Location location) {
        String mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        setOnLocationChanged(location);
    }

    public void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    public void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private void setOnLocationChanged(Location location) {
        if (onLocationClientListener != null) {
            onLocationClientListener.onLocationChanged(location);
        }
    }

    public void setOnLocationClientListener(OnLocationClientListener onLocationClientListener) {
        this.onLocationClientListener = onLocationClientListener;
    }

    public interface OnLocationClientListener {
        void onLocationChanged(Location location);
    }
}