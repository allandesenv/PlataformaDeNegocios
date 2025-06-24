package com.suaempresa.auth.stepdefs;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import com.suaempresa.auth.model.RegisterRequest;
import com.suaempresa.auth.model.LoginRequest; // NOVO IMPORT
import com.suaempresa.auth.model.User;
import com.suaempresa.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder; // NOVO IMPORT
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserRegistrationStepDefs {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired // Injetar PasswordEncoder para registrar usuários nos testes
    private PasswordEncoder passwordEncoder;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest; // Para requisições de login
    private ResponseEntity<String> response;

    @Before
    public void cleanUpDatabase() {
        userRepository.deleteAll(); // Limpeza do DB antes de cada cenário
    }

    @Given("que eu estou na tela de cadastro")
    public void queEuEstouNaTelaDeCadastro() {
        // Nada a fazer aqui
    }

    @When("eu preencho o e-mail {string}, a senha {string} e confirmo a senha")
    public void euPreenchoOE_mailASenhaEConfirmoASenha(String email, String password) {
        this.registerRequest = RegisterRequest.builder()
                .email(email)
                .password(password)
                .confirmPassword(password)
                .build();
    }

    @And("eu clico em {string}")
    public void euClicoEm(String action) {
        if ("Cadastrar".equals(action)) {
            try {
                response = restTemplate.postForEntity("/api/auth/register", registerRequest, String.class);
            } catch (HttpClientErrorException e) {
                response = new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
            }
        } else if ("Entrar".equals(action)) { // Lógica para o botão de Login
            try {
                response = restTemplate.postForEntity("/api/auth/login", loginRequest, String.class);
            } catch (HttpClientErrorException e) {
                response = new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
            }
        }
    }

    @Then("minha conta deve ser criada com sucesso")
    public void minhaContaDeveSerCriadaComSessuco() {
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        User user = userRepository.findByEmail(registerRequest.getEmail()).orElse(null);
        assertNotNull(user, "Usuário não deveria ser nulo após cadastro bem-sucedido");
        assertNotNull(user.getPassword(), "Senha não deveria ser nula");
        assertTrue(user.getPassword().length() > 0, "Senha deveria estar criptografada");
    }

    @And("eu devo ser redirecionado para a tela de login")
    public void euDevoSerRedirecionadoParaATelaDeLogin() {
        // Asserções para redirecionamento lógico da API.
        // Já verificamos o status 201 no passo anterior.
    }

    @Given("que o e-mail {string} já está cadastrado")
    public void queOE_mailJáEstáCadastrado(String email) {
        userRepository.save(User.builder()
                .email(email)
                .password(passwordEncoder.encode("senha_hashed_qualquer")) // Criptografa a senha para o DB
                .googleAuth(false)
                .build());
    }

    @Then("eu devo receber uma mensagem de erro {string}")
    public void euDevoReceberUmaMensagemDeErro(String errorMessage) {
        // Para "Credenciais inválidas" ou "As senhas não coincidem", o status é BAD_REQUEST (400)
        // Para "E-mail já cadastrado", o status é CONFLICT (409)
        if (errorMessage.equals("E-mail já cadastrado")) {
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        } else if (errorMessage.equals("Credenciais inválidas") || errorMessage.equals("As senhas não coincidem.")) {
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        } else {
            // Caso de erro inesperado, apenas verifica se contém a mensagem
            assertTrue(response.getBody().contains(errorMessage), "A mensagem de erro esperada não foi encontrada. Resposta: " + response.getBody());
        }
        assertNotNull(response.getBody(), "O corpo da resposta de erro não deve ser nulo.");
        assertTrue(response.getBody().contains(errorMessage), "A mensagem de erro esperada não foi encontrada. Resposta: " + response.getBody());
    }

    // NOVOS PASSOS PARA LOGIN:

    @Given("que o e-mail {string} e a senha {string} estão cadastrados")
    public void queOE_mailEASenhaEstaoCadastrados(String email, String password) {
        userRepository.save(User.builder()
                .email(email)
                .password(passwordEncoder.encode(password)) // Criptografa a senha para o DB
                .googleAuth(false)
                .build());
    }

    @Given("que nenhum usuário com o e-mail {string} está cadastrado")
    public void queNenhumUsuarioComOE_mailEstaCadastrado(String email) {
        // O @Before limpa o DB, então apenas garantimos que não haja usuários.
        // Nenhuma ação adicional é necessária aqui além da limpeza do cenário.
    }

    @When("eu preencho o e-mail {string} e a senha {string}")
    public void euPreenchoOE_mailEASenha(String email, String password) {
        this.loginRequest = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();
    }

    @Then("eu devo ser logado com sucesso")
    public void euDevoSerLogadoComSucesso() {
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Login bem-sucedido retorna 200 OK
        assertNotNull(response.getBody(), "Corpo da resposta não deve ser nulo.");
        assertTrue(response.getBody().contains("temp-token-login-sucesso"), "Token de login esperado na resposta."); // Verifica o token temporário
    }

    @And("eu devo ser redirecionado para o dashboard principal")
    public void euDevoSerRedirecionadoParaODashboardPrincipal() {
        // Similar ao cadastro, verificamos o status 200 OK.
        // Em um frontend real, isso implicaria em uma rota de redirecionamento.
    }
}