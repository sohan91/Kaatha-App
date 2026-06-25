package com.kaatha.customer_service.mapper;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "shop_customer_mapping",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_shop_customer",
                        columnNames = {"shopkeeper_id", "customer_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopCustomerMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shopkeeper_id", nullable = false)
    private Long shopkeeperId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Builder.Default
    private Boolean active = true;
}