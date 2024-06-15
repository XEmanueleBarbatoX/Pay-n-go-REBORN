package boundary;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DateTimePicker;
import control.ControllerUtente;
import dto.*;
import utility.Utilities;

public class MainWindow extends JFrame {
    private JPanel mainWindowPanel;
    private JTabbedPane contentTab;
    private JLabel welcomeLabel;
    private JPanel circularLogoPanel;
    private JButton accountTabButton;
    private JButton homeTabButton;
    private JButton valutaTabButton;
    private JButton ricercaTabButton;
    private JButton condividiTabButton;
    private JButton viaggiTabButton;
    private JEditorPane homeEditorPane;
    private JTextField nomeField;
    private JTextField cognomeField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField autoField;
    private JSpinner postiSpinner;
    private JButton aggiornaDatiPersonaliButton;
    private JTextField telefonoField;
    private JTextField luogoPartenzaField;
    private JTextField luogoArrivoField;
    private JButton cercaViaggioButton;
    private JTable viaggiTrovatiTable;
    private JButton prenotaViaggioButton;
    private JTable prenotazioniEffettuateTable;
    private DatePicker dataPartenzaPicker;
    private JButton valutaAutistaButton;
    private JTextField condividiLuogoPartenzaField;
    private JTextField condividiLuogoDestinazioneField;
    private DateTimePicker condividiDataOraPartenzaPicker;
    private DateTimePicker condividiDataOraArrivoPicker;
    private JTextField condividiContributoSpeseField;
    private JButton condividiViaggioButton;
    private JTable viaggiCondivisiTable;
    private JTable prenotazioniTable;
    private JButton gestisciPrenotazioneButton;
    private JButton valutaPasseggeroButton;

    private final JButton[] tabButtons = {homeTabButton, accountTabButton, ricercaTabButton, valutaTabButton,
            condividiTabButton, viaggiTabButton};

    public MainWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Pay-n-go REBORN");
        setSize(1000, 800);
        setMinimumSize(new Dimension(1000, 800));
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().add(new JScrollPane(mainWindowPanel), BorderLayout.CENTER);
        postiSpinner.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        contentTab.setUI(new BasicTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int tabPlacement, int runCount, int maxTabHeight) {
                return -1;
            }
        });

        MyDto sessione = ControllerUtente.getInstance().getSessione();

        if (Long.parseLong(sessione.getCampo1()) == 0)
        {
            JOptionPane.showMessageDialog(rootPane, "Non è presente una sessione utente attiva!", "Errore",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        } else if (Long.parseLong(sessione.getCampo1()) == 1) {
            FormGestore formGestore = new FormGestore();
            formGestore.setVisible(true);
            setVisible(false);
        }

        welcomeLabel.setText(String.format("Ciao %s %s!",
                sessione.getCampo2(), sessione.getCampo3()));

        homeTabButton.addActionListener(actionEvent -> setTabActive(0));
        accountTabButton.addActionListener(actionEvent -> setTabActive(1));
        ricercaTabButton.addActionListener(actionListener -> setTabActive(2));
        valutaTabButton.addActionListener(actionListener -> setTabActive(3));
        condividiTabButton.addActionListener(actionListener -> setTabActive(4));
        viaggiTabButton.addActionListener(actionListener -> setTabActive(5));

        contentTab.addChangeListener(e -> {
            if (contentTab.getSelectedIndex() == 0) {
                MyDto datiSessione = ControllerUtente.getInstance().getSessione();
                welcomeLabel.setText(String.format("Ciao %s %s!", datiSessione.getCampo2(), datiSessione.getCampo3()));
            }
            else if (contentTab.getSelectedIndex() == 1) {
                populateAccountTab();
            } else if (contentTab.getSelectedIndex() == 3) {
                visualizzaPrenotazioniEffettuate();
            } else if (contentTab.getSelectedIndex() == 5) {
                visualizzaViaggiCondivisi();
            }
        });

        aggiornaDatiPersonaliButton.addActionListener(actionEvent -> aggiornaDatiPersonali());

        viaggiTrovatiTable.getSelectionModel().addListSelectionListener(selectionEvent ->
                prenotaViaggioButton.setEnabled(viaggiTrovatiTable.getSelectedRow() != -1));

        cercaViaggioButton.addActionListener(actionEvent -> {
            AbstractMap.SimpleEntry<Boolean, String> result = ricercaViaggiValidateInput();
            if (Boolean.FALSE.equals(result.getKey())) {
                JOptionPane.showMessageDialog(mainWindowPanel, result.getValue(), "Errore",
                        JOptionPane.ERROR_MESSAGE);
            } else
                ricercaViaggi();
        });

        prenotaViaggioButton.addActionListener(actionEvent -> {
            int selectedRow = viaggiTrovatiTable.getSelectedRow();
            if (selectedRow != -1) {
                prenotaViaggio(Long.parseLong((String) viaggiTrovatiTable.getValueAt(selectedRow, 0)));
            }
        });

        prenotazioniEffettuateTable.getSelectionModel().addListSelectionListener(selectionEvent ->
                valutaAutistaButton.setEnabled(prenotazioniEffettuateTable.getSelectedRow() != -1));

        valutaAutistaButton.addActionListener(actionEvent -> {
            int selectedRow = prenotazioniEffettuateTable.getSelectedRow();
            if (selectedRow != -1) {
                valutaUtente(Long.parseLong((String) prenotazioniEffettuateTable.getValueAt(selectedRow, 0)),
                        (String) prenotazioniEffettuateTable.getValueAt(selectedRow, 1));
            }
        });

        condividiViaggioButton.addActionListener(actionEvent -> condividiViaggio());

        viaggiCondivisiTable.getSelectionModel().addListSelectionListener(selectionEvent -> {
            int selectedRow = viaggiCondivisiTable.getSelectedRow();
            if (selectedRow != -1) {
                visualizzaPrenotazioni(Long.parseLong(
                        (String) viaggiCondivisiTable.getValueAt(selectedRow, 0)));
            } else visualizzaPrenotazioni(0); // idViaggio nullo per svuotare la tabella.
        });

        prenotazioniTable.getSelectionModel().addListSelectionListener(selectionEvent -> handlePrenotazioneButtons());

        gestisciPrenotazioneButton.addActionListener(actionEvent -> {
            int selectedRow = prenotazioniTable.getSelectedRow();
            if (selectedRow != -1)
                gestisciPrenotazione(Long.parseLong((String) prenotazioniTable.getValueAt(selectedRow, 0)));

        });

        valutaPasseggeroButton.addActionListener(actionEvent -> {
            int selectedRow = prenotazioniTable.getSelectedRow();
            if (selectedRow != -1) {
               valutaUtente(Long.parseLong((String)prenotazioniTable.getValueAt(selectedRow, 0)),
                               (String)prenotazioniTable.getValueAt(selectedRow, 1));
            }
        });
    }

    private void createUIComponents() {
        circularLogoPanel = new ImagePanel("resources/payngo-circlogo.png", 42, 42);
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

    private void populateAccountTab() {
        MyDto datiSessione = ControllerUtente.getInstance().getSessione();
        nomeField.setText(datiSessione.getCampo2());
        cognomeField.setText(datiSessione.getCampo3());
        emailField.setText(datiSessione.getCampo4());
        passwordField.setText(datiSessione.getCampo5());
        telefonoField.setText(datiSessione.getCampo6());
        autoField.setText(datiSessione.getCampo7());
        postiSpinner.setValue(Integer.parseInt(datiSessione.getCampo8()));
    }

    private void aggiornaDatiPersonali() {
        AbstractMap.SimpleEntry<Boolean, String> result = Utilities.validateDatiPersonali(nomeField.getText(),
                cognomeField.getText(),
                emailField.getText(),
                passwordField.getPassword(),
                telefonoField.getText(),
                autoField.getText(),
                (Integer) postiSpinner.getValue());
        if (Boolean.FALSE.equals(result.getKey())) {
            JOptionPane.showMessageDialog(rootPane, result.getValue(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        } else {
            result = ControllerUtente.getInstance().aggiornaDatiPersonali(nomeField.getText(), cognomeField.getText(),
                    emailField.getText(), passwordField.getPassword(), telefonoField.getText(), autoField.getText(),
                    (Integer) postiSpinner.getValue());

            if (Boolean.FALSE.equals(result.getKey())) {
                JOptionPane.showMessageDialog(rootPane, result.getValue(), "Errore", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(rootPane, result.getValue(), "OK", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void condividiViaggio() {
        AbstractMap.SimpleEntry<Boolean, String> result = validateCondividiViaggioInput();

        if (Boolean.FALSE.equals(result.getKey()))
            JOptionPane.showMessageDialog(mainWindowPanel, result.getValue(), "Errore", JOptionPane.ERROR_MESSAGE);
        else {
            // Variabili temporanee per facilitare la lettura
            String luogoPartenza = condividiLuogoPartenzaField.getText();
            String luogoDestinazione = condividiLuogoDestinazioneField.getText();
            LocalDateTime dataPartenza = condividiDataOraPartenzaPicker.getDateTimeStrict();
            LocalDateTime dataArrivo = condividiDataOraArrivoPicker.getDateTimeStrict();
            float contributoSpese = Float.parseFloat(condividiContributoSpeseField.getText());

            result = ControllerUtente.getInstance().condividiViaggio(luogoPartenza, luogoDestinazione,
                    dataPartenza, dataArrivo, contributoSpese);

            String title = Boolean.FALSE.equals(result.getKey()) ? "Errore" : "Info";
            int messageType = Boolean.FALSE.equals(result.getKey()) ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE;

            JOptionPane.showMessageDialog(mainWindowPanel, result.getValue(), title, messageType);
        }
    }

    private AbstractMap.SimpleEntry<Boolean, String> validateCondividiViaggioInput() {
        if (condividiLuogoPartenzaField.getText().isBlank() || condividiLuogoDestinazioneField.getText().isBlank() ||
                condividiDataOraPartenzaPicker.timePicker.getTime() == null ||
                condividiDataOraPartenzaPicker.datePicker.getDate() == null ||
                condividiDataOraArrivoPicker.timePicker.getTime() == null ||
                condividiDataOraArrivoPicker.datePicker.getDate() == null ||
                condividiContributoSpeseField.getText().isBlank()) {
            return new AbstractMap.SimpleEntry<>(false, "Riempi i campi obbligatori");
        }

        Pattern specialCharRegex = Pattern.compile("[^a-z A-Z]", Pattern.CASE_INSENSITIVE);
        Pattern floatRegex = Pattern.compile("^[+]?([0-9]*[.])?[0-9]+$", Pattern.CASE_INSENSITIVE);
        Matcher condividiLuogoPartenzaMatcher = specialCharRegex.matcher(condividiLuogoPartenzaField.getText());
        Matcher condividiLuogoDestinazioneMatcher = specialCharRegex.matcher(condividiLuogoDestinazioneField.getText());
        Matcher condividiContributoSpeseMatcher = floatRegex.matcher(condividiContributoSpeseField.getText());

        if (condividiLuogoPartenzaField.getText().length() > 50) {
            return new AbstractMap.SimpleEntry<>(false, "Il luogo di partenza supera la lunghezza massima di 50 caratteri");
        }

        if (condividiLuogoPartenzaMatcher.find()) {
            return new AbstractMap.SimpleEntry<>(false, "Il luogo di partenza contiene caratteri non validi");
        }

        if (condividiLuogoDestinazioneField.getText().length() > 50) {
            return new AbstractMap.SimpleEntry<>(false, "Il luogo di destinazione supera la lunghezza massima di 50 caratteri");
        }

        if (condividiLuogoDestinazioneMatcher.find()) {
            return new AbstractMap.SimpleEntry<>(false, "Il luogo di destinazione contiene caratteri non validi");
        }

        if (!condividiContributoSpeseMatcher.find()) {
            return new AbstractMap.SimpleEntry<>(false, "Il contributo spese contiene caratteri non validi");
        }

        LocalDateTime dataPartenzaTemp = condividiDataOraPartenzaPicker.getDateTimeStrict();
        LocalDateTime dataArrivoTemp = condividiDataOraArrivoPicker.getDateTimeStrict();

        if (dataPartenzaTemp.isAfter(dataArrivoTemp) || dataPartenzaTemp.isEqual(dataArrivoTemp)) {
            return new AbstractMap.SimpleEntry<>(false, "La data ed ora di arrivo deve essere successiva alla " +
                    "data ed ora di partenza.");
        }

        if (dataPartenzaTemp.isBefore(LocalDateTime.now()) || dataPartenzaTemp.isEqual(LocalDateTime.now())) {
            return new AbstractMap.SimpleEntry<>(false, "La data di partenza è invalida.");
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
            populateTable(viaggiTrovatiTable, columnNames, data);

        } else {
            JOptionPane.showMessageDialog(rootPane, result.getValue(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
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

    private void visualizzaPrenotazioniEffettuate() {
        String[] columnNames = {"Id prenotazione", "Autista", "Viaggio", "Stato"};
        AbstractMap.SimpleEntry<Boolean, Object> result =
                ControllerUtente.getInstance().visualizzaPrenotazioniEffettuate();
        if (Boolean.FALSE.equals(result.getKey())) {
            JOptionPane.showMessageDialog(rootPane, result.getValue(), "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ArrayList<MyDto> prenotazioniEffettuate = (ArrayList<MyDto>)result.getValue();
        ArrayList<String> rows = new ArrayList<>();
        for (MyDto prenotazioni : prenotazioniEffettuate) {
            rows.add(prenotazioni.getCampo1());
            rows.add(prenotazioni.getCampo2());
            rows.add(prenotazioni.getCampo3());
            rows.add(prenotazioni.getCampo4());
        }

        String[][] data = new String[rows.size() / columnNames.length][columnNames.length];

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++)
            {
                data[i][j] = rows.get(i * columnNames.length + j);
            }
        }

        populateTable(prenotazioniEffettuateTable, columnNames, data);
    }

    private void prenotaViaggio(long idViaggio) {
        AbstractMap.SimpleEntry<Boolean, String> result = ControllerUtente.getInstance().prenotaViaggio(idViaggio);

        String title = Boolean.FALSE.equals(result.getKey()) ? "Errore" : "Info";
        int messageType =
                Boolean.FALSE.equals(result.getKey()) ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE;
        JOptionPane.showMessageDialog(mainWindowPanel, result.getValue(), title, messageType);
    }

    private void valutaUtente(long idPrenotazione, String nomeUtente) {
        ValutaUtente valutaUtente = new ValutaUtente(idPrenotazione, nomeUtente);
        valutaUtente.setVisible(true);
    }


    private void visualizzaViaggiCondivisi() {
        String[] columnNames = {"Id viaggio", "Partenza", "Destinazione", "Data e ora partenza",
                "Data e ora arrivo", "Contributo spese"};
        String[][] data;

        ArrayList<String> rows = new ArrayList<>();
        ArrayList<MyDto> listaValutazioni =
                (ArrayList<MyDto>) ControllerUtente.getInstance().visualizzaViaggiCondivisi();
        for (MyDto valutazione : listaValutazioni) {
            rows.add(valutazione.getCampo1());
            rows.add(valutazione.getCampo2());
            rows.add(valutazione.getCampo3());
            rows.add(valutazione.getCampo4());
            rows.add(valutazione.getCampo5());
            rows.add(valutazione.getCampo6());
        }

        data = new String[rows.size() / columnNames.length][columnNames.length];

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++)
            {
                data[i][j] = rows.get(i * columnNames.length + j);
            }
        }
        populateTable(viaggiCondivisiTable, columnNames, data);
    }

    private void visualizzaPrenotazioni(long idViaggio) {
        String[] columnNames = {"Id prenotazione", "Passeggero", "Stato"};
        String[][] data;
        ArrayList<String> rows = new ArrayList<>();

        if (idViaggio == 0) {
            data = new String[0][0];
        } else {
            AbstractMap.SimpleEntry<Boolean, Object> result =
                    ControllerUtente.getInstance().visualizzaPrenotazioni(idViaggio);
            if (Boolean.FALSE.equals(result.getKey())) {
                JOptionPane.showMessageDialog(mainWindowPanel, result.getValue(), "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            ArrayList<MyDto> listaPrenotazioni = (ArrayList<MyDto>) result.getValue();
            for (MyDto prenotazione : listaPrenotazioni) {
                rows.add(prenotazione.getCampo1());
                rows.add(prenotazione.getCampo2());
                rows.add(prenotazione.getCampo3());
            }

            data = new String[rows.size() / columnNames.length][columnNames.length];
        }

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++)
            {
                data[i][j] = rows.get(i * columnNames.length + j);
            }
        }

        populateTable(prenotazioniTable, columnNames, data);
    }

    private void handlePrenotazioneButtons() {
        int selectedViaggiRow = viaggiCondivisiTable.getSelectedRow();
        int selectedPrenotazioniRow = prenotazioniTable.getSelectedRow();
        if (selectedPrenotazioniRow == -1) {
            gestisciPrenotazioneButton.setEnabled(false);
            valutaPasseggeroButton.setEnabled(false);
        } else {
            String dataPartenza = (String) viaggiCondivisiTable.getValueAt(selectedViaggiRow, 3);
            String accettata = (String) prenotazioniTable.getValueAt(selectedPrenotazioniRow, 2);
            LocalDateTime dataPartenzaViaggio =
                    LocalDateTime.parse(dataPartenza, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            valutaPasseggeroButton.setEnabled(dataPartenzaViaggio.isBefore(LocalDateTime.now()));
            gestisciPrenotazioneButton.setEnabled(accettata.equals("In attesa"));
        }
    }

    private void gestisciPrenotazione(long idPrenotazione) {
        GestisciPrenotazione gestisciPrenotazione = new GestisciPrenotazione(idPrenotazione);
        gestisciPrenotazione.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                visualizzaPrenotazioni(Long.parseLong(
                        (String) viaggiCondivisiTable.getValueAt(viaggiCondivisiTable.getSelectedRow(), 0)));
            }
        });
        gestisciPrenotazione.setVisible(true);
    }
}
