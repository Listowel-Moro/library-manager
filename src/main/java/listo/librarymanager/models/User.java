package listo.librarymanager.models;

public abstract class User {
    int id;
    String name,  phone;
    boolean isStaff;

    public User(int id, String name, String phone){
        this.name = name;
        this.phone = phone;
        this.id = id;
    }

    public abstract User registerUser();
    public  abstract User loginUser();
}

