package mx.com.factico.diputinder.location;

import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import mx.com.factico.diputinder.models.Address;
import mx.com.factico.diputinder.models.StateType;
import mx.com.factico.diputinder.dialogues.Dialogues;

/**
 * Created by zace3d on 4/29/15.
 */
public class LocationUtils {
    public static final String TAG_CLASS = LocationUtils.class.getSimpleName();

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
        geocoder = new Geocoder(context, new Locale("es", "MX"));

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

    public static String getStateFromLatLong(Context context, double latitude, double longitude) {
        Geocoder geocoder;
        List<android.location.Address> addresses;
        geocoder = new Geocoder(context, new Locale("es", "MX"));

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if (addresses != null && addresses.size() > 0) {
                return addresses.get(0).getAdminArea();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static void getStateFromLatLong(Context context) {
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.AGUASCALIENTES) +
                LocationUtils.getStateFromLatLong(context, 21.8852562, -102.2915677), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.BAJA_CALIFORNIA) +
                LocationUtils.getStateFromLatLong(context, 31.8860389, -116.6003659), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.BAJA_CALIFORNIA_SUR) +
                LocationUtils.getStateFromLatLong(context, 26.0444446, -111.6660725), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.CAMPECHE) +
                LocationUtils.getStateFromLatLong(context, 19.8301251, -90.5349087), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.CHIAPAS) +
                LocationUtils.getStateFromLatLong(context, 16.3224417, -91.7910619), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.CHIHUAHUA) +
                LocationUtils.getStateFromLatLong(context, 28.6329957, -106.0691004), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.COAHUILA) +
                LocationUtils.getStateFromLatLong(context, 27.058676, -101.7068294), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.COLIMA) +
                LocationUtils.getStateFromLatLong(context, 19.2452342, -103.7240868), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.DISTRITO_FEDERAL) +
                LocationUtils.getStateFromLatLong(context, 19.4326077, -99.133208), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.DURANGO) +
                LocationUtils.getStateFromLatLong(context, 24.0277202, -104.6531759), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.ESTADO_DE_MÉXICO) +
                LocationUtils.getStateFromLatLong(context, 19.4968732, -99.7232673), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.GUERRERO) +
                LocationUtils.getStateFromLatLong(context, 17.4391926, -99.5450974), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.GUANAJUATO) +
                LocationUtils.getStateFromLatLong(context, 21.0190145, -101.2573586), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.HIDALGO) +
                LocationUtils.getStateFromLatLong(context, 20.0910963, -98.7623874), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.JALISCO) +
                LocationUtils.getStateFromLatLong(context, 20.6595382, -103.3494376), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.MICHOACAN) +
                LocationUtils.getStateFromLatLong(context, 19.5665192, -101.7068294), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.MORELOS) +
                LocationUtils.getStateFromLatLong(context, 18.6813049, -99.1013498), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.NAYARIT) +
                LocationUtils.getStateFromLatLong(context, 21.7513844, -104.8454619), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.NUEVO_LEON) +
                LocationUtils.getStateFromLatLong(context, 25.592172, -99.9961947), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.OAXACA) +
                LocationUtils.getStateFromLatLong(context, 17.0594169, -96.7216219), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.PUEBLA) +
                LocationUtils.getStateFromLatLong(context, 19.0412967, -98.2061996), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.QUERETARO) +
                LocationUtils.getStateFromLatLong(context, 20.5887932, -100.3898881), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.QUINTANA_ROO) +
                LocationUtils.getStateFromLatLong(context, 19.1817393, -88.4791376), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.SAN_LUIS_POTOSI) +
                LocationUtils.getStateFromLatLong(context, 22.1564699, -100.9855409), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.SINALOA) +
                LocationUtils.getStateFromLatLong(context, 25.825701, -108.214302), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.SONORA) +
                LocationUtils.getStateFromLatLong(context, 30.6212872, -109.9652404), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.TABASCO) +
                LocationUtils.getStateFromLatLong(context, 17.8409173, -92.6189273), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.TAMAULIPAS) +
                LocationUtils.getStateFromLatLong(context, 24.26694, -98.8362755), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.TLAXCALA) +
                LocationUtils.getStateFromLatLong(context, 19.3181521, -98.2375146), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.VERACRUZ) +
                LocationUtils.getStateFromLatLong(context, 19.173773, -96.1342241), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.YUCATAN) +
                LocationUtils.getStateFromLatLong(context, 20.7098786, -89.0943377), Log.INFO);
        Dialogues.Log(TAG_CLASS, StateType.getStateName(StateType.ZACATECAS) +
                LocationUtils.getStateFromLatLong(context, 22.7709249, -102.5832539), Log.INFO);
    }
}
