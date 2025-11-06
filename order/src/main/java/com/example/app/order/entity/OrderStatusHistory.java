package com.example.app.order.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * Order status history entity for tracking status changes.
 */
@Entity
@Table(name = "order_status_history", schema = "cursordb", indexes = {
    @Index(name = "idx_order_status_history_order_id", columnList = "order_id")
})
public class OrderStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "order_id", nullable = false, columnDefinition = "UUID")
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 20)
    private OrderStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 20)
    private OrderStatus toStatus;

    @Column(name = "timestamp", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = new Date();
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

    public OrderStatus getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(OrderStatus fromStatus) {
        this.fromStatus = fromStatus;
    }

    public OrderStatus getToStatus() {
        return toStatus;
    }

    public void setToStatus(OrderStatus toStatus) {
        this.toStatus = toStatus;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}

