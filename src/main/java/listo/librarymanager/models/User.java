package listo.librarymanager.models;

public abstract class User {
    String name,  phone, password;
    boolean isStaff;

    public User(String name, String phone, String password){
        this.name = name;
        this.phone = phone;
    }

    public abstract User registerUser();
    public  abstract User loginUser();
}

