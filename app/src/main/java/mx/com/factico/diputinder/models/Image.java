package mx.com.factico.diputinder.models;

import java.io.Serializable;

/**
 * Created by Edgar Z. on 5/3/17.
 */

public class Image implements Serializable {
    private String url;
    private Thumb thumb;

    public class Thumb {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Thumb getThumb() {
        return thumb;
    }

    public void setThumb(Thumb thumb) {
        this.thumb = thumb;
    }
}
