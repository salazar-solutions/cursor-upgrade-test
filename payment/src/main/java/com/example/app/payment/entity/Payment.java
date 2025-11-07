package com.example.app.payment.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Payment entity for persistence.
 */
@Entity
@Table(name = "payments", schema = "cursordb", indexes = {
    @Index(name = "idx_payment_order_id", columnList = "order_id"),
    @Index(name = "idx_payment_status", columnList = "status")
})
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "order_id", nullable = false, columnDefinition = "UUID")
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "provider_ref", length = 100)
    private String providerRef;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getProviderRef() {
        return providerRef;
    }

    public void setProviderRef(String providerRef) {
        this.providerRef = providerRef;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

