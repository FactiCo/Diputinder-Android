package mx.com.factico.diputinder.beans;

/**
 * Created by zace3d on 5/13/15.
 */
public enum CandidateType {
    DEFAULT,
    DIPUTADO,
    GOBERNADOR,
    ALCALDIAS;

    public static final int DEFAULT_ID = -1;
    public static final int DIPUTADO_ID = 1;
    public static final int GOBERNADOR_ID = 2;
    public static final int ALCALDIAS_ID = 3;

    public static int getCandidatoTypeId(CandidateType type) {
        switch (type) {
            case DIPUTADO:
                return DIPUTADO_ID;
            case GOBERNADOR:
                return GOBERNADOR_ID;
            case ALCALDIAS:
                return ALCALDIAS_ID;
            default:
                return DEFAULT_ID;
        }
    }

    public static CandidateType getCandidatoType(int type) {
        switch (type) {
            case DIPUTADO_ID:
                return DIPUTADO;
            case GOBERNADOR_ID:
                return GOBERNADOR;
            case ALCALDIAS_ID:
                return ALCALDIAS;
            default:
                return DEFAULT;
        }
    }
}
