package me.jujin.demoinflearnrestapi.configs;

import me.jujin.demoinflearnrestapi.accounts.Account;
import me.jujin.demoinflearnrestapi.accounts.AccountRole;
import me.jujin.demoinflearnrestapi.accounts.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Set;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper( ){
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

//    @Bean
//    public ApplicationRunner applicationRunner(){
//        return new ApplicationRunner() {
//            @Autowired
//            AccountService accountService;
//
//            @Override
//            public void run(ApplicationArguments args) throws Exception {
//                Account jujin = Account.builder()
//                        .email("jujin@email.com")
//                        .password("jujin")
//                        .roles(Set.of(AccountRole.ADMIN,AccountRole.USER))
//                        .build();
//                accountService.saveAccount(jujin);
//
////                UsernamePasswordAuthenticationFilter ss = new
//            }
//        };
//    }
}
