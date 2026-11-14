package pl.wsb.wypozyczalnia.customer.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import pl.wsb.wypozyczalnia.rental.entity.Rental;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 80)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "driving_license_number", nullable = false, unique = true, length = 20)
    private String drivingLicenseNumber;

    @Column(name = "registered_on", nullable = false)
    private LocalDate registeredOn;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "customer")
    private List<Rental> rentals = new ArrayList<>();

    protected Customer() {
    }

    public Customer(String fullName, String email, String phone, String drivingLicenseNumber,
                    LocalDate registeredOn, Address address) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.drivingLicenseNumber = drivingLicenseNumber;
        this.registeredOn = registeredOn;
        this.address = address;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getDrivingLicenseNumber() { return drivingLicenseNumber; }
    public LocalDate getRegisteredOn() { return registeredOn; }
    public Address getAddress() { return address; }
    public List<Rental> getRentals() { return rentals; }
}