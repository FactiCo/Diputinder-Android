package mx.com.factico.diputinder.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jordymelendez on 18/05/15.
 */
public class Candidatos implements Serializable {
    private List<Diputado> candidatos = new ArrayList<>();

    public List<Diputado> getCandidatos() {
        return candidatos;
    }

    public void setCandidatos(List<Diputado> candidatos) {
        this.candidatos = candidatos;
    }
}
