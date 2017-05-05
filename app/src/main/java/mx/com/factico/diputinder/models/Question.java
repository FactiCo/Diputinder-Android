package mx.com.factico.diputinder.models;

import java.io.Serializable;

/**
 * Created by Edgar Z. on 5/3/17.
 */

public class Question implements Serializable {
    private String content;
    private String element_type;
    private Answer answer;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getElementType() {
        return element_type;
    }

    public void setElementType(String element_type) {
        this.element_type = element_type;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }
}
