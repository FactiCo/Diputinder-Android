package mx.com.factico.diputinder.models;

import java.io.Serializable;

/**
 * Created by zace3d on 17/08/15.
 */
public class CandidateInfo implements Serializable {
    private String position;
    private String territoryName;
    private Candidate candidate;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTerritoryName() {
        return territoryName;
    }

    public void setTerritoryName(String territoryName) {
        this.territoryName = territoryName;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }
}