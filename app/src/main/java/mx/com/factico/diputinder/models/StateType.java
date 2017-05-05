package mx.com.factico.diputinder.models;

/**
 * Created by zace3d on 5/27/15.
 */
public enum StateType {
    AGUASCALIENTES,
    BAJA_CALIFORNIA,
    BAJA_CALIFORNIA_SUR,
    CAMPECHE,
    CHIAPAS,
    CHIHUAHUA,
    COAHUILA,
    COLIMA,
    DISTRITO_FEDERAL,
    DURANGO,
    ESTADO_DE_MÉXICO,
    GUERRERO,
    GUANAJUATO,
    HIDALGO,
    JALISCO,
    MICHOACAN,
    MORELOS,
    NAYARIT,
    NUEVO_LEON,
    OAXACA,
    PUEBLA,
    QUERETARO,
    QUINTANA_ROO,
    SAN_LUIS_POTOSI,
    SINALOA,
    SONORA,
    TABASCO,
    TAMAULIPAS,
    TLAXCALA,
    VERACRUZ,
    YUCATAN,
    ZACATECAS,
    DEFAULT;

    public static String getStateName(StateType type) {
        switch (type) {
            case AGUASCALIENTES:
                return "Aguascalientes";
            case BAJA_CALIFORNIA:
                return "Baja California";
            case BAJA_CALIFORNIA_SUR:
                return "Baja California Sur";
            case CAMPECHE:
                return "Campeche";
            case CHIAPAS:
                return "Chiapas";
            case CHIHUAHUA:
                return "Chihuahua";
            case COAHUILA:
                return "Coahuila";
            case COLIMA:
                return "Colima";
            case DISTRITO_FEDERAL:
                return "Distrito Federal";
            case DURANGO:
                return "Durango";
            case ESTADO_DE_MÉXICO:
                return "México";
            case GUERRERO:
                return "Guerrero";
            case GUANAJUATO:
                return "Guanajuato";
            case HIDALGO:
                return "Hidalgo";
            case JALISCO:
                return "Jalisco";
            case MICHOACAN:
                return "Michoacán";
            case MORELOS:
                return "Morelos";
            case NAYARIT:
                return "Nayarit";
            case NUEVO_LEON:
                return "Nuevo León";
            case OAXACA:
                return "Oaxaca";
            case PUEBLA:
                return "Puebla";
            case QUERETARO:
                return "Querétaro";
            case QUINTANA_ROO:
                return "Quintana Roo";
            case SAN_LUIS_POTOSI:
                return "San Luis Potosí";
            case SINALOA:
                return "Sinaloa";
            case SONORA:
                return "Sonora";
            case TABASCO:
                return "Tabasco";
            case TAMAULIPAS:
                return "Tamaulipas";
            case TLAXCALA:
                return "Tlaxcala";
            case VERACRUZ:
                return "Veracruz";
            case YUCATAN:
                return "Yucatán";
            case ZACATECAS:
                return "Zacatecas";
            default:
                return "";
        }
    }

    public static StateType getStateType(String type) {
        switch (type) {
            case "Aguascalientes":
                return AGUASCALIENTES;
            case "Baja California":
                return BAJA_CALIFORNIA;
            case "Baja California Sur":
                return BAJA_CALIFORNIA_SUR;
            case "Campeche":
                return CAMPECHE;
            case "Chiapas":
                return CHIAPAS;
            case "Chihuahua":
                return CHIHUAHUA;
            case "Coahuila de Zaragoza":
                return COAHUILA;
            case "Colima":
                return COLIMA;
            case "Distrito Federal":
                return DISTRITO_FEDERAL;
            case "Durango":
                return DURANGO;
            case "Estado de México":
                return ESTADO_DE_MÉXICO;
            case "Guerrero":
                return GUERRERO;
            case "Guanajuato":
                return GUANAJUATO;
            case "Hidalgo":
                return JALISCO;
            case "Jalisco":
                return HIDALGO;
            case "Morelia":
                return MICHOACAN;
            case "Michoacán":
                return MORELOS;
            case "Nayarit":
                return NAYARIT;
            case "Nuevo León":
                return NUEVO_LEON;
            case "Oaxaca":
                return OAXACA;
            case "Puebla":
                return PUEBLA;
            case "Querétaro":
                return QUERETARO;
            case "Quintana Roo":
                return QUINTANA_ROO;
            case "San Luis Potosí":
                return SAN_LUIS_POTOSI;
            case "Sinaloa":
                return SINALOA;
            case "Sonora":
                return SONORA;
            case "Tabasco":
                return TABASCO;
            case "Tamaulipas":
                return TAMAULIPAS;
            case "Tlaxcala":
                return TLAXCALA;
            case "Veracruz":
                return VERACRUZ;
            case "Yucatan":
                return YUCATAN;
            case "Zacatecas":
                return ZACATECAS;
            default:
                return DEFAULT;
        }
    }
}
