package dev.shelenkov.portfolio.web.controller;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.service.account.AccountService;
import dev.shelenkov.portfolio.web.response.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("admin/")
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AccountService accountService;
    private final ConversionService conversionService;

    @GetMapping("users")
    public Page<AccountResponse> getAllUsers(@PageableDefault(sort = "id") Pageable pageable) {
        Page<Account> page = accountService.findAll(pageable);
        List<AccountResponse> content = page.getContent().stream()
            .map(account -> conversionService.convert(account, AccountResponse.class))
            .collect(Collectors.toList());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }
}
