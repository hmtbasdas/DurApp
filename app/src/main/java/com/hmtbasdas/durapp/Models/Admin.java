package com.hmtbasdas.durapp.Models;

public class Admin {
    private String id, mail, name, password, surname;

    public Admin() {
    }

    public Admin(String id, String mail, String name, String password, String surname) {
        this.id = id;
        this.mail = mail;
        this.name = name;
        this.password = password;
        this.surname = surname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
