package boundary;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.AbstractMap;
import java.util.ArrayList;

import com.github.lgooddatepicker.components.DatePicker;
import control.ControllerUtente;
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
    private JButton ricercaTabButton;
    private JTextField nomeField;
    private JTextField cognomeField;
    private JTextField emailField;
    private JTextField autoField;
    private JSpinner postiSpinner;
    private JButton aggiornaDatiPersonaliButton;
    private JPasswordField passwordField;
    private JTextField telefonoField;
    private JTextField luogoPartenzaField;
    private JTextField luogoArrivoField;
    private JTable viaggiTable;
    private JButton prenotaViaggioButton;
    private DatePicker dataPartenzaPicker;
    private JButton cercaViaggioButton;
    private JButton condividiTabButton;
    private JButton viaggiTabButton;

    private JButton[] tabButtons = {homeTabButton, accountTabButton, ricercaTabButton, prenotazioniTabButton,
            condividiTabButton, viaggiTabButton};

    public MainWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Pay-n-go REBORN");
        setSize(1000, 800);
        setMinimumSize(new Dimension(1000, 800));
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().add(new JScrollPane(mainWindowPanel), BorderLayout.CENTER);
        if (UtenteCorrente.getInstance().getIdUtenteCorrente() == 0)
        {
            JOptionPane.showMessageDialog(rootPane, "Non è presente una sessione utente attiva!", "Errore",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        } else if (UtenteCorrente.getInstance().getIdUtenteCorrente() == 1) {
            FormGestore formGestore = new FormGestore();
            formGestore.setVisible(true);
            setVisible(false);
        }

        welcomeLabel.setText(String.format("Ciao %s %s!",
                UtenteCorrente.getInstance().getNome(), UtenteCorrente.getInstance().getCognome()));

        contentTab.setUI(new BasicTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int tabPlacement, int runCount, int maxTabHeight) {
                    return -1;
            }
        });

        homeTabButton.addActionListener(actionEvent -> setTabActive(0));

        accountTabButton.addActionListener(actionEvent -> setTabActive(1));

        ricercaTabButton.addActionListener(actionListener -> setTabActive(2));

        prenotazioniTabButton.addActionListener(actionListener -> setTabActive(3));

        condividiTabButton.addActionListener(actionListener -> setTabActive(4));

        viaggiTabButton.addActionListener(actionListener -> setTabActive(5));

        contentTab.addChangeListener(e -> {
            if (contentTab.getSelectedIndex() == 1) {
                populateAccountTab();
            } else if (contentTab.getSelectedIndex() == 3) {
                visuaizzaPreotazioni();
            }
        });

        prenotazioniTable.getSelectionModel().addListSelectionListener(selectionEvent ->
                prenotaViaggioButton.setEnabled(viaggiTable.getSelectedRow() != -1));

        cercaViaggioButton.addActionListener(actionEvent -> {
            AbstractMap.SimpleEntry<Boolean, String> result = ricercaViaggiValidateInput();
            if (Boolean.FALSE.equals(result.getKey())) {
                JOptionPane.showMessageDialog(mainWindowPanel, result.getValue(), "Errore",
                        JOptionPane.ERROR_MESSAGE);
            } else
                ricercaViaggi();
        });

        viaggiTable.getSelectionModel().addListSelectionListener(selectionEvent ->
                prenotaViaggioButton.setEnabled(viaggiTable.getSelectedRow() != -1));

        prenotaViaggioButton.addActionListener(actionEvent -> {

        });
    }

    private void createUIComponents() {
        circularLogoPanel = new MainWindowCircularLogoPanel();
    }

    private void setTabActive(int index) {
        contentTab.setSelectedIndex(index);
        if (index < 0 || index > 5)
            return;
        for (int i = 0; i < 6; i++) {
            if (i != index) {
                tabButtons[i].setBackground(new Color(240, 155, 50));
                tabButtons[i].setForeground(Color.BLACK);
            } else {
                tabButtons[i].setBackground(new Color(15, 53, 156));
                tabButtons[i].setForeground(Color.WHITE);
            }
        }
    }

    private void populateAccountTab() {
        nomeField.setText(UtenteCorrente.getInstance().getNome());
        cognomeField.setText(UtenteCorrente.getInstance().getCognome());
        emailField.setText(UtenteCorrente.getInstance().getEmail());
        passwordField.setText(UtenteCorrente.getInstance().getPassword());
        telefonoField.setText(UtenteCorrente.getInstance().getContattoTelefonico());
        autoField.setText(UtenteCorrente.getInstance().getAuto());
        postiSpinner.setValue(Integer.valueOf(UtenteCorrente.getInstance().getPostiDisp()));
    }

    private void populateTable(JTable table, Object[] columnNames, Object[][] data) {
        TableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("Lucida Sans Typewriter", Font.PLAIN, 16));
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(48, 48, 48));
        headerRenderer.setOpaque(true);
        table.getTableHeader().setDefaultRenderer(headerRenderer);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
    }

    private AbstractMap.SimpleEntry<Boolean, String> ricercaViaggiValidateInput() {
        if (luogoPartenzaField.getText().isBlank()) {
            return new AbstractMap.SimpleEntry<>(false, "Il campo luogo di partenza non può essere vuoto");
        }

        if (luogoArrivoField.getText().isBlank()) {
            return new AbstractMap.SimpleEntry<>(false, "Il campo luogo di destinazione non può essere vuoto");
        }

        if (dataPartenzaPicker.getDate() == null) {
            return new AbstractMap.SimpleEntry<>(false, "Il campo data di partenza non può essere vuoto");
        }

        return new AbstractMap.SimpleEntry<>(true, "OK");
    }

    private void ricercaViaggi() {
        AbstractMap.SimpleEntry<Boolean, Object> result;
        result = ControllerUtente.getInstance().ricercaViaggio(luogoPartenzaField.getText(),
                luogoArrivoField.getText(),
                dataPartenzaPicker.getDate());

        if (Boolean.TRUE.equals(result.getKey())) {
            ArrayList<MyDto> viaggiTrovati = (ArrayList<MyDto>)result.getValue();
            ArrayList<String> rows = new ArrayList<>();
            for (MyDto viaggi : viaggiTrovati) {
                rows.add(viaggi.getCampo1());
                rows.add(viaggi.getCampo2());
                rows.add(viaggi.getCampo3());
                rows.add(viaggi.getCampo4());
                rows.add(viaggi.getCampo5());
                rows.add(viaggi.getCampo6());
                rows.add(viaggi.getCampo7());
            }

            if (rows.isEmpty()) {
                JOptionPane.showMessageDialog(rootPane, "Non è stato trovato alcun viaggio corrispondente ai " +
                        "criteri di ricerca inseriti.", "Nessun viaggio trovato", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String[] columnNames = {"Id viaggio", "Partenza", "Destinazione", "Data e ora partenza",
                    "Data e ora arrivo", "Contributo spese", "Autista"};
            String[][] data = new String[rows.size() / columnNames.length][columnNames.length];

            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++)
                {
                    data[i][j] = rows.get(i * columnNames.length + j);
                }
            }
            populateTable(viaggiTable, columnNames, data);

        } else {
            JOptionPane.showMessageDialog(rootPane, result.getValue(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void visuaizzaPreotazioni() {
        /* TODO qualcosa del genere */
        // ControllerUtente.getInstance().visualizzaPrenotazioni

        String[][] data = {{"1", "Nome Cognome", "3"}, {"2", "Name Surname", "3"}, {"3", "Mario Rossi", "4"}};
        String[] columnNames = {"Id prenotazione", "Passeggero", "Viaggio"};
        populateTable(prenotazioniTable, columnNames, data);
    }

}
