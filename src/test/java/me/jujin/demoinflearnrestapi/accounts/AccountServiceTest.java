package me.jujin.demoinflearnrestapi.accounts;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Test
    public void findByUsername(){
        String username= "jujin@email.com";
        String password ="jujin";
        Account account= Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN,AccountRole.USER))
                .build();

        accountRepository.save(account);

        UserDetailsService userDetailsService=(UserDetailsService)accountService;
        UserDetails userDetails=userDetailsService.loadUserByUsername(username);

        assertEquals(userDetails.getPassword(),password);
      }

      @Test
    public void findByUsernameFail(){
        //expected
        String username= "sssdf";
        expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage(Matchers.containsString(username));


        //when
         accountService.loadUserByUsername(username);


      }

}