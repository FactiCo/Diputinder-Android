package mx.com.factico.diputinder.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Edgar Z. on 5/3/17.
 */

public class Position implements Serializable {
    private long id;
    private String title;
    private String territory;
    private List<Candidate> candidates;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTerritory() {
        return territory;
    }

    public void setTerritory(String territory) {
        this.territory = territory;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }
}