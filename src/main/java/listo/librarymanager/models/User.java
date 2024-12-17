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

    public abstract String getName();
    public abstract String getPhone();
    public abstract int getId();
}