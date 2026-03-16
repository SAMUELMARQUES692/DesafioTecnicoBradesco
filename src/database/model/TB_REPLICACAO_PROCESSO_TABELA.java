package database.model;

public class TB_REPLICACAO_PROCESSO_TABELA {

    private Long id;
    private Long processo_id;
    private String tabela_origem;
    private String tabela_destino;
    private int ordem;
    private boolean habilitado;
    private String ds_where;

    public TB_REPLICACAO_PROCESSO_TABELA() {
    }

    public String getDs_where() {
        return ds_where;
    }

    public void setDs_where(String ds_where) {
        this.ds_where = ds_where;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public String getTabela_destino() {
        return tabela_destino;
    }

    public void setTabela_destino(String tabela_destino) {
        this.tabela_destino = tabela_destino;
    }

    public String getTabela_origem() {
        return tabela_origem;
    }

    public void setTabela_origem(String tabela_origem) {
        this.tabela_origem = tabela_origem;
    }

    public Long getProcesso_id() {
        return processo_id;
    }

    public void setProcesso_id(Long processo_id) {
        this.processo_id = processo_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "TB_REPLICACAO_PROCESSO_TABELA{" +
                "id=" + id +
                ", processo_id=" + processo_id +
                ", tabela_origem='" + tabela_origem + '\'' +
                ", tabela_destino='" + tabela_destino + '\'' +
                ", ordem=" + ordem +
                ", habilitado=" + habilitado +
                ", ds_where='" + ds_where + '\'' +
                '}';
    }
}
