package org.example.demoescqrsaxon.query.services;

import org.example.demoescqrsaxon.commonapi.queries.GetAccountQuery;
import org.example.demoescqrsaxon.commonapi.queries.GetAllAccountsQuery;
import org.example.demoescqrsaxon.query.entities.Account;
import org.example.demoescqrsaxon.query.repositories.AccountRepository;
import org.example.demoescqrsaxon.query.repositories.OperationRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountQueryService {
    private AccountRepository accountRepository;
    private OperationRepository operationRepository;

    public AccountQueryService(AccountRepository accountRepository, OperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }

    @QueryHandler
    public List<Account> on(GetAllAccountsQuery query) {
        return accountRepository.findAll();
    }

    @QueryHandler
    public Account on(GetAccountQuery query) {
        return accountRepository.findById(query.getId()).orElse(null);
    }
}
