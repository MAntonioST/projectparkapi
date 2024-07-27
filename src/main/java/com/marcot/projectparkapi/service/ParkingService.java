package com.marcot.projectparkapi.service;

import com.marcot.projectparkapi.entity.Customer;
import com.marcot.projectparkapi.entity.CustomerParkingSpace;

import com.marcot.projectparkapi.entity.ParkingSpace;
import com.marcot.projectparkapi.util.ParkingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ParkingService {

    private final CustomerParkingSpaceService customerParkingSpaceService;
    private final CustomerService customerService;
    private final ParkingSpaceService parkingSpaceService;

    @Transactional
    public CustomerParkingSpace checkIn(CustomerParkingSpace customerParkingSpot) {
        Customer customer = customerService.findByCpf(customerParkingSpot.getCustomer().getCpf());
        customerParkingSpot.setCustomer(customer);

        ParkingSpace parkingSpace = parkingSpaceService.findFirstAvailableSpace();
        parkingSpace.setStatus(ParkingSpace.ParkingSpaceStatus.OCCUPIED);
        customerParkingSpot.setParkingSpot(parkingSpace);

        customerParkingSpot.setEntryTime(LocalDateTime.now());

        customerParkingSpot.setReceiptNumber(ParkingUtils.generateReceipt());

        return customerParkingSpaceService.save(customerParkingSpot);
    }

    @Transactional
    public CustomerParkingSpace checkOut(String receiptNumber) {
        CustomerParkingSpace customerParkingSpot = customerParkingSpaceService.findByReceiptNumber(receiptNumber);

        LocalDateTime exitTime = LocalDateTime.now();

        BigDecimal price = ParkingUtils.calculateCost(customerParkingSpot.getEntryTime(), exitTime);
        customerParkingSpot.setPrice(price);

        long totalParkingTimes = customerParkingSpaceService.getTotalParkingTimesByCustomerCpf(customerParkingSpot.getCustomer().getCpf());

        BigDecimal discount = ParkingUtils.calculateDiscount(price, totalParkingTimes);
        customerParkingSpot.setDiscount(discount);

        customerParkingSpot.setExitTime(exitTime);
        customerParkingSpot.getParkingSpot().setStatus(ParkingSpace.ParkingSpaceStatus.FREE);

        return customerParkingSpaceService.save(customerParkingSpot);
    }
}