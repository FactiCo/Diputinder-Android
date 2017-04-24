package mx.com.factico.diputinder.beans;

import java.io.Serializable;

/**
 * Created by zace3d on 10/12/15.
 */
public class Messages implements Serializable {
    private long id;
    private long country_id;
    private String explanation_checked;
    private String explanation_missing;
    private String tweet_checked;
    private String tweet_missing;
    private String congratulation;
    private String demand;
    private String no_candidates;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCountryId() {
        return country_id;
    }

    public void setCountryId(long country_id) {
        this.country_id = country_id;
    }

    public String getExplanationChecked() {
        return explanation_checked;
    }

    public void setExplanationChecked(String explanation_checked) {
        this.explanation_checked = explanation_checked;
    }

    public String getExplanationMissing() {
        return explanation_missing;
    }

    public void setExplanationMissing(String explanation_missing) {
        this.explanation_missing = explanation_missing;
    }

    public String getTweetChecked() {
        return tweet_checked;
    }

    public void setTweetChecked(String tweet_checked) {
        this.tweet_checked = tweet_checked;
    }

    public String getTweetMissing() {
        return tweet_missing;
    }

    public void setTweetMissing(String tweet_missing) {
        this.tweet_missing = tweet_missing;
    }

    public String getDemand() {
        return demand;
    }

    public void setDemand(String demand) {
        this.demand = demand;
    }

    public String getCongratulation() {
        return congratulation;
    }

    public void setCongratulation(String congratulation) {
        this.congratulation = congratulation;
    }

    public String getNoCandidates() {
        return no_candidates;
    }

    public void setNoCandidates(String no_candidates) {
        this.no_candidates = no_candidates;
    }
}
