package mx.com.factico.diputinder.models;

import java.io.Serializable;

/**
 * Created by zace3d on 17/08/15.
 */
public class GeocoderResult implements Serializable {
    private Territory country;
    private Territory state;
    private Territory city;

    public Territory getCountry() {
        return country;
    }

    public void setCountry(Territory country) {
        this.country = country;
    }

    public Territory getState() {
        return state;
    }

    public void setState(Territory state) {
        this.state = state;
    }

    public Territory getCity() {
        return city;
    }

    public void setCity(Territory city) {
        this.city = city;
    }
}
