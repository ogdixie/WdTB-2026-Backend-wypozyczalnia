package pl.wsb.wypozyczalnia.fleet.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brand", nullable = false, length = 40)
    private String brand;

    @Column(name = "model", nullable = false, length = 60)
    private String model;

    @Column(name = "plate_number", nullable = false, unique = true, length = 10)
    private String plateNumber;

    @Column(name = "horsepower", nullable = false)
    private Integer horsepower;

    @Column(name = "daily_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CarStatus status;

    protected Car() {
    }

    public Car(String brand, String model, String plateNumber, Integer horsepower,
               BigDecimal dailyPrice, CarStatus status) {
        this.brand = brand;
        this.model = model;
        this.plateNumber = plateNumber;
        this.horsepower = horsepower;
        this.dailyPrice = dailyPrice;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getPlateNumber() { return plateNumber; }
    public Integer getHorsepower() { return horsepower; }
    public BigDecimal getDailyPrice() { return dailyPrice; }
    public CarStatus getStatus() { return status; }
}