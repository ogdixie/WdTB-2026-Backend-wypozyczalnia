package pl.wsb.wypozyczalnia.rental.entity;

import jakarta.persistence.*;
import pl.wsb.wypozyczalnia.customer.entity.Customer;
import pl.wsb.wypozyczalnia.fleet.entity.Car;
import pl.wsb.wypozyczalnia.insurance.entity.InsurancePlan;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "rental")
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RentalStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_plan_id")
    private InsurancePlan insurancePlan;

    protected Rental() {
    }

    public Rental(LocalDate startDate, LocalDate endDate, BigDecimal totalPrice, RentalStatus status,
                  Car car, Customer customer, InsurancePlan insurancePlan) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.status = status;
        this.car = car;
        this.customer = customer;
        this.insurancePlan = insurancePlan;
    }

    public Long getId() { return id; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public RentalStatus getStatus() { return status; }
    public Car getCar() { return car; }
    public Customer getCustomer() { return customer; }
    public InsurancePlan getInsurancePlan() { return insurancePlan; }
}