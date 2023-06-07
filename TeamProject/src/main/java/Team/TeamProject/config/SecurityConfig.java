package Team.TeamProject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/sign/sign-in")
                .defaultSuccessUrl("/board/list")
                .usernameParameter("id")
                .passwordParameter("password")
                .failureUrl("/sign/sign-in/error")
                .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/sign/logout"))
//                .logoutSuccessUrl("/")
                .and();

        http.authorizeRequests()
                .mvcMatchers("/css/**", "/js/**", "/img/**", "/assets/**").permitAll()
                .mvcMatchers("/**", "/fragments/**", "/sign/**").permitAll()
                .mvcMatchers("/css/**", "/js/**", "/img/**", "/assets/**", "/summernote/**", "/summernote_image/**", "/board/review/view").permitAll()
                .mvcMatchers("/", "/fragments/**", "/sign/**", "/board/list/**", "/board/detail/**").permitAll()
                .mvcMatchers("/item/**").hasAnyRole("ADMIN", "USER")
                .anyRequest().authenticated();

        http.exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint());

        http.csrf().disable().cors().disable();

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
