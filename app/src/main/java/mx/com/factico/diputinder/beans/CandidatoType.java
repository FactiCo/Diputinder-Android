package mx.com.factico.diputinder.beans;

/**
 * Created by zace3d on 5/13/15.
 */
public enum CandidatoType {
    DEFAULT,
    DIPUTADO,
    GOBERNADOR,
    PRESIDENTE_PARTIDO;

    public static final int DEFAULT_ID = -1;
    public static final int DIPUTADO_ID = 1;
    public static final int GOBERNADOR_ID = 2;
    public static final int PRESIDENTE_PARTIDO_ID = 3;

    public static int getCandidatoTypeId(CandidatoType type) {
        switch (type) {
            case DIPUTADO:
                return DIPUTADO_ID;
            case GOBERNADOR:
                return GOBERNADOR_ID;
            case PRESIDENTE_PARTIDO:
                return PRESIDENTE_PARTIDO_ID;
            default:
                return DEFAULT_ID;
        }
    }

    public static CandidatoType getCandidatoType(int type) {
        switch (type) {
            case DIPUTADO_ID:
                return DIPUTADO;
            case GOBERNADOR_ID:
                return GOBERNADOR;
            case PRESIDENTE_PARTIDO_ID:
                return PRESIDENTE_PARTIDO;
            default:
                return DEFAULT;
        }
    }
}
