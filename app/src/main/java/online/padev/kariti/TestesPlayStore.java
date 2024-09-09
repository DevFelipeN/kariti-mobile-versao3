package online.padev.kariti;

public class TestesPlayStore {
    public static void inserirDadosAltomaticos(BancoDados bancoDados){
        bancoDados.cadastrarUsuario("Master user", "user1", "karitimobile@gmail.com");
        bancoDados.inserirDadosEscola("Escola Teste 1", null, 1);
        bancoDados.inserirDadosEscola("Escola Teste 2", null, 0);
    }
}
