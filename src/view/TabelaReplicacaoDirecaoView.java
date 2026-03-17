package view;

import database.dao.DirecaoDAO;
import database.dao.ReplicacaoProcessoDAO;
import database.model.TB_REPLICACAO_DIRECAO;
import database.model.TB_REPLICACAO_PROCESSO;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class TabelaReplicacaoDirecaoView extends JFrame {

    private enum ModoTela{NENHUM, INSERT, UPDATE}
    private TabelaReplicacaoDirecaoView.ModoTela modoTela = TabelaReplicacaoDirecaoView.ModoTela.NENHUM;

    private final Connection conn;
    private final DirecaoDAO dao;
    private final ReplicacaoProcessoDAO daoProcesso;


    private JTextField txfId;
    private JComboBox<TB_REPLICACAO_PROCESSO> cbProcesso;

    private JTextField txfOrigem;
    private JTextField txfDestino;
    private JTextField txfUsuarioOrigem;
    private JTextField txfUsuarioDestino;
    private JTextField txfSenhaOrigem;
    private JTextField txfSenhaDestino;
    private JCheckBox chkHabilitado;

    private JButton btnSalvar;
    private JButton btnAdicionar;
    private JButton btnBuscar;
    private JButton btnExcluir;

    public TabelaReplicacaoDirecaoView(Connection conn) throws SQLException {
        this.conn = conn;
        this.dao = new DirecaoDAO(conn);
        this.daoProcesso = new ReplicacaoProcessoDAO(conn);

        setTitle("Cadastro de Tabelas");
        setSize(760, 460);
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

        JLabel lblProcesso = new JLabel("PROCESSO:");
        lblProcesso.setBounds(10, 105, 140, 25);
        getContentPane().add(lblProcesso);

        cbProcesso = new JComboBox<>();
        cbProcesso.setBounds(160, 105, 520, 25);
        getContentPane().add(cbProcesso);

        JLabel lblOrigem = new JLabel("ORIGEM:");
        lblOrigem.setBounds(10, 150, 200, 25);
        lblOrigem.setFont(lblOrigem.getFont().deriveFont(Font.BOLD));
        getContentPane().add(lblOrigem);

        JLabel lblDirecaoOrigem = new JLabel("DIREÇÃO_ORIGEM:");
        lblDirecaoOrigem.setBounds(10, 185, 140, 25);
        getContentPane().add(lblDirecaoOrigem);

        txfOrigem = new JTextField();
        txfOrigem.setBounds(160, 185, 560, 25);
        getContentPane().add(txfOrigem);

        JLabel lblUsuarioOrigem = new JLabel("USUARIO_ORIGEM:");
        lblUsuarioOrigem.setBounds(10, 220, 140, 25);
        getContentPane().add(lblUsuarioOrigem);

        txfUsuarioOrigem = new JTextField();
        txfUsuarioOrigem.setBounds(160, 220, 560, 25);
        getContentPane().add(txfUsuarioOrigem);

        JLabel lblSenhaOrigem = new JLabel("SENHA_ORIGEM:");
        lblSenhaOrigem.setBounds(10, 255, 140, 25);
        getContentPane().add(lblSenhaOrigem);

        txfSenhaOrigem = new JTextField();
        txfSenhaOrigem.setBounds(160, 255, 560, 25);
        getContentPane().add(txfSenhaOrigem);

        JLabel lblDestino = new JLabel("DESTINO:");
        lblDestino.setBounds(10, 300, 200, 25);
        lblDestino.setFont(lblOrigem.getFont().deriveFont(Font.BOLD));
        getContentPane().add(lblDestino);

        JLabel lblDirecaoDestino = new JLabel("DIREÇÃO_DESTINO:");
        lblDirecaoDestino.setBounds(10, 335, 140, 25);
        getContentPane().add(lblDirecaoDestino);

        txfDestino = new JTextField();
        txfDestino.setBounds(160, 335, 560, 25);
        getContentPane().add(txfDestino);

        JLabel lblUsuarioDestino = new JLabel("USUARIO_DESTINO:");
        lblUsuarioDestino.setBounds(10, 370, 140, 25);
        getContentPane().add(lblUsuarioDestino);

        txfUsuarioDestino = new JTextField();
        txfUsuarioDestino.setBounds(160, 370, 280, 25);
        getContentPane().add(txfUsuarioDestino);

        JLabel lblSenhaDestinatario = new JLabel("SENHA_DESTINO:");
        lblSenhaDestinatario.setBounds(450, 370, 120, 25);
        getContentPane().add(lblSenhaDestinatario);

        txfSenhaDestino = new JTextField();
        txfSenhaDestino.setBounds(570, 370, 150, 25);
        getContentPane().add(txfSenhaDestino);

        chkHabilitado = new JCheckBox("HABILITADO");
        chkHabilitado.setBounds(10, 405, 140, 25);
        getContentPane().add(chkHabilitado);

        cbProcesso.removeAllItems();;
        ArrayList<TB_REPLICACAO_PROCESSO> processos = daoProcesso.selectAll();
        for (TB_REPLICACAO_PROCESSO p : processos) {
            cbProcesso.addItem(p);
        }


        txfId.setEnabled(false);
        cbProcesso.setEnabled(false);
        chkHabilitado.setEnabled(false);
        txfOrigem.setEnabled(false);
        txfUsuarioOrigem.setEnabled(false);
        txfSenhaOrigem.setEnabled(false);
        txfUsuarioDestino.setEnabled(false);
        txfSenhaDestino.setEnabled(false);
        btnSalvar.setEnabled(false);
        btnExcluir.setEnabled(false);

        btnAdicionar.addActionListener(e -> {

            modoTela = ModoTela.INSERT;

            txfId.setText("");

            if (cbProcesso.getItemCount() > 0) {
                cbProcesso.setSelectedIndex(0);
            }
            chkHabilitado.setSelected(true);

            txfOrigem.setText("");
            txfUsuarioOrigem.setText("");
            txfSenhaOrigem.setText("");

            txfDestino.setText("");
            txfUsuarioDestino.setText("");
            txfSenhaDestino.setText("");

            cbProcesso.setEnabled(true);
            chkHabilitado.setEnabled(true);
            txfOrigem.setEnabled(true);
            txfUsuarioOrigem.setEnabled(true);
            txfSenhaOrigem.setEnabled(true);

            txfDestino.setEnabled(true);
            txfUsuarioDestino.setEnabled(true);
            txfSenhaDestino.setEnabled(true);

            btnSalvar.setEnabled(true);
            btnExcluir.setEnabled(true);
        });

        btnSalvar.addActionListener(e ->{
            try {
                if (cbProcesso.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(this, "Informe o PROCESSO");
                    return;
                }

                if (txfOrigem.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Informe a ORIGEM");
                    return;
                }

                if (txfDestino.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Informe a DESTINO");
                    return;
                }

                if (txfSenhaOrigem.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Informe a SENHA DE ORIGEM");
                    return;
                }

                if (txfSenhaDestino.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Informe a SENHA DE DESTINO");
                    return;
                }

                if (txfUsuarioOrigem.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Informe o USUARIO DE ORIGEM");
                    return;
                }

                if (txfUsuarioDestino.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Informe o USUARIO DE DESTINO");
                    return;
                }

                TB_REPLICACAO_PROCESSO pSel = (TB_REPLICACAO_PROCESSO) cbProcesso.getSelectedItem();

                TB_REPLICACAO_DIRECAO d = new TB_REPLICACAO_DIRECAO();
                d.setProcesso_id(pSel.getId());
                d.setHabilitado(chkHabilitado.isSelected());
                d.setDirecao_origem(txfOrigem.getText().trim());
                d.setUsuario_origem(txfUsuarioOrigem.getText().trim());
                d.setSenha_origem(txfSenhaOrigem.getText().trim());
                d.setDirecao_destino(txfDestino.getText().trim());
                d.setUsuario_destino(txfUsuarioDestino.getText().trim());
                d.setSenha_destino(txfSenhaDestino.getText().trim());

                if (modoTela == ModoTela.INSERT) {
                    dao.insert(d);
                    JOptionPane.showMessageDialog(this, "Direção Cadastrada!");
                } else if (modoTela == ModoTela.UPDATE) {
                    if (txfId.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "ID não carregado para update");
                        return;
                    }
                    d.setId(Long.valueOf(Integer.parseInt(txfId.getText())));
                    dao.update(d);
                    JOptionPane.showMessageDialog(this, "Direção Atualizada!");
                }else {
                    JOptionPane.showMessageDialog(this, "Clique em Adicionar ou buscar antes de salvar");
                }

                modoTela = ModoTela.NENHUM;
                txfId.setEnabled(false);
                cbProcesso.setEnabled(false);
                chkHabilitado.setEnabled(false);
                txfOrigem.setEnabled(false);
                txfUsuarioOrigem.setEnabled(false);
                txfSenhaOrigem.setEnabled(false);
                txfDestino.setEnabled(false);
                txfUsuarioDestino.setEnabled(false);
                txfSenhaDestino.setEnabled(false);
                btnSalvar.setEnabled(false);
                btnExcluir.setEnabled(false);


            }catch (Exception exception) {
                exception.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao Salvar" + exception.getMessage());
            }
        });

        btnExcluir.addActionListener(e -> {
           try {
               if (txfId.getText().trim().isEmpty()) {
                  JOptionPane.showMessageDialog(this, "ID não carregado para exclusão");
                  return;
               }

               int op = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir o registro?", "Excluir", JOptionPane.YES_NO_OPTION);
               if (op != JOptionPane.YES_OPTION) return;

               Long id = Long.parseLong(txfId.getText());
               dao.delete(id);
               JOptionPane.showMessageDialog(this,"Direção excluido");

               modoTela = ModoTela.NENHUM;

               txfId.setText("");

               if (cbProcesso.getItemCount() > 0) {
                   cbProcesso.setSelectedIndex(0);
               }
               chkHabilitado.setSelected(false);

               txfOrigem.setText("");
               txfUsuarioOrigem.setText("");
               txfSenhaOrigem.setText("");

               txfDestino.setText("");
               txfUsuarioDestino.setText("");
               txfSenhaDestino.setText("");

               cbProcesso.setEnabled(false);
               chkHabilitado.setEnabled(false);
               txfOrigem.setEnabled(false);
               txfUsuarioOrigem.setEnabled(false);
               txfSenhaOrigem.setEnabled(false);

               txfDestino.setEnabled(false);
               txfUsuarioDestino.setEnabled(false);
               txfSenhaDestino.setEnabled(false);

               btnSalvar.setEnabled(false);
               btnExcluir.setEnabled(false);


           }catch (Exception exception) {
               exception.printStackTrace();
               JOptionPane.showMessageDialog(null, "Erro ao excluir" + exception.getMessage());
           }
        });

        btnBuscar.addActionListener(e -> {
            try {
                ConsultaDirecaoDialog dlg = new ConsultaDirecaoDialog(this, dao);
                dlg.setVisible(true);

                TB_REPLICACAO_DIRECAO sel = dlg.getSelecionado();

                if (sel == null) return;

                modoTela = ModoTela.UPDATE;

                txfId.setText(String.valueOf(sel.getId()));
                chkHabilitado.setSelected(sel.isHabilitado());

                txfOrigem.setText(sel.getDirecao_origem());
                txfUsuarioOrigem.setText(sel.getUsuario_origem());
                txfSenhaOrigem.setText(sel.getSenha_origem());

                txfDestino.setText(sel.getDirecao_destino());
                txfUsuarioDestino.setText(sel.getUsuario_destino());

               Long id = sel.getProcesso_id();
               for (int i = 0; i < cbProcesso.getItemCount(); i++) {
                    TB_REPLICACAO_PROCESSO item = cbProcesso.getItemAt(i);
                    if (item.getId() == id) {
                        cbProcesso.setSelectedIndex(i);
                        break;
                    }
               }

                cbProcesso.setEnabled(true);
                chkHabilitado.setEnabled(true);
                txfOrigem.setEnabled(true);
                txfUsuarioOrigem.setEnabled(true);
                txfSenhaOrigem.setEnabled(true);
                txfUsuarioDestino.setEnabled(true);
                txfSenhaDestino.setEnabled(true);
                btnSalvar.setEnabled(true);
                btnExcluir.setEnabled(true);

            }catch (Exception exception) {
                exception.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao Consultar" + exception.getMessage());
            }
        });


    }

}
