package boundary;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.Enumeration;

import dto.*;

public class MainWindow extends JFrame {
    private JPanel mainWindowPanel;
    private JLabel welcomeLabel;
    private JTabbedPane contentTab;
    private JPanel circularLogoPanel;
    private JButton accountTabButton;
    private JButton homeTabButton;
    private JButton prenotazioniTabButton;
    private JTable prenotazioniTable;
    private JButton gestisciPrenotazioneButton;
    private JEditorPane homeEditorPane;
    private JButton viaggiTabButton;
    private JTextField nomeField;
    private JTextField cognomeField;
    private JTextField emailField;
    private JTextField autoField;
    private JSpinner postiSpinner;
    private JButton aggiornaDatiPersonaliButton;
    private JPasswordField passwordField;
    private JTextField telefonoField;

    public MainWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Pay-n-go REBORN");
        setSize(1000, 800);
        setMinimumSize(new Dimension(1000, 800));
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().add(new JScrollPane(mainWindowPanel), BorderLayout.CENTER);
        welcomeLabel.setText(String.format("Ciao %s %s!",
                UtenteCorrente.getInstance().getNome(), UtenteCorrente.getInstance().getCognome()));

        contentTab.setUI(new BasicTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int tabPlacement, int runCount, int maxTabHeight) {
                    return -1;
            }
        });
        homeTabButton.addActionListener(actionEvent -> {
            contentTab.setSelectedIndex(0);
            homeTabButton.setBackground(new Color(15, 53, 156));
            homeTabButton.setForeground(Color.WHITE);
            accountTabButton.setBackground(new Color(240, 155, 50));
            accountTabButton.setForeground(Color.BLACK);
            viaggiTabButton.setBackground(new Color(240, 155, 50));
            viaggiTabButton.setForeground(Color.BLACK);
            prenotazioniTabButton.setBackground(new Color(240, 155, 50));
            prenotazioniTabButton.setForeground(Color.BLACK);
        });
        accountTabButton.addActionListener(actionEvent -> {
            contentTab.setSelectedIndex(1);
            homeTabButton.setBackground(new Color(240, 155, 50));
            homeTabButton.setForeground(Color.BLACK);
            accountTabButton.setBackground(new Color(15, 53, 156));
            accountTabButton.setForeground(Color.WHITE);
            viaggiTabButton.setBackground(new Color(240, 155, 50));
            viaggiTabButton.setForeground(Color.BLACK);
            prenotazioniTabButton.setBackground(new Color(240, 155, 50));
            prenotazioniTabButton.setForeground(Color.BLACK);
        });
        viaggiTabButton.addActionListener(actionListener -> {
            contentTab.setSelectedIndex(2);
            homeTabButton.setBackground(new Color(240, 155, 50));
            homeTabButton.setForeground(Color.BLACK);
            accountTabButton.setBackground(new Color(240, 155, 50));
            accountTabButton.setForeground(Color.BLACK);
            viaggiTabButton.setBackground(new Color(15, 53, 156));
            viaggiTabButton.setForeground(Color.WHITE);
            prenotazioniTabButton.setBackground(new Color(240, 155, 50));
            prenotazioniTabButton.setForeground(Color.BLACK);
        });
        prenotazioniTabButton.addActionListener(actionListener -> {
            contentTab.setSelectedIndex(3);
            homeTabButton.setBackground(new Color(240, 155, 50));
            homeTabButton.setForeground(Color.BLACK);
            accountTabButton.setBackground(new Color(240, 155, 50));
            accountTabButton.setForeground(Color.BLACK);
            viaggiTabButton.setBackground(new Color(240, 155, 50));
            viaggiTabButton.setForeground(Color.BLACK);
            prenotazioniTabButton.setBackground(new Color(15, 53, 156));
            prenotazioniTabButton.setForeground(Color.WHITE);
        });
        contentTab.addChangeListener(e -> {
            if (contentTab.getSelectedIndex() == 0) {

            } else if (contentTab.getSelectedIndex() == 1) {

            } else if (contentTab.getSelectedIndex() == 2) {

            } else if (contentTab.getSelectedIndex() == 3) {
                /* TODO qualcosa del genere */
                // ControllerUtente.getInstance().visualizzaPrenotazioni

                Object[][] data = {{"1", "Nome Cognome", "3"}, {"2", "Name Surname", "3"}, {"3", "Mario Rossi", "4"}};
                String[] columnNames = {"Id prenotazione", "Passeggero", "Viaggio"};
                TableModel tableModel = new DefaultTableModel(data, columnNames) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                prenotazioniTable.setModel(tableModel);
                prenotazioniTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                prenotazioniTable.setFont(new Font("Lucida Sans Typewriter", Font.PLAIN, 16));
                DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
                headerRenderer.setBackground(new Color(48, 48, 48));
                headerRenderer.setOpaque(true);
                prenotazioniTable.getTableHeader().setDefaultRenderer(headerRenderer);
                prenotazioniTable.getTableHeader().setReorderingAllowed(false);
                prenotazioniTable.getTableHeader().setResizingAllowed(false);
            }
        });
        prenotazioniTable.getSelectionModel().addListSelectionListener(e -> {
            if (prenotazioniTable.getSelectedRow() != -1) {
                gestisciPrenotazioneButton.setEnabled(true);
            } else if (prenotazioniTable.getSelectedRow() == -1) {
                gestisciPrenotazioneButton.setEnabled(false);
            }
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            Font f = new javax.swing.plaf.FontUIResource("Lucida Sans Typewriter", Font.PLAIN,16);
            Enumeration<Object> keys = UIManager.getDefaults().keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = UIManager.get (key);
                if (value instanceof javax.swing.plaf.FontUIResource)
                    UIManager.put (key, f);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            java.util.logging.Logger.getLogger("loggerFormLogin").log(java.util.logging.Level.SEVERE, e.getMessage());
        }
        MainWindow window = new MainWindow();
        window.setVisible(true);
    }

    private void createUIComponents() {
        circularLogoPanel = new MainWindowCircularLogoPanel();
    }
}
