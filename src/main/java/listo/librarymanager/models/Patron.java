package listo.librarymanager.models;

public class Patron extends User {

    public Patron(int id, String name, String phone, boolean isStaff) {
        super(id, name, phone);
        this.isStaff = false;
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public String getPhone() {
        return this.phone;
    }
}
