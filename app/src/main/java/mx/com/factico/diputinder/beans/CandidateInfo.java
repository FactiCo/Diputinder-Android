package mx.com.factico.diputinder.beans;

import java.io.Serializable;

/**
 * Created by zace3d on 17/08/15.
 */
public class CandidateInfo implements Serializable {
    private String position;
    private String territoryName;
    private Candidates candidate;

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

    public Candidates getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidates candidate) {
        this.candidate = candidate;
    }
}