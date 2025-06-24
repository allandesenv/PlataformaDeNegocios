package com.suaempresa.auth.stepdefs;

import io.cucumber.java.Before; // Adicione este import
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import com.suaempresa.auth.model.RegisterRequest;
import com.suaempresa.auth.repository.UserRepository;
import com.suaempresa.auth.model.User;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserRegistrationStepDefs {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;

    private RegisterRequest registerRequest;
    private ResponseEntity<String> response;

    // NOVO HOOK: Limpa o banco de dados antes de CADA cenário
    @Before
    public void cleanUpDatabase() {
        userRepository.deleteAll(); // Voltar para deleteAll() ou usar TRUNCATE TABLE diretamente para H2
        // Ou se preferir usar SQL nativo para limpeza super rápida no H2:
        // userRepository.getEntityManager().createNativeQuery("TRUNCATE TABLE users RESTART IDENTITY;").executeUpdate();
    }

    // REMOVA ESTE MÉTODO - A limpeza será feita no @Before hook
    // @Given("que eu limpo os dados de teste de usuário")
    // public void queEuLimpouOsDadosDeTesteDeUsuario() {
    //     userRepository.deleteAllInBatch();
    // }

    @Given("que eu estou na tela de cadastro")
    public void queEuEstouNaTelaDeCadastro() {
        // Nenhuma ação específica para a UI no backend.
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
        // Nada a fazer aqui se já passamos no 201 Created.
    }

    @Given("que o e-mail {string} já está cadastrado")
    public void queOE_mailJáEstáCadastrado(String email) {
        // Este passo agora apenas prepara o usuário, a limpeza já foi feita no @Before
        userRepository.save(User.builder()
                .email(email)
                .password("senha_hashed_qualquer")
                .googleAuth(false)
                .build());
    }

    @Then("eu devo receber uma mensagem de erro {string}")
    public void euDevoReceberUmaMensagemDeErro(String errorMessage) {
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody(), "O corpo da resposta de erro não deve ser nulo.");
        assertTrue(response.getBody().contains(errorMessage), "A mensagem de erro esperada não foi encontrada. Resposta: " + response.getBody());
    }
}