package mx.com.factico.diputinder.beans;

import java.io.Serializable;

/**
 * Created by zace3d on 4/27/15.
 */
public class Diputado implements Serializable {
    private long id;
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
    private String intereses;
    private String fiscal;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
}
