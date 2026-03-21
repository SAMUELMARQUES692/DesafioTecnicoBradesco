package service;

import database.dao.DirecaoDAO;
import database.dao.OrigemDAO;
import database.dao.ProcessoTabelaDAO;
import database.dao.ReplicacaoProcessoDAO;
import database.model.TB_REPLICACAO_DIRECAO;
import database.model.TB_REPLICACAO_PROCESSO;
import database.model.TB_REPLICACAO_PROCESSO_TABELA;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReplicacaoExecutar {

    private Connection connControle;
    private Connection connOrigem;
    private Connection connDestino;


    public ReplicacaoExecutar(Connection connControle) {
        this.connControle = connControle;
        System.out.println("Replicação inciada, Acompanhe pelo console ou log");
        replicacaoIniciar();
        replicacaoFinalizada();
        System.out.println("Replicação finalizada,Aguardando inicio da proxima replicação");
    }

    private void replicacaoIniciar() {

        ReplicacaoProcessoDAO processo = null;

        try {
            processo = new ReplicacaoProcessoDAO(connControle);

            ArrayList<TB_REPLICACAO_PROCESSO> arlProcessos = processo.selectAll();
            if (arlProcessos != null && !arlProcessos.isEmpty()) {
                for (TB_REPLICACAO_PROCESSO p : arlProcessos) {
                    if (p != null && p.isHabilitado()) {

                        DirecaoDAO direcao = new DirecaoDAO(connControle);
                        ArrayList<TB_REPLICACAO_DIRECAO> arlDirecao = direcao.selectByProcessoHabilitado(p.getId());

                        for (TB_REPLICACAO_DIRECAO d : arlDirecao) {
                            if (d != null && d.isHabilitado()) {
                                connOrigem = DriverManager.getConnection(d.getDirecao_origem(), d.getUsuario_origem(), d.getSenha_origem());
                                if (connOrigem == null) {
                                    System.out.println("Falha ao conectar ao banco origem");
                                    continue;
                                }

                                connDestino = DriverManager.getConnection(d.getDirecao_destino(), d.getUsuario_destino(), d.getSenha_destino());
                                if (connDestino == null) {
                                    System.out.println("Falha ao conectar ao banco destino");
                                    continue;
                                }

                                ProcessoTabelaDAO tabela = new ProcessoTabelaDAO(connControle);
                                OrigemDAO daoOrigem = new OrigemDAO(connOrigem);

                                ArrayList<TB_REPLICACAO_PROCESSO_TABELA> arlTabelas = tabela.selectByProcessoHabilitado(p.getId());
                                for (TB_REPLICACAO_PROCESSO_TABELA t : arlTabelas) {
                                    if (t != null && t.isHabilitado()) {
                                        System.out.println("Origem: " + d.getDirecao_origem() + "<--->"+ d.getDirecao_destino() + "- Tabela: " + t.getTabela_origem());
                                        ResultSet resultado = daoOrigem.selectComandoOrigem(t.getTabela_origem(), t.getDs_where());
                                        if (resultado != null) {
                                            ResultSetMetaData metaData = resultado.getMetaData();
                                            int ln_columns = metaData.getColumnCount();
                                            String insertSql = insertGet(t.getTabela_destino(), metaData);

                                            connDestino.setAutoCommit(false);
                                            try(PreparedStatement pstInsert = connDestino.prepareStatement(insertSql)) {
                                                while (resultado.next()) {
                                                    for (int i = 1; i <= ln_columns; i++) {
                                                        pstInsert.setObject(i, resultado.getObject(i));
                                                    }
                                                    pstInsert.addBatch();
                                                }
                                                pstInsert.executeBatch();
                                                System.out.println("Dados replicados com sucesso!");
                                                connDestino.commit();
                                            }catch (Exception exception) {
                                                System.out.println("DEU PRIMARY KEY");
                                            }
                                            finally {
                                                connDestino.setAutoCommit(true);
                                                resultado.close();
                                            }

                                        }
                                    }else {
                                        System.out.println("Nenhuma tabela habilitada para replicar...");
                                    }
                                }

                            }else {
                                System.out.println("Nenhuma direção habilitada para replicar...");
                            }
                        }

                    }else{
                        System.out.println("Nenhum processo habilitado...");
                    }
                }
            }else {
                System.out.println("Nenhum processo encontrado...");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String insertGet(String tabelaDestino, ResultSetMetaData metaData) throws SQLException {

        String ls_column = "", ls_value = "";

        for (int ln_1 = 0; ln_1 < metaData.getColumnCount(); ls_column += metaData.getColumnName(++ln_1) + " ," + "\n", ls_value += " ? ," + "\n");

        return "insert into " + tabelaDestino + " (" + ls_column.substring(0, ls_column.length()-2) + ") values (" + ls_value.substring(0, ls_value.length()-2)+")";

    }

    private void replicacaoFinalizada() {

        try {
            String sqlFila =
                    "SELECT id, table_name, row_id, operation " +
                            "FROM replication_queue " +
                            "WHERE processed_at IS NULL " +
                            "ORDER BY occurred_at";

            String sqlMark =
                    "UPDATE replication_queue SET processed_at = NOW() WHERE id=?";

            ResultSet rsFila = null;
            PreparedStatement psFila = null;
            PreparedStatement pstMark = null;

            try {
                psFila = connOrigem.prepareStatement(sqlFila);
                rsFila = psFila.executeQuery();

                pstMark = connOrigem.prepareStatement(sqlMark);

                while (rsFila.next()) {
                     Long queueId = rsFila.getLong("id");
                     String tableName = rsFila.getString("table_name");
                     Long rowId = rsFila.getLong("row_id");
                     String operation = rsFila.getString("operation");

                    System.out.println("Fila: tabela=" + tableName +
                            " id=" + rowId +
                            " op=" + operation);
                    if ("D".equalsIgnoreCase(operation)) {
                        String sqlDelete = "DELETE FROM " + tableName + " WHERE id = ?";
                        try(PreparedStatement pstDel = connDestino.prepareStatement(sqlDelete)) {
                            pstDel.setLong(1, rowId);
                            pstDel.executeUpdate();
                        }
                    } else if ("U".equalsIgnoreCase(operation)) {

                        String sqlSelect = "SELECT * FROM " + tableName + " WHERE id = ?";
                        try(PreparedStatement pstSel = connOrigem.prepareStatement(sqlSelect)) {
                            pstSel.setLong(1, rowId);

                            try(ResultSet rsRow = pstSel.executeQuery()) {

                                if (rsRow.next()) {

                                    ResultSetMetaData md = rsRow.getMetaData();
                                    int colCount = md.getColumnCount();

                                    StringBuilder set = new StringBuilder();
                                    List<Integer> cols = new ArrayList<>();

                                    for (int i = 1; i <= colCount; i++) {
                                        String col = md.getColumnLabel(i);
                                        if ("id".equalsIgnoreCase(col)) continue;

                                        if (!set.isEmpty()) set.append(", ");
                                        set.append(col).append(" = ?");
                                        cols.add(i);
                                    }

                                    String sqlUpdate = "UPDATE " + tableName + " SET " + set + " WHERE id = ?";

                                    try(PreparedStatement pstUp = connDestino.prepareStatement(sqlUpdate)) {

                                        int p = 1;
                                        for (Integer i : cols) {
                                            Object value = rsRow.getObject(i);
                                            if (value == null) {
                                                pstUp.setObject(p++ , null);
                                            } else {
                                                pstUp.setObject(p++, value);
                                            }
                                        }

                                        pstUp.setLong(p, rowId);

                                        int updated = pstUp.executeUpdate();

                                        // se não existia no destino, faz INSERT (upsert simples didatico)
                                        if (updated == 0) {
                                            StringBuilder colNames = new StringBuilder();
                                            StringBuilder qs = new StringBuilder();
                                            List<Integer> colsInsert = new ArrayList<>();

                                            for (int i = 1; i <= colCount; i++) {
                                                String col = md.getColumnLabel(i);

                                                if (colNames.length() > 0) {
                                                    colNames.append(", ");
                                                    qs.append(", ");
                                                }
                                                colNames.append(col);
                                                qs.append("?");
                                                colsInsert.add(i);
                                            }

                                            String sqlInsert = "INSERT INTO " + tableName +
                                                    " (" + colNames + ") VALUES (" + qs + ")";

                                            try (PreparedStatement pstIns = connDestino.prepareStatement(sqlInsert)) {
                                                int pi = 1;
                                                for (Integer i : colsInsert) {
                                                    Object value = rsRow.getObject(i);
                                                    pstIns.setObject(pi++, value);
                                                }
                                                pstIns.executeUpdate();
                                            }

                                        }

                                    }

                                    // ====================================================================
                                } else {
                                    // se não existe mais no origem, apaga o destino
                                    String sqlDelete = "DELETE FROM " + tableName + " WHERE id = ?";
                                    try (PreparedStatement pstDel = connDestino.prepareStatement(sqlDelete)) {
                                        pstDel.setLong(1, rowId);
                                        pstDel.executeUpdate();
                                    }
                                }
                            }
                        }
                    }

                    // Marca como processado na origem
                    pstMark.setLong(1, queueId);
                    pstMark.executeUpdate();

                }

            }finally {
                if (rsFila != null) rsFila.close();
                if (psFila != null) psFila.close();
                if (pstMark != null) pstMark.close();
            }
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("Falha na finalização da replicação: "+e.getMessage());
        }

    }

}
