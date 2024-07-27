package com.marcot.projectparkapi.repository.projection;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface CustomerParkingSpaceProjection {
    String getLicensePlate();
    String getBrand();
    String getModel();
    String getColor();
    String getCustomerCpf();
    String getReceiptNumber();
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    LocalDateTime getEntryTime();
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    LocalDateTime getExitTime();
    String getParkingSpotCode();
    BigDecimal getAmount();
    BigDecimal getDiscount();
}