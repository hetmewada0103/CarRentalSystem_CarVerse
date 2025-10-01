package carverse.model;

public class SimpleHash {

    // Custom simple hash function for passwords
    public static int hash(String password) {
        int hash = 0;
        for (int i = 0; i < password.length(); i++) {
            // multiply each char by its position (i+1) and add to hash
            hash = (hash + password.charAt(i) * (i + 1)) % 100000;
        }
        return hash;
    }
}
