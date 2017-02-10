package com.nextdots.marvelcomics.rest.entity;

/**
 * Created by solerambp01 on 27/10/16.
 */



import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Usuario {


    @SerializedName("idUsuario")
    @Expose
    private Integer idUsuario;
    @SerializedName("nombreCompleto")
    @Expose
    private String nombreCompleto;
    @SerializedName("tipoDocumento")
    @Expose
    private String tipoDocumento;
    @SerializedName("nroDocumento")
    @Expose
    private String nroDocumento;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("flagNotificaciones")
    @Expose
    private String flagNotificaciones;
    @SerializedName("fbId")
    @Expose
    private String fbId;
    @SerializedName("googleId")
    @Expose
    private String googleId;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("activacion")
    @Expose
    private String activacion;

    /**
     * @return The idUsuario
     */
    public Integer getIdUsuario() {
        return idUsuario;
    }

    /**
     * @param idUsuario The idUsuario
     */
    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * @return The nombreCompleto
     */
    public String getNombreCompleto() {
        return nombreCompleto;
    }

    /**
     * @param nombreCompleto The nombreCompleto
     */
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    /**
     * @return The tipoDocumento
     */
    public String getTipoDocumento() {
        return tipoDocumento;
    }

    /**
     * @param tipoDocumento The tipoDocumento
     */
    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    /**
     * @return The nroDocumento
     */
    public String getNroDocumento() {
        return nroDocumento;
    }

    /**
     * @param nroDocumento The nroDocumento
     */
    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
    }

    /**
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return The password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password The password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return The flagNotificaciones
     */
    public String getFlagNotificaciones() {
        return flagNotificaciones;
    }

    /**
     * @param flagNotificaciones The flagNotificaciones
     */
    public void setFlagNotificaciones(String flagNotificaciones) {
        this.flagNotificaciones = flagNotificaciones;
    }

    /**
     * @return The fbId
     */
    public String getFbId() {
        return fbId;
    }

    /**
     * @param fbId The fbId
     */
    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    /**
     * @return The googleId
     */
    public String getGoogleId() {
        return googleId;
    }

    /**
     * @param googleId The googleId
     */
    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    /**
     * @return The token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token The token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return The activacion
     */
    public String getActivacion() {
        return activacion;
    }

    /**
     * @param activacion The activacion
     */
    public void setActivacion(String activacion) {
        this.activacion = activacion;
    }

}
