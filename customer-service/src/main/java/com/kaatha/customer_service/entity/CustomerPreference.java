package com.kaatha.customer_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customer_preference")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "default_shopkeeper_id", nullable = false)
    private Long defaultShopkeeperId;
}
