package org.example.demoescqrsaxon.commonapi.events;

import lombok.Getter;

public class AccountCreatedEvent extends BaseEvent<String> {
    @Getter
    private double initialBalance;
    @Getter
    private String currency;
    @Getter
    private String status;

    public AccountCreatedEvent(String id, double initialBalance, String currency, String status) {
        super(id);
        this.initialBalance = initialBalance;
        this.currency = currency;
        this.status = status;
    }
}
