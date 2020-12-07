package dev.shelenkov.portfolio.web.controller;

import dev.shelenkov.portfolio.domain.Account;
import dev.shelenkov.portfolio.domain.Role;
import dev.shelenkov.portfolio.service.account.AccountService;
import dev.shelenkov.portfolio.support.ConfiguredWebMvcTest;
import dev.shelenkov.portfolio.support.WithAdmin;
import dev.shelenkov.portfolio.support.WithUser;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ConfiguredWebMvcTest(AdminController.class)
public class AdminControllerTests {

    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Value("classpath:admin/all_users_response.json")
    private Resource allUsersResource;

    @Test
    public void getAllUsers_anonymousUser_redirectToLoginPage() throws Exception {
        mockMvc.perform(get("/admin/users"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("http://localhost/login"));
    }

    @WithUser
    @Test
    public void getAllUsers_userWithoutRights_forbidden() throws Exception {
        mockMvc.perform(get("/admin/users"))
            .andExpect(status().isForbidden());
    }

    @WithAdmin
    @Test
    public void getAllUsers_admin_expectedResponse() throws Exception {
        expectRepositoryReturnsUsersPage();

        String expectedJSON = FileUtils.readFileToString(allUsersResource.getFile(), UTF_8);
        mockMvc.perform(
            get("/admin/users")
                .queryParam("page", "0")
                .queryParam("size", "2"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(expectedJSON));
    }

    private void expectRepositoryReturnsUsersPage() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("id"));

        Account account1 = createAccount(
            1L, "username1", "email1", "password1",
            EnumSet.of(Role.USER), false);
        Account account2 = createAccount(
            2L, "username2", "email2", "password2",
            EnumSet.of(Role.ADMIN, Role.USER), true);
        Page<Account> page = new PageImpl<>(Arrays.asList(account1, account2), pageable, 5);

        when(accountService.findAll(eq(pageable))).thenReturn(page);
    }

    private Account createAccount(long id, String userName, String email, String password,
                                  Collection<Role> roles, boolean enabled) {

        Account account = new Account(userName, email, password, roles);
        account.setEnabled(enabled);
        ReflectionTestUtils.setField(account, "id", id);
        return account;
    }
}
