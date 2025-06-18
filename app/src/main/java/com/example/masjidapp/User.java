<<<<<<< HEAD
package com.example.masjidapp;

public class User {
    public String uid;
    private String name;
    private String email;
    private String phone;

    public User() {
        // Diperlukan oleh Firebase
    }

=======
// File: User.java
package com.example.masjidapp;

public class User {

    // Properti/variabel yang dimiliki oleh seorang User
    public String uid;
    public String name;
    public String email;
    public String phone;

    // Constructor kosong, WAJIB ADA untuk Firebase
    public User() {
    }

    // Constructor untuk membuat objek baru dengan data
>>>>>>> 94f4782 (Fiksasi Fitur Berita)
    public User(String uid, String name, String email, String phone) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

<<<<<<< HEAD
    // Getter dan Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
=======
    // Getter dan Setter (opsional tapi praktik yang baik)
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
>>>>>>> 94f4782 (Fiksasi Fitur Berita)
