package mx.com.factico.diputinder.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zace3d on 17/08/15.
 */
public class Territory implements Serializable {
    private long id;
    private String name;
    private Image image;

    private List<Position> positions;

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

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }
}
