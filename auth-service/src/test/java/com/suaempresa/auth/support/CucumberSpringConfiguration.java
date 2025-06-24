package com.suaempresa.auth.support;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest
public class CucumberSpringConfiguration {
    // Classe usada apenas para carregar o contexto Spring nos testes Cucumber
}
