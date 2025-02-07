# KARITI MOBILE
Este repositório destina-se ao código fonte do Kariti Mobile. O Kariti foi desenvolvido com o objetivo de automatizar a correção de provas objetivas utilizando visão computacional. Esse aplicativo, é uma interface criada para facilitar o contato dos usuários com o Kariti.
## Funcionalidades do aplicativo:
* Cadastro de usuários - Para ter acesso ao aplicativo, primeiramente será necessário realizar o cadastro informando dados como: nome, e-mail e uma senha.
* Cadastro de Escolas - Cadastro da(s) escola(s) em que atua (apenas após o cadastro de ao menos uma escola, o usuário terá acesso as outras funcionalidades do app).
* Cadastro de Alunos - Cadastrar alunos pertencentes a escola selecionada.
* Cadastro de Turma - Cadastrar turmas pertencentes a escola selecionada e incluir alunos a essa turma (aqui o Kariti permite o cadastro de alunos anônimos - alunos identificados sequêncialmente, sem a necessidade de utilizar a funcionalidade anterior).
* Cadastro de Provas - Uma das funcionalidades principais do aplicativo, o usuário informa informações como: Nome da prova, turma, data de aplicação, quantidade de questões, quantidade de alternativas e gabarito.
* Download de Cartões - Após a prova cadastrada, será disponibilizado para download os cartões respostas dessa prova com a quantidade equivalente a quantidade de alunos pertencentes a turma selecionada.
* Correção da Prova - Apenas apontar a câmera do aplicativo para o cartão resposta preenchido, que por sua vez captura os dados do QRcode para identificar a prova, em seguida o cartão é processado e enviado para correção ao servidor do Kariti (Kariti online) que devolve um Json contendo os dados da correção para ser processado e mostrado no app.
* Correções - O aplicativo exibe os detalhes de acertos e erros de cada aluno na opção "Visualizar Provas" do aplicativo.

Como melhoria desta verão para a versão inicial [Versão 1](https://github.com/DevFelipeN/kariti-mobile-versao1). O processo de captura do cartão resposta que antes necessitava de fundo escuro para facilitar o corte da imagem e porteriormente a correção no servidor, processo que muitas vezes ocorriam falhas devido a qualidade da imagem. foi substituido pelo metodo apresentado nesta versão.

## MUDANÇAS
Nesta versão do aplicativo Kariti, foi incorporado ao projeto a biblioteca OpenCV, a mesma utilizada no módulo de correção online, com intuito de realizar o processamento de imagem diretamente no app, e enviar ao Kariti online apenas imagens consideradas válidas para correção e com o corte já realizado (imagem com área contento apenas os quadrados das questões e alternativas, e as marcações dos alunos).

## RESULTADO DA MUDANÇA

* 100% de acerto para todos os testes
* Mais práticidade na captura dos cartões
* Melhor desempenho da aplicação
* Mais praticidade
