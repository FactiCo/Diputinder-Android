package mx.com.factico.diputinder.beans;

import mx.com.factico.diputinder.R;

/**
 * Created by zace3d on 5/13/15.
 */
public enum PartidoType {
    PH,
    MC,
    PANAL,
    PES,
    PAN,
    PRD,
    PRI,
    PT,
    PVEM,
    MORENA,
    DEFAULT;

    public static int getIconPartido(PartidoType type) {
        switch (type) {
            case PES:
                return R.drawable.ic_logo_pes;
            case PH:
                return R.drawable.ic_logo_ph;
            case MC:
                return R.drawable.ic_logo_mc;
            case PANAL:
                return R.drawable.ic_logo_panal;
            case PAN:
                return R.drawable.ic_logo_pan;
            case PRD:
                return R.drawable.ic_logo_prd;
            case PRI:
                return R.drawable.ic_logo_pri;
            case PT:
                return R.drawable.ic_logo_pt;
            case PVEM:
                return R.drawable.ic_logo_pvem;
            case MORENA:
                return R.drawable.ic_logo_morena;
            default:
                return R.drawable.drawable_bgr_circle;
        }
    }

    public static PartidoType getPartidoType(String type) {
        switch (type) {
            case "PES":
                return PES;
            case "PH":
                return PH;
            case "MC":
                return MC;
            case "PANAL":
                return PANAL;
            case "PAN":
                return PAN;
            case "PRD":
                return PRD;
            case "PRI":
                return PRI;
            case "PT":
                return PT;
            case "PVEM":
                return PVEM;
            case "MORENA":
                return MORENA;
            default:
                return DEFAULT;
        }
    }

    public static int getIconPartidoFiscal(PartidoType type) {
        switch (type) {
            case PES:
                return R.drawable.ic_fiscal_on_pes;
            case PH:
                return R.drawable.ic_fiscal_on_ph;
            case MC:
                return R.drawable.ic_fiscal_on_mc;
            case PANAL:
                return R.drawable.ic_fiscal_on_panal;
            case PAN:
                return R.drawable.ic_fiscal_on_pan;
            case PRD:
                return R.drawable.ic_fiscal_on_prd;
            case PRI:
                return R.drawable.ic_fiscal_on_pri;
            case PT:
                return R.drawable.ic_fiscal_on_pt;
            case PVEM:
                return R.drawable.ic_fiscal_on_pvem;
            case MORENA:
                return R.drawable.ic_fiscal_on_morena;
            default:
                return R.drawable.ic_fiscal_off;
        }
    }

    public static int getIconPartidoIntereses(PartidoType type) {
        switch (type) {
            case PES:
                return R.drawable.ic_intereses_on_pes;
            case PH:
                return R.drawable.ic_intereses_on_ph;
            case MC:
                return R.drawable.ic_intereses_on_mc;
            case PANAL:
                return R.drawable.ic_intereses_on_panal;
            case PAN:
                return R.drawable.ic_intereses_on_pan;
            case PRD:
                return R.drawable.ic_intereses_on_prd;
            case PRI:
                return R.drawable.ic_intereses_on_pri;
            case PT:
                return R.drawable.ic_intereses_on_pt;
            case PVEM:
                return R.drawable.ic_intereses_on_pvem;
            case MORENA:
                return R.drawable.ic_intereses_on_morena;
            default:
                return R.drawable.ic_intereses_off;
        }
    }

    public static int getIconPartidoPatrimonial(PartidoType type) {
        switch (type) {
            case PES:
                return R.drawable.ic_patrimonial_on_pes;
            case PH:
                return R.drawable.ic_patrimonial_on_ph;
            case MC:
                return R.drawable.ic_patrimonial_on_mc;
            case PANAL:
                return R.drawable.ic_patrimonial_on_panal;
            case PAN:
                return R.drawable.ic_patrimonial_on_pan;
            case PRD:
                return R.drawable.ic_patrimonial_on_prd;
            case PRI:
                return R.drawable.ic_patrimonial_on_pri;
            case PT:
                return R.drawable.ic_patrimonial_on_pt;
            case PVEM:
                return R.drawable.ic_patrimonial_on_pvem;
            case MORENA:
                return R.drawable.ic_patrimonial_on_morena;
            default:
                return R.drawable.ic_patrimonial_off;
        }
    }
}
