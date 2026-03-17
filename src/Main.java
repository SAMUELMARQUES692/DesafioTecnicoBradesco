import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main extends JFrame {

    static String dbUserName = System.getenv("DATABASE_USERNAME");
    static String dbPassword = System.getenv("DATABASE_PASSWORD");

    private JDesktopPane desktop;
    private static Connection conn;

    public Main() {
        setTitle("Sistema de Replicação de Dados");
        setSize(1700, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        desktop = new JDesktopPane();
        setContentPane(desktop);

        JMenu menuSistema = new JMenu("Sistema");

        JMenuItem itemExecutar = new JMenuItem("Executar Replicação");
        itemExecutar.addActionListener(e -> {});
        menuSistema.add(itemExecutar);

        JMenuItem itemSair = new JMenuItem("Sair");
        itemExecutar.addActionListener(e -> {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            System.exit(0);
        });
        menuSistema.add(itemSair);

        JMenu menuCadastro = new JMenu("Cadastro");

        JMenuItem itemProcesso = new JMenuItem("Processo");
        itemProcesso.addActionListener(e ->{});
        menuCadastro.add(itemProcesso);

        JMenuItem itemProcessoTabela = new JMenuItem("Processo X Tabelas");
        itemProcessoTabela.addActionListener(e -> {});
        menuCadastro.add(itemProcessoTabela);

        JMenuItem itemDirecao = new JMenuItem("Direção");
        itemDirecao.addActionListener(e -> {});
        menuCadastro.add(itemDirecao);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menuSistema);
        menuBar.add(menuCadastro);
        setJMenuBar(menuBar);
    }

    public static void main(String[] args) {

        try {

            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/controle", dbUserName, dbPassword);
            SwingUtilities.invokeLater(() -> new Main().setVisible(true));

        }catch (Exception exception) {
            exception.printStackTrace();
            JOptionPane.showMessageDialog(null, "Não foi possivel conectar no banco de dados.");
            System.exit(0);
        }



    }
}