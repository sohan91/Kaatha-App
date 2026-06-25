package com.kaatha.shopkeeper.shopkeeper_service.entity;


import com.kaatha.shopkeeper.shopkeeper_service.entity.base.PersonEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "shopkeepers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_shopkeeper_phone",
                        columnNames = "phone_number"
                )
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Shopkeeper extends PersonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_name", nullable = false)
    private String shopName;

    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "gst_number")
    private String gstNumber;

    @Column(name = "shop_category")
    private String shopCategory;

    @Column(name = "account_holder_name")
    private String accountHolderName;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "ifsc_code")
    private String ifscCode;

    @Column(name = "razorpay_account_id")
    private String razorpayAccountId;
}