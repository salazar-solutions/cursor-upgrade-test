package com.example.app.billing.controller;

import com.example.app.billing.domain.PaymentRequest;
import com.example.app.billing.dto.PaymentResponse;
import com.example.app.billing.service.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

/**
 * REST controller for billing operations.
 */
@RestController
@RequestMapping("/api/v1/billing/payments")
@Tag(name = "Billing", description = "Billing and payment API")
public class BillingController {
    
    @Autowired
    private BillingService billingService;

    @PostMapping
    @Operation(summary = "Create a payment")
    @ApiResponse(responseCode = "201", description = "Payment created successfully")
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = billingService.createPayment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID")
    @ApiResponse(responseCode = "200", description = "Payment found")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable UUID id) {
        PaymentResponse response = billingService.getPaymentById(id);
        return ResponseEntity.ok(response);
    }
}

