import database.model.TB_REPLICACAO_PROCESSO;

public class Main {
    public static void main(String[] args) {

        TB_REPLICACAO_PROCESSO tbReplicacaoProcesso = new TB_REPLICACAO_PROCESSO();
        tbReplicacaoProcesso.setId(1L);
        tbReplicacaoProcesso.setProcesso("Completo");
        tbReplicacaoProcesso.setDescricao("Processa todas as tabelas");
        tbReplicacaoProcesso.setHabilitado(true);
        System.out.println(tbReplicacaoProcesso);

    }
}