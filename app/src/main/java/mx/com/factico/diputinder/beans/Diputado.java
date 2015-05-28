package mx.com.factico.diputinder.beans;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by zace3d on 4/27/15.
 */
public class Diputado implements Serializable {
    private String id;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String gnero;
    private String partido;
    private String puesto;
    private String circunscripcinElectoral;
    private String distritoElectoral;
    private String entidadFederativa;
    private String entidadAbrev;
    private String noDeMunicipios;
    private String twitter;
    private String alianza;
    private String partidosEnAlianza;
    private String patrimonial;
    private String patrimonialPDF;
    private String intereses;
    private String interesesPDF;
    private String fiscal;
    private String fiscalPDF;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getGnero() {
        return gnero;
    }

    public void setGnero(String gnero) {
        this.gnero = gnero;
    }

    public String getPartido() {
        return partido;
    }

    public void setPartido(String partido) {
        this.partido = partido;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public String getCircunscripcinElectoral() {
        return circunscripcinElectoral;
    }

    public void setCircunscripcinElectoral(String circunscripcinElectoral) {
        this.circunscripcinElectoral = circunscripcinElectoral;
    }

    public String getDistritoElectoral() {
        return distritoElectoral;
    }

    public void setDistritoElectoral(String distritoElectoral) {
        this.distritoElectoral = distritoElectoral;
    }

    public String getEntidadFederativa() {
        return entidadFederativa;
    }

    public void setEntidadFederativa(String entidadFederativa) {
        this.entidadFederativa = entidadFederativa;
    }

    public String getEntidadAbrev() {
        return entidadAbrev;
    }

    public void setEntidadAbrev(String entidadAbrev) {
        this.entidadAbrev = entidadAbrev;
    }

    public String getNoDeMunicipios() {
        return noDeMunicipios;
    }

    public void setNoDeMunicipios(String noDeMunicipios) {
        this.noDeMunicipios = noDeMunicipios;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getAlianza() {
        return alianza;
    }

    public void setAlianza(String alianza) {
        this.alianza = alianza;
    }

    public String getPartidosEnAlianza() {
        return partidosEnAlianza;
    }

    public void setPartidosEnAlianza(String partidosEnAlianza) {
        this.partidosEnAlianza = partidosEnAlianza;
    }

    public String getPatrimonial() {
        return patrimonial;
    }

    public void setPatrimonial(String patrimonial) {
        this.patrimonial = patrimonial;
    }

    public String getIntereses() {
        return intereses;
    }

    public void setIntereses(String intereses) {
        this.intereses = intereses;
    }

    public String getFiscal() {
        return fiscal;
    }

    public void setFiscal(String fiscal) {
        this.fiscal = fiscal;
    }

    public String getPatrimonialPDF() {
        return patrimonialPDF;
    }

    public void setPatrimonialPDF(String patrimonialPDF) {
        this.patrimonialPDF = patrimonialPDF;
    }

    public String getInteresesPDF() {
        return interesesPDF;
    }

    public void setInteresesPDF(String interesesPDF) {
        this.interesesPDF = interesesPDF;
    }

    public String getFiscalPDF() {
        return fiscalPDF;
    }

    public void setFiscalPDF(String fiscalPDF) {
        this.fiscalPDF = fiscalPDF;
    }

    /*@Override
    public int hashCode() {
        int hash = 3;
        hash = 7 * hash + this.getNombres().hashCode();
        hash = 7 * hash + this.getApellidoPaterno().hashCode();
        hash = 7 * hash + this.getApellidoMaterno().hashCode();
        hash = 7 * hash + this.getEntidadFederativa().hashCode();
        hash = 7 * hash + this.getDistritoElectoral().hashCode();
        return hash;
    }*/

    @Override
    public boolean equals(Object o) {
        boolean isEqual = false;

        if(this == o) return true;
        if (o == null)  return false;
        if (o instanceof Diputado) {
            Diputado diputado = (Diputado) o;
            isEqual = ((this.nombres != null && this.nombres.equals(diputado.nombres))
                    && (this.apellidoPaterno != null && this.apellidoPaterno.equals(diputado.apellidoPaterno))
                    && (this.apellidoMaterno != null && this.apellidoMaterno.equals(diputado.apellidoMaterno))
                    && (this.entidadFederativa != null && this.entidadFederativa.equals(diputado.entidadFederativa))
                    && (this.distritoElectoral != null && this.distritoElectoral.equals(diputado.distritoElectoral)));
        }

        return isEqual;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (null == nombres ? 0 : nombres.hashCode());
        hash = 31 * hash + (null == apellidoPaterno ? 0 : apellidoPaterno.hashCode());
        hash = 31 * hash + (null == apellidoMaterno ? 0 : apellidoMaterno.hashCode());
        hash = 31 * hash + (null == entidadFederativa ? 0 : entidadFederativa.hashCode());
        hash = 31 * hash + (null == distritoElectoral ? 0 : distritoElectoral.hashCode());
        return hash;
    }
}
