package mx.com.factico.diputinder.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Edgar Z. on 5/3/17.
 */

public class HasTerritory implements Serializable {
    private Territory territory;
    private Position position;
    private List<Candidate> candidates;

    public Territory getTerritory() {
        return territory;
    }

    public void setTerritory(Territory territory) {
        this.territory = territory;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }
}