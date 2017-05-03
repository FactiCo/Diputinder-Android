package mx.com.factico.diputinder.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zace3d on 17/08/15.
 */
public class Indicator implements Serializable {
    private long id;
    private String name;
    private List<Section> sections;

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

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }
}
