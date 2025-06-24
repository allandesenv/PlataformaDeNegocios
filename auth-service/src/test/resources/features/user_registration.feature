# language: pt
Funcionalidade: Cadastro e Autenticação de Usuário
  Como um usuário da plataforma
  Desejo gerenciar minha conta e fazer login
  Para ter acesso aos recursos

  Cenário: Cadastro de novo usuário com e-mail e senha válidos
    Dado que eu estou na tela de cadastro
    Quando eu preencho o e-mail "novo.usuario@example.com", a senha "SenhaForte123!" e confirmo a senha
    E eu clico em "Cadastrar"
    Então minha conta deve ser criada com sucesso
    E eu devo ser redirecionado para a tela de login

  Cenário: Tentativa de cadastro com e-mail já existente
    Dado que o e-mail "usuario.existente@example.com" já está cadastrado
    Quando eu preencho o e-mail "usuario.existente@example.com", a senha "NovaSenha123!" e confirmo a senha
    E eu clico em "Cadastrar"
    Então eu devo receber uma mensagem de erro "E-mail já cadastrado"

  @login @valid_credentials
  Cenário: Login de usuário com credenciais válidas
    Dado que o e-mail "usuario.valido@example.com" e a senha "SenhaCorreta123!" estão cadastrados
    Quando eu preencho o e-mail "usuario.valido@example.com" e a senha "SenhaCorreta123!"
    E eu clico em "Entrar"
    Então eu devo ser logado com sucesso
    E eu devo ser redirecionado para o dashboard principal

  @login @invalid_credentials
  Cenário: Tentativa de login com e-mail inválido
    Dado que nenhum usuário com o e-mail "email.inexistente@example.com" está cadastrado
    Quando eu preencho o e-mail "email.inexistente@example.com" e a senha "QualquerSenha123!"
    E eu clico em "Entrar"
    Então eu devo receber uma mensagem de erro "Credenciais inválidas"

  @login @invalid_credentials
  Cenário: Tentativa de login com senha incorreta
    Dado que o e-mail "usuario.cadastrado@example.com" e a senha "SenhaCorreta123!" estão cadastrados
    Quando eu preencho o e-mail "usuario.cadastrado@example.com" e a senha "SenhaIncorreta!"
    E eu clico em "Entrar"
    Então eu devo receber uma mensagem de erro "Credenciais inválidas"

  # Cenários para Login com Google serão adicionados posteriormente