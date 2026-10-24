package pl.wsb.wypozyczalnia.insurance.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "insurance_plan")
public class InsurancePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 40)
    private String name;

    @Column(name = "daily_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyPrice;

    @Column(name = "deductible", nullable = false, precision = 10, scale = 2)
    private BigDecimal deductible;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "active", nullable = false)
    private Boolean active;

    protected InsurancePlan() {
    }

    public InsurancePlan(String name, BigDecimal dailyPrice, BigDecimal deductible,
                         String description, Boolean active) {
        this.name = name;
        this.dailyPrice = dailyPrice;
        this.deductible = deductible;
        this.description = description;
        this.active = active;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getDailyPrice() { return dailyPrice; }
    public BigDecimal getDeductible() { return deductible; }
    public String getDescription() { return description; }
    public Boolean getActive() { return active; }
}