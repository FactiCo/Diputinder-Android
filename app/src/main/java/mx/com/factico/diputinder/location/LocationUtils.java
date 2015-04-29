package mx.com.factico.diputinder.location;

import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import mx.com.factico.diputinder.beans.Address;

/**
 * Created by zace3d on 4/29/15.
 */
public class LocationUtils {

    public static boolean isGpsOrNetworkProviderEnabled(Context context) {
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Do what you need if enabled...
            return true;
        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //Do what you need if not enabled...
            return true;
        } else {
            return false;
        }
    }

    public static LatLng getLatLngFromLocation(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    public static Address getAdressFromLatLong(Context context, double latitude, double longitude) {
        Geocoder geocoder;
        List<android.location.Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                Address addressBean = new Address();
                addressBean.setAddress(address);
                addressBean.setCity(city);
                addressBean.setState(state);
                addressBean.setCountry(country);
                addressBean.setPostalCode(postalCode);
                addressBean.setKnownName(knownName);

                return addressBean;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
