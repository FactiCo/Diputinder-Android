package mx.com.factico.diputinder.beans;

import java.io.Serializable;

/**
 * Created by zace3d on 17/08/15.
 */
public class Indicator implements Serializable {
    private long id;
    private String name;
    private String document;

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

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }
}
