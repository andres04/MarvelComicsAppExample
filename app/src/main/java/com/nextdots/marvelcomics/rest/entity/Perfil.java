package com.nextdots.marvelcomics.rest.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by solerambp01 on 21/08/16.
 */
public class Perfil implements Parcelable {

    private String id;
    private String nombre;
    private String correo;
    private String img;
    private String telefono;
    private String password;
    private int tipoSocialNetwork;
    private boolean isSocialNetwork;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTipoSocialNetwork() {
        return tipoSocialNetwork;
    }

    public void setTipoSocialNetwork(int tipoSocialNetwork) {
        this.tipoSocialNetwork = tipoSocialNetwork;
    }

    public boolean isSocialNetwork() {
        return isSocialNetwork;
    }

    public void setSocialNetwork(boolean socialNetwork) {
        isSocialNetwork = socialNetwork;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.nombre);
        dest.writeString(this.correo);
        dest.writeString(this.img);
        dest.writeString(this.telefono);
        dest.writeString(this.password);
        dest.writeInt(this.tipoSocialNetwork);
        dest.writeByte(this.isSocialNetwork ? (byte) 1 : (byte) 0);
    }

    public Perfil() {
    }

    protected Perfil(Parcel in) {
        this.id = in.readString();
        this.nombre = in.readString();
        this.correo = in.readString();
        this.img = in.readString();
        this.telefono = in.readString();
        this.password = in.readString();
        this.tipoSocialNetwork = in.readInt();
        this.isSocialNetwork = in.readByte() != 0;
    }

    public static final Creator<Perfil> CREATOR = new Creator<Perfil>() {
        @Override
        public Perfil createFromParcel(Parcel source) {
            return new Perfil(source);
        }

        @Override
        public Perfil[] newArray(int size) {
            return new Perfil[size];
        }
    };
}
