package com.dude.dms;

import com.dude.dms.backend.data.DataEntity;
import com.dude.dms.backend.repositories.DocRepository;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.brain.SpringContext;
import com.dude.dms.ui.EditMode;
import com.dude.dms.ui.views.MainView;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackageClasses = { MainView.class, Application.class, EditMode.class, DocService.class, SpringContext.class }, exclude = ErrorMvcAutoConfiguration.class)
@EnableJpaRepositories(basePackageClasses = DocRepository.class)
@EntityScan(basePackageClasses = DataEntity.class)
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }
}