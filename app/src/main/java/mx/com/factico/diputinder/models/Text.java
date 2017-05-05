package mx.com.factico.diputinder.models;

import java.io.Serializable;

/**
 * Created by zace3d on 18/05/15.
 */
public class Text implements Serializable {
    private String title;
    private String content;

    public Text(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
