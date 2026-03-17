package view;

import database.dao.ProcessoTabelaDAO;
import database.dao.ReplicacaoProcessoDAO;
import database.model.TB_REPLICACAO_PROCESSO;
import database.model.TB_REPLICACAO_PROCESSO_TABELA;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class TelaReplicacaoProcessoTabelaView extends JFrame {

    private enum ModoTela{NENHUM, INSERT, UPDATE}
    private ModoTela modo = ModoTela.NENHUM;

    private final Connection conn;
    private final ProcessoTabelaDAO daoTabela;
    private final ReplicacaoProcessoDAO daoProcesso;


    private JTextField txfId;
    private JComboBox<TB_REPLICACAO_PROCESSO> cbProcesso;
    private JTextField txfTabelaOrigem;
    private JTextField txfTabelaDestino;
    private JTextField txfOrdem;
    private JCheckBox chkHabilitado;
    private JTextArea txtWhere;

    private JButton btnSalvar;
    private JButton btnAdicionar;
    private JButton btnBuscar;
    private JButton btnExcluir;

    public TelaReplicacaoProcessoTabelaView(Connection conn) throws SQLException {

        this.conn = conn;
        this.daoTabela = new ProcessoTabelaDAO(conn);
        this.daoProcesso = new ReplicacaoProcessoDAO(conn);


        setTitle("Cadastro de Tabelas");
        setSize(720, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        btnBuscar = new JButton("BUSCAR");
        btnAdicionar = new JButton("ADICIONAR");
        btnSalvar = new JButton("SALVAR");
        btnExcluir = new JButton("EXCLUIR");

        btnBuscar.setBounds(10, 10, 130, 30);
        btnAdicionar.setBounds(150, 10, 130, 30);
        btnSalvar.setBounds(290, 10, 130, 30);
        btnExcluir.setBounds(430, 10, 130, 30);

        getContentPane().add(btnBuscar);
        getContentPane().add(btnAdicionar);
        getContentPane().add(btnSalvar);
        getContentPane().add(btnExcluir);

        JLabel lblId = new JLabel("ID:");
        lblId.setBounds(10, 70, 140, 25);
        getContentPane().add(lblId);

        txfId = new JTextField();
        txfId.setBounds(160, 70, 220, 25);
        getContentPane().add(txfId);

        JLabel lblPRocesso = new JLabel("PROCESSO:");
        lblPRocesso.setBounds(10, 105, 140, 25);
        getContentPane().add(lblPRocesso);

        cbProcesso = new JComboBox<>();
        cbProcesso.setBounds(160, 105, 520, 25);
        getContentPane().add(cbProcesso);

        JLabel lblTabelaOrigem = new JLabel("TABELA ORIGEM:");
        lblTabelaOrigem.setBounds(10, 140, 140, 25);
        getContentPane().add(lblTabelaOrigem);

        txfTabelaOrigem = new JTextField();
        txfTabelaOrigem.setBounds(160, 140, 520, 25);
        getContentPane().add(txfTabelaOrigem);

        JLabel lblTabelaDestino = new JLabel("TABELA DESTINO:");
        lblTabelaDestino.setBounds(10, 175, 140, 25);
        getContentPane().add(lblTabelaDestino);

        txfTabelaDestino = new JTextField();
        txfTabelaDestino.setBounds(160, 175, 520, 25);
        getContentPane().add(txfTabelaDestino);

        JLabel lblOrdem = new JLabel("ORDEM:");
        lblOrdem.setBounds(10, 210, 140, 25);
        getContentPane().add(lblOrdem);

        txfOrdem = new JTextField();
        txfOrdem.setBounds(160, 210, 220, 25);
        getContentPane().add(txfOrdem);

        chkHabilitado = new JCheckBox("HABILITADO");
        chkHabilitado.setBounds(10, 245, 140, 25);
        getContentPane().add(chkHabilitado);

        JLabel lblWhere = new JLabel("WHERE:");
        lblWhere.setBounds(10, 280, 140, 25);
        getContentPane().add(lblWhere);

        txtWhere = new JTextArea();
        txtWhere.setBounds(160, 280, 520, 80);
        getContentPane().add(txtWhere);

        cbProcesso.removeAllItems();;
        ArrayList<TB_REPLICACAO_PROCESSO> processos = daoProcesso.selectAll();
        for (TB_REPLICACAO_PROCESSO p : processos) {
            cbProcesso.addItem(p);
        }

        // Estado Inicial
        txfId.setEnabled(false);
        cbProcesso.setEnabled(false);
        txfTabelaOrigem.setEnabled(false);
        txfTabelaDestino.setEnabled(false);
        txfOrdem.setEnabled(false);
        chkHabilitado.setEnabled(false);
        txtWhere.setEnabled(false);

        btnSalvar.setEnabled(false);
        btnExcluir.setEnabled(false);

        // Ações botoes

        btnAdicionar.addActionListener(e -> {
            modo = ModoTela.INSERT;

            txfId.setText("");
            if (cbProcesso.getItemCount() > 0) cbProcesso.setSelectedIndex(0);

            txfTabelaOrigem.setText("");
            txfTabelaDestino.setText("");
            txfOrdem.setText("");
            chkHabilitado.setSelected(true);
            txtWhere.setText("");

            cbProcesso.setEnabled(true);
            txfTabelaOrigem.setEnabled(true);
            txfTabelaDestino.setEnabled(true);
            txfOrdem.setEnabled(true);
            chkHabilitado.setEnabled(true);
            txtWhere.setEnabled(true);

            btnSalvar.setEnabled(true);
            btnExcluir.setEnabled(false);
        });

        btnSalvar.addActionListener(e -> {
            try {
                if (cbProcesso.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(this, "Selecione um PROCESSO");
                    return;
                }
                if (txfTabelaOrigem.getText().trim().isEmpty() || txfTabelaDestino.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Informe TABELA_ORIGEM e TABELA_DESTINO");
                    return;
                }
                if (txfOrdem.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "INFORME A ORDEM.");
                    return;
                }

                int ordem;
                try {
                    ordem = Integer.parseInt(txfOrdem.getText().trim());
                }catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(this, "ORDEM deve ser número");
                    return;
                }

                TB_REPLICACAO_PROCESSO pSel = (TB_REPLICACAO_PROCESSO) cbProcesso.getSelectedItem();

                TB_REPLICACAO_PROCESSO_TABELA t = new TB_REPLICACAO_PROCESSO_TABELA();
                t.setProcesso_id(pSel.getId());
                t.setTabela_origem(txfTabelaOrigem.getText().trim());
                t.setTabela_destino(txfTabelaDestino.getText().trim());
                t.setOrdem(ordem);
                t.setHabilitado(chkHabilitado.isSelected());
                t.setDs_where(txtWhere.getText());

                if (modo == ModoTela.INSERT) {
                    daoTabela.insert(t);
                    JOptionPane.showMessageDialog(this, "Inaserido com sucesso.");
                } else if (modo == ModoTela.UPDATE) {
                    if (txfId.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "ID não carregado para update.");
                        return;
                    }
                    t.setId(Long.parseLong(txfId.getText().trim()));
                    daoTabela.update(t);
                    JOptionPane.showMessageDialog(this, "Atualizado com sucesso.");
                } else {
                    JOptionPane.showMessageDialog(this, "Clique em ADICIONAR ou BUSCAR antes de salvar.");
                    return;
                }

                // Trava apos salvar

                modo = ModoTela.NENHUM;

                cbProcesso.setEnabled(false);
                txfTabelaOrigem.setEnabled(false);
                txfTabelaDestino.setEnabled(false);
                txfOrdem.setEnabled(false);
                chkHabilitado.setEnabled(false);
                txtWhere.setEnabled(false);

                btnSalvar.setEnabled(false);
            }catch (Exception exception) {
                exception.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao salvar" + exception.getMessage());
            }
        });

        btnExcluir.addActionListener(e ->{
            try {
                if (txfId.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nenhum registro carregado para excluir");
                    return;
                }

                int op = JOptionPane.showConfirmDialog(this, "Confirma exclusão", "Excluir", JOptionPane.YES_NO_OPTION);

                if (op != JOptionPane.YES_OPTION) return;

                Long id = Long.parseLong(txfId.getText().trim());
                daoTabela.delete(id);

                JOptionPane.showMessageDialog(this, "Excluido com sucesso.");

                modo = ModoTela.NENHUM;
                if (cbProcesso.getItemCount() > 0) cbProcesso.setSelectedIndex(0);

                txfTabelaOrigem.setText("");
                txfTabelaDestino.setText("");
                txfOrdem.setText("");
                chkHabilitado.setSelected(false);
                txtWhere.setText("");

                cbProcesso.setEnabled(false);
                txfTabelaOrigem.setEnabled(false);
                txfTabelaDestino.setEnabled(false);
                txfOrdem.setEnabled(false);
                chkHabilitado.setEnabled(false);
                txtWhere.setEnabled(false);
            } catch (Exception exception) {
                exception.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao excluir" + exception.getMessage());
            }
        });

        btnBuscar.addActionListener(e -> {
            try {
                ConsultaProcessoTabelaDialog dlg = new ConsultaProcessoTabelaDialog(this,  daoTabela);
                dlg.setVisible(true);

                TB_REPLICACAO_PROCESSO_TABELA sel = dlg.getSelecionado();
                if (sel == null) return;

                modo = ModoTela.UPDATE;

                txfId.setText(String.valueOf(sel.getId()));
                txfTabelaOrigem.setText(sel.getTabela_origem());
                txfTabelaDestino.setText(sel.getTabela_destino());
                txfOrdem.setText(String.valueOf(sel.getOrdem()));
                chkHabilitado.setSelected(sel.isHabilitado());
                txtWhere.setText(sel.getDs_where());


                // seleciona processo no combo pelo id
                long pid = sel.getProcesso_id();
                for (int i = 0; i < cbProcesso.getItemCount(); i++) {
                    TB_REPLICACAO_PROCESSO item = cbProcesso.getItemAt(i);
                    if (item != null && item.getId() == pid) {
                        cbProcesso.setSelectedIndex(i);
                        break;
                    }
                }

                cbProcesso.setEnabled(true);
                txfTabelaOrigem.setEnabled(true);
                txfTabelaDestino.setEnabled(true);
                txfOrdem.setEnabled(true);
                chkHabilitado.setEnabled(true);
                txtWhere.setEnabled(true);

                btnSalvar.setEnabled(true);
                btnExcluir.setEnabled(true);

            } catch (Exception exception) {
                exception.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao buscar: " + exception.getMessage());
            }
        });
    }
}




































