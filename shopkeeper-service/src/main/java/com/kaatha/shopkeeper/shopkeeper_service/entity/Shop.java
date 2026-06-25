package com.kaatha.shopkeeper.shopkeeper_service.entity;

import com.kaatha.shopkeeper.shopkeeper_service.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@Table(name = "shop")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Shop extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shopkeeper_id")
    private Long shopkeeperId;

    @Column(name = "shop_name")
    private String shopName;

    @Column(name = "business_type")
    private String businessType;

    @Column(name= "address_line_1")
    private String addressLineOne;

    @Column(name="address_line_2")
    private String addressLineSecond;

    @Column(name = "city")
    private String city;

    @Column(name="state")
    private String state;

    @Column(name="pincode")
    private String pinCode;

}
