package listo.librarymanager.models;


public class Staff extends User {

    public Staff(int id, String name, String phone, boolean isStaff) {
        super(id, name, phone);
        this.isStaff = true;
    }

    public String getName() {
        return this.name;
    }

    public String getPhone() {
        return this.phone;
    }

    public int getId (){
        return this.id;
    }
}
