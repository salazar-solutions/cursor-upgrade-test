package com.example.app.order.adapter;

/**
 * Re-export BillingAdapter from billing module for convenience.
 * The actual interface is in billing module.
 * This is a type alias to avoid circular dependencies.
 */
public interface BillingAdapter extends com.example.app.billing.adapter.BillingAdapter {
    // Interface extends billing module's BillingAdapter
}

