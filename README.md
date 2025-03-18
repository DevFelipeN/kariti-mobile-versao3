# KARITI MOBILE
Este repositório destina-se ao código fonte da versão 3.0 do Kariti Mobile. O Kariti foi desenvolvido com o objetivo de automatizar a correção de provas objetivas utilizando visão computacional. Esse aplicativo, é uma interface criada para facilitar o contato dos usuários com o Kariti e, nesta versão o aplicativo esta totalmente autonomo sem dependência de servidor na internet.
## Funcionalidades do aplicativo:
* Cadastro de usuários - Para ter acesso ao aplicativo, primeiramente será necessário realizar o cadastro informando dados como: nome, e-mail e uma senha.
* Cadastro de Escolas - Cadastro da(s) escola(s) em que atua (apenas após o cadastro de ao menos uma escola, o usuário terá acesso as outras funcionalidades do app).
* Cadastro de Alunos - Cadastrar alunos pertencentes a escola selecionada.
* Cadastro de Turma - Cadastrar turmas pertencentes a escola selecionada e incluir alunos a essa turma (aqui o Kariti permite o cadastro de alunos anônimos - alunos identificados sequêncialmente, sem a necessidade de utilizar a funcionalidade anterior).
* Cadastro de Provas - Uma das funcionalidades principais do aplicativo, o usuário informa informações como: Nome da prova, turma, data de aplicação, quantidade de questões, quantidade de alternativas e gabarito.
* Download de Cartões - Após a prova cadastrada, será disponibilizado para download os cartões respostas dessa prova com a quantidade equivalente a quantidade de alunos pertencentes a turma selecionada.
* Correção da Prova - Apenas apontar a câmera do aplicativo para o cartão resposta preenchido, que por sua vez captura os dados do QRcode para identificar a prova, em seguida o cartão é processado e corrigido usando técnicas de processamento de imagens com suporte da biblioteca OpenCV, ao final o Kariti exibe a imagem do cartão corrigido juntamente com a nota do aluno, quantidade de acertos e erros.
* Provas corrigidas - O aplicativo exibe os detalhes de acertos e erros de cada aluno na opção "Visualizar Provas" do aplicativo.

Como melhoria desta verão com relação à versão [Versão 2](https://github.com/DevFelipeN/kariti-mobile-versao2):
* A correção que antes contava com o suporte de código em servidor na internet, tornou-se independente, e todo processamento da imagem é realizado em tempo real e no próprio app sem a necessidade de acesso a internet.
* A geração e download de cartões respostas e relatório de correção, antes realizado em servidor, passou a ser também um processo autonomo e realizado totalmente pelo app sem necessidade de conexão a internet.

## MUDANÇAS
Nesta versão do aplicativo Kariti, funcionalidades importantes como: correção, download de cartões e relatório de correção deixaram de ser dependentes de conexão a internet para serem realizadas e passaram a ser realizadas de forma autônoma pelo próprio Kariti Mobile.

## RESULTADO DA MUDANÇA

* 100% de acerto para todos os testes sintéticos
* Mais práticidade na captura dos cartões
* Melhor desempenho da aplicação
