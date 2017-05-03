package mx.com.factico.diputinder.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zace3d on 4/27/15.
 */
public class Candidate implements Serializable {
    private long id;
    private String nombres;
    private String apellido_paterno;
    private String apellido_materno;
    private String twitter;
    private String facebook;
    private String email;
    private String party_id;
    private String position_id;
    private String created_at;
    private String updated_at;
    private String territory_id;

    private Party party;
    private List<Indicator> indicators;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidoPaterno() {
        return apellido_paterno;
    }

    public void setApellidoPaterno(String apellido_paterno) {
        this.apellido_paterno = apellido_paterno;
    }

    public String getApellidoMaterno() {
        return apellido_materno;
    }

    public void setApellidoMaterno(String apellido_materno) {
        this.apellido_materno = apellido_materno;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPartyId() {
        return party_id;
    }

    public void setPartyId(String party_id) {
        this.party_id = party_id;
    }

    public String getPositionId() {
        return position_id;
    }

    public void setPositionId(String position_id) {
        this.position_id = position_id;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdatedAt() {
        return updated_at;
    }

    public void setUpdatedAt(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getTerritoryId() {
        return territory_id;
    }

    public void setTerritoryId(String territory_id) {
        this.territory_id = territory_id;
    }


    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public List<Indicator> getIndicators() {
        return indicators;
    }

    public void setIndicators(List<Indicator> indicators) {
        this.indicators = indicators;
    }
}
