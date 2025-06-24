package com.suaempresa.auth.stepdefs;

import com.suaempresa.auth.AuthApplication;
import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = AuthApplication.class)
@ContextConfiguration(classes = AuthApplication.class)
@ActiveProfiles("test")
public class CucumberSpringConfiguration {

    @Before
    public void setupScenario() {
        // ...
    }
}