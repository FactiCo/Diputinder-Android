package mx.com.factico.diputinder.beans;

import java.io.Serializable;

/**
 * Created by zace3d on 17/08/15.
 */
public class CandidateInfo implements Serializable {
    private String position;
    private Candidates candidate;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Candidates getCandidate() {
        return candidate;
    }

    public void setCandidates(Candidates candidate) {
        this.candidate = candidate;
    }
}