package pl.wsb.wypozyczalnia.branch.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "branch")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 60)
    private String name;

    @Column(name = "city", nullable = false, length = 40)
    private String city;

    @Column(name = "street", nullable = false, length = 80)
    private String street;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "active", nullable = false)
    private Boolean active;

    protected Branch() {
    }

    public Branch(String name, String city, String street, String phone, Boolean active) {
        this.name = name;
        this.city = city;
        this.street = street;
        this.phone = phone;
        this.active = active;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public String getStreet() { return street; }
    public String getPhone() { return phone; }
    public Boolean getActive() { return active; }
}
