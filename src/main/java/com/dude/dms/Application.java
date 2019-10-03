package com.dude.dms;

import com.dude.dms.app.security.SecurityConfiguration;
import com.dude.dms.backend.data.entity.User;
import com.dude.dms.backend.brain.polling.DocPollingService;
import com.dude.dms.backend.repositories.UserRepository;
import com.dude.dms.backend.service.UserService;
import com.dude.dms.ui.MainView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackageClasses = { SecurityConfiguration.class, MainView.class, Application.class, UserService.class, DocPollingService.class }, exclude = ErrorMvcAutoConfiguration.class)
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
@EntityScan(basePackageClasses = User.class)
@EnableTransactionManagement
@EnableScheduling
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }

    @Bean
    public CommandLineRunner demoData(UserService userService, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userService.findByLogin("admin").isPresent()) {
                userService.create(new User("admin", passwordEncoder.encode("admin"), "admin"));
            }
        };
    }
}
