package mx.com.factico.diputinder.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zace3d on 17/08/15.
 */
public class Territory {
    private long id;
    private String name;
    private long country_id;
    private long state_id;
    private String image;

    private List<Positions> positions;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCountry_id() {
        return country_id;
    }

    public void setCountry_id(long country_id) {
        this.country_id = country_id;
    }

    public long getState_id() {
        return state_id;
    }

    public void setState_id(long state_id) {
        this.state_id = state_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Positions> getPositions() {
        return positions;
    }

    public void setPositions(List<Positions> positions) {
        this.positions = positions;
    }

    public class Positions implements Serializable {
        private long id;
        private String title;
        private List<Candidates> candidates;

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

        public List<Candidates> getCandidates() {
            return candidates;
        }

        public void setCandidates(List<Candidates> candidates) {
            this.candidates = candidates;
        }
    }
}
