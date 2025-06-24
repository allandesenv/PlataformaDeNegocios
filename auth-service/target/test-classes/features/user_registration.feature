# language: pt
Funcionalidade: Cadastro de Usuário
  Como um novo usuário
  Desejo cadastrar uma nova conta na plataforma
  Para ter acesso aos recursos

  Cenário: Cadastro de novo usuário com e-mail e senha válidos
    Dado que eu estou na tela de cadastro
    Quando eu preencho o email "novo.usuario@example.com", a senha "SenhaForte123!" e confirmo a senha
    E eu clico em "Cadastrar"
    Então minha conta deve ser criada com sucesso
    E eu devo ser redirecionado para a tela de login

  Cenário: Tentativa de cadastro com e-mail já existente
    Dado que o e-mail "usuario.existente@example.com" já está cadastrado
    Quando eu preencho o e-mail "usuario.existente@example.com", a senha "NovaSenha123!" e confirmo a senha
    E eu clico em "Cadastrar"
    Então eu devo receber uma mensagem de erro "E-mail já cadastrado"