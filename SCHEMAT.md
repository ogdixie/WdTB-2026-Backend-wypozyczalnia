# Schemat bazy danych

Plik generują reguły projektu (panel Maven → Lifecycle → verify). Nie edytujcie go ręcznie.

Tabele: 6 · kolumny: 43 · relacje: 4 × @ManyToOne, 1 × @OneToMany, 1 × @OneToOne, 0 × @ManyToMany

| Tabela | Encja | Kolumny | Klucze obce |
|---|---|---|---|
| branch | Branch | 6 | — |
| car | Car | 8 | branch_id |
| customer | Customer | 10 | user_account_id |
| insurance_plan | InsurancePlan | 6 | — |
| rental | Rental | 8 | car_id, customer_id, insurance_plan_id |
| user_account | UserAccount | 5 | — |

```mermaid
erDiagram
    branch {
        Long id PK
        Boolean active
        String city
        String name
        String phone
        String street
    }
    car {
        Long id PK
        Long branch_id FK
        String brand
        BigDecimal daily_price
        Integer horsepower
        String model
        String plate_number
        CarStatus status
    }
    customer {
        Long id PK
        String city
        String driving_license_number
        String email
        String full_name
        String phone
        String postal_code
        LocalDate registered_on
        String street
        Long user_account_id FK
    }
    insurance_plan {
        Long id PK
        Boolean active
        BigDecimal daily_price
        BigDecimal deductible
        String description
        String name
    }
    rental {
        Long id PK
        Long car_id FK
        Long customer_id FK
        LocalDate end_date
        Long insurance_plan_id FK
        LocalDate start_date
        RentalStatus status
        BigDecimal total_price
    }
    user_account {
        Long id PK
        LocalDateTime created_at
        String display_name
        String email
        AccountRole role
    }
    branch ||--o{ car : "branch_id"
    car ||--o{ rental : "car_id"
    customer ||--o{ rental : "customer_id"
    insurance_plan |o--o{ rental : "insurance_plan_id"
    user_account |o--o| customer : "user_account_id"
```
