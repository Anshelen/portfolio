package dev.shelenkov.portfolio.web.controller;

import dev.shelenkov.portfolio.model.Account;
import dev.shelenkov.portfolio.repository.AccountRepository;
import dev.shelenkov.portfolio.web.wrappers.view.AccountView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("admin/")
@RestController
public class AdminController {

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("users")
    public Page<AccountView> getAllUsers(Pageable pageable) {
        Page<Account> page = accountRepository.findAll(pageable);
        List<AccountView> content = page.getContent().stream()
            .map(AccountView::new)
            .collect(Collectors.toList());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }
}
