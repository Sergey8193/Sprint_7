package ru.yandex.praktikum.qascooter.services.courier;

public class CourierData extends CourierCredentials {
    private String firstName;

    public CourierData(String login, String password, String firstName) {
        super(login, password);
        this.firstName = firstName;
    }

    public String getLogin() { return super.getLogin(); }
    public void setLogin(String login) { super.setLogin(login); }

    public String getPassword() {
        return super.getPassword();
    }
    public void setPassword(String password) {
        super.setPassword(password);
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String toString() {
        return " ( Login: '" + getLogin() + "', Password: '" + getPassword() + "', FirstName: '" + firstName + "' )";
    }
}
