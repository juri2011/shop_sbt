package com.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
//강의에서는 WebSecurityConfigurerAdapter를 사용하는 대신 Bean을 등록하는 방식을 사용
public class SecurityConfig{
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http.formLogin(form -> form
                .loginPage("/members/login") //로그인 페이지의 URL
                //로그인 성공 후 리다이렉션될 URL. true : 항상 리다이렉션이 수행됨
                .defaultSuccessUrl("/", true)
                //로그인 실패시 이동될 페이지
                .failureUrl("/members/login/error")
                //사용자 이름(input 필드)으로 사용될 파라미터 이름.(이메일을 사용하도록 지정됨)
                .usernameParameter("email")
                //비밀번호로 사용될 파라미터 이름
                .passwordParameter("password")
                //로그인 페이지와 관련된 요청을 모든 사용자에게 허용
                .permitAll());

        //로그아웃 URL을 /logout으로 설정하고, 리다이렉션 수행 없디 단순히 로그아웃 처리만 함
        http.logout(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
}
