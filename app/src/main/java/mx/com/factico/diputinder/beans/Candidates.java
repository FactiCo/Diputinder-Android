package mx.com.factico.diputinder.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zace3d on 17/08/15.
 */
public class Candidates implements Serializable {
    private Candidate candidate;
    private List<Party> party;
    private List<Indicator> indicators;

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public List<Party> getParty() {
        return party;
    }

    public void setParty(List<Party> party) {
        this.party = party;
    }

    public List<Indicator> getIndicators() {
        return indicators;
    }

    public void setIndicators(List<Indicator> indicators) {
        this.indicators = indicators;
    }
}
