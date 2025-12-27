package org.example.demoescqrsaxon.commands.aggregates;


import org.example.demoescqrsaxon.commonapi.commands.CreateAccountCommand;
import org.example.demoescqrsaxon.commonapi.commands.CreditAccountCommand;
import org.example.demoescqrsaxon.commonapi.commands.DebitAccountCommand;
import org.example.demoescqrsaxon.commonapi.enums.AccountStatus;
import org.example.demoescqrsaxon.commonapi.events.AccountActivatedEvent;
import org.example.demoescqrsaxon.commonapi.events.AccountCreatedEvent;
import org.example.demoescqrsaxon.commonapi.events.AccountCreditedEvent;
import org.example.demoescqrsaxon.commonapi.events.AccountDebitedEvent;

import org.axonframework.spring.stereotype.Aggregate;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
@Aggregate
public class AccountAggregate {
    @AggregateIdentifier
    private String accountId;
    private double balance;
    private String currency;
    private AccountStatus status;

    public AccountAggregate() {
    }

    @CommandHandler
    public AccountAggregate(CreateAccountCommand command) {
        if (command.getInitialBalance() < 0) throw new RuntimeException("Initial Balance cannot be negative");
        AggregateLifecycle.apply(new AccountCreatedEvent(
                command.getId(),
                command.getInitialBalance(),
                command.getCurrency(),
                AccountStatus.CREATED.toString()));
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        this.accountId = event.getId();
        this.balance = event.getInitialBalance();
        this.currency = event.getCurrency();
        this.status = AccountStatus.valueOf(event.getStatus());
        AggregateLifecycle.apply(new AccountActivatedEvent(
                event.getId(),
                AccountStatus.ACTIVATED.toString()));
    }

    @EventSourcingHandler
    public void on(AccountActivatedEvent event) {
        this.status = AccountStatus.valueOf(event.getStatus());
    }

    @CommandHandler
    public void handle(CreditAccountCommand command) {
        if (command.getAmount() < 0) throw new RuntimeException("Amount cannot be negative");
        AggregateLifecycle.apply(new AccountCreditedEvent(
                command.getId(),
                command.getAmount(),
                command.getCurrency()));
    }

    @EventSourcingHandler
    public void on(AccountCreditedEvent event) {
        this.balance += event.getAmount();
    }

    @CommandHandler
    public void handle(DebitAccountCommand command) {
        if (command.getAmount() < 0) throw new RuntimeException("Amount cannot be negative");
        if (this.balance < command.getAmount()) throw new RuntimeException("Insufficient Balance");
        AggregateLifecycle.apply(new AccountDebitedEvent(
                command.getId(),
                command.getAmount(),
                command.getCurrency()));
    }

    @EventSourcingHandler
    public void on(AccountDebitedEvent event) {
        this.balance -= event.getAmount();
    }
}
