package org.example.demoescqrsaxon.query.services;

import org.example.demoescqrsaxon.commonapi.enums.AccountStatus;
import org.example.demoescqrsaxon.commonapi.enums.OperationType;
import org.example.demoescqrsaxon.commonapi.events.AccountActivatedEvent;
import org.example.demoescqrsaxon.commonapi.events.AccountCreatedEvent;
import org.example.demoescqrsaxon.commonapi.events.AccountCreditedEvent;
import org.example.demoescqrsaxon.commonapi.events.AccountDebitedEvent;
import org.example.demoescqrsaxon.query.entities.Account;
import org.example.demoescqrsaxon.query.entities.AccountOperation;
import org.example.demoescqrsaxon.query.repositories.AccountRepository;
import org.example.demoescqrsaxon.query.repositories.OperationRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
@Slf4j
public class AccountServiceHandler {
    private AccountRepository accountRepository;
    private OperationRepository operationRepository;
    private org.axonframework.queryhandling.QueryUpdateEmitter queryUpdateEmitter;

    public AccountServiceHandler(AccountRepository accountRepository, OperationRepository operationRepository, org.axonframework.queryhandling.QueryUpdateEmitter queryUpdateEmitter) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
        this.queryUpdateEmitter = queryUpdateEmitter;
    }

    @EventHandler
    public void on(AccountCreatedEvent event) {
        log.info("AccountCreatedEvent received");
        Account account = Account.builder()
                .id(event.getId())
                .balance(event.getInitialBalance())
                .currency(event.getCurrency())
                .status(AccountStatus.valueOf(event.getStatus()))
                .build();
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountActivatedEvent event) {
        log.info("AccountActivatedEvent received");
        Account account = accountRepository.findById(event.getId()).orElse(null);
        if (account != null) {
            account.setStatus(AccountStatus.valueOf(event.getStatus()));
            accountRepository.save(account);
        }
    }

    @EventHandler
    public void on(AccountCreditedEvent event) {
        log.info("AccountCreditedEvent received");
        Account account = accountRepository.findById(event.getId()).orElse(null);
        if (account != null) {
            account.setBalance(account.getBalance() + event.getAmount());
            accountRepository.save(account);
            AccountOperation operation = AccountOperation.builder()
                    .amount(event.getAmount())
                    .operationDate(new Date()) // Should ideally come from event metadata
                    .type(OperationType.CREDIT)
                    .account(account)
                    .build();
            operationRepository.save(operation);
            queryUpdateEmitter.emit(m -> ((org.example.demoescqrsaxon.commonapi.queries.GetAccountQuery) m.getPayload()).getId().equals(event.getId()), account);
        }
    }

    @EventHandler
    public void on(AccountDebitedEvent event) {
        log.info("AccountDebitedEvent received");
        Account account = accountRepository.findById(event.getId()).orElse(null);
        if (account != null) {
            account.setBalance(account.getBalance() - event.getAmount());
            accountRepository.save(account);
            AccountOperation operation = AccountOperation.builder()
                    .amount(event.getAmount())
                    .operationDate(new Date())
                    .type(OperationType.DEBIT)
                    .account(account)
                    .build();
            operationRepository.save(operation);
            queryUpdateEmitter.emit(m -> ((org.example.demoescqrsaxon.commonapi.queries.GetAccountQuery) m.getPayload()).getId().equals(event.getId()), account);
        }
    }
}
