package mx.com.factico.diputinder.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jordymelendez on 18/05/15.
 */
public class Candidatos implements Serializable {
    private List<Candidate> candidatos = new ArrayList<>();

    public List<Candidate> getCandidatos() {
        return candidatos;
    }

    public void setCandidatos(List<Candidate> candidatos) {
        this.candidatos = candidatos;
    }
}
