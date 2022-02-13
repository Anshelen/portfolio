package dev.shelenkov.portfolio.web.convert;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.web.response.AccountResponse;
import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;

@Mapper
public interface AccountToAccountResponseConverter extends Converter<Account, AccountResponse> {

    @Override
    AccountResponse convert(Account account);
}
