package ru.yandex.praktikum.qascooter.services.courier;

public class CourierCredentials {

    private String login;
    private String password;

    public CourierCredentials(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public static CourierCredentials getCredentialsFrom(CourierData courier) {
        return new CourierCredentials(courier.getLogin(), courier.getPassword());
    }

    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return " ( Login: '" + login + "', Password: '" + password + "' )";
    }
}
