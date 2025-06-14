package com.example.masjidapp;

public class MosqueModel {
    private String id;
    private String name;
    private String address;
    private float rating;
    private String distance;
    private String imageUrl;
    private String description;
    private String establishedDate;
    private String chairman;

    // BARU: Tambahkan konstruktor publik kosong (no-argument constructor)
    // Ini yang dibutuhkan Firebase untuk deserialisasi data
    public MosqueModel() {
        // Anda bisa biarkan kosong
        // atau inisialisasi field dengan nilai default jika diperlukan,
        // contoh: this.name = "Nama Default";
        // tapi biasanya dibiarkan kosong karena Firebase akan mengisinya.
    }

    // Constructor lengkap (sudah ada)
    public MosqueModel(String name, String address, float rating,  String imageUrl,
                       String description, String establishedDate, String chairman) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.description = description;
        this.establishedDate = establishedDate;
        this.chairman = chairman;
    }

    // Constructor lama (jika belum pakai semua data) - ini opsional, bisa dipertahankan jika masih dipakai
    public MosqueModel(String name, String address, float rating, String distance, String imageUrl) {
        this(name, address, rating, imageUrl, "", "", ""); // Memanggil constructor lengkap
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter (sudah ada dan sudah benar)
    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public float getRating() {
        return rating;
    }

    public String getDistance() {
        return distance;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getEstablishedDate() {
        return establishedDate;
    }

    public String getChairman() {
        return chairman;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // CATATAN: Setter tidak wajib untuk deserialisasi dari Firebase,
    // tapi wajib jika Anda ingin memodifikasi objek dan menyimpannya kembali ke Firebase
    // atau jika Anda menggunakan library lain yang memerlukannya.
    // Contoh setter jika diperlukan:
    /*
    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEstablishedDate(String establishedDate) {
        this.establishedDate = establishedDate;
    }

    public void setChairman(String chairman) {
        this.chairman = chairman;
    }
    */
}