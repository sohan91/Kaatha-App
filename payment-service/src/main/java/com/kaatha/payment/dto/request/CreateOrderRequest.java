package com.kaatha.payment.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {

    @NotNull
    private Long shopkeeperId;

    @NotNull
    private Long customerId;

    @NotEmpty
    private List<Long> transactionIds;

    @NotNull
    private BigDecimal amount;
}
