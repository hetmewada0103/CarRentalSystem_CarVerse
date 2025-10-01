package carverse.model;

import java.sql.Date;

public class CustomerNode {
    // Customer details
    public int id;
    public String name;
    public String email;
    public String phone;
    public String address;
    public Date dob;
    public CustomerNode next; // Pointer to the next node in the linked list

    // Constructor to initialize a customer node
    public CustomerNode(int id, String name, String email, String phone, String address, Date dob) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.dob = dob;
        this.next = null; // initially no next node
    }
}
