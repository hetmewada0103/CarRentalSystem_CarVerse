package carverse.model;

import java.sql.Date;

public class CustomerList {
    private CustomerNode head; // first node in the list
    private int size = 0;      // total number of customers

    // Insert a new customer at the end of the list
    public void insertCustomer(int customer_id, String name, String email, String phone_no, String address, Date dob) {
        CustomerNode newNode = new CustomerNode(customer_id, name, email, phone_no, address, dob);
        if (head == null) {
            head = newNode;
        } else {
            CustomerNode temp = head;
            while (temp.next != null){
                temp = temp.next;
                temp.next = newNode;
            }
        }
        size++;
    }

    // Clear the list (remove all customers)
    public void clear() {
        head = null;
        size = 0;
    }

    // Check if the list is empty
    public boolean isEmpty() {
        return size == 0;
    }

    // Return the first node (head)
    public CustomerNode head() {
        return head;
    }

    // Return number of customers
    public int size() {
        return size;
    }
}
