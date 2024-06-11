package entity;

import exceptions.DatabaseException;
import exceptions.LoginUserException;
import exceptions.RegistrationFailedException;
import database.UtenteRegistratoDAO;
import dto.utenteCorrente;
import java.util.Arrays;

public class GestoreUtenti {

    private static GestoreUtenti uniqueInstance;

    private GestoreUtenti() {
    }

    public static GestoreUtenti getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new GestoreUtenti();
        }
        return uniqueInstance;
    }

    public void registraUtente(String nome, String cognome, String email,
                               String auto, char[] password, Integer postiDisp,
                               String telefono) throws RegistrationFailedException {

        UtenteRegistratoDAO utenteDAO = new UtenteRegistratoDAO();
        try {
            utenteDAO.createUtenteRegistrato(nome, cognome, telefono, email, auto,
                    postiDisp, new String(password));
        } catch (DatabaseException e) {
            throw new RegistrationFailedException("Registrazione Utente fallita");
        }
    }

    public void loginUtente(String email, char[] password) throws LoginUserException {
        UtenteRegistratoDAO utenteDAO = new UtenteRegistratoDAO();
        try {
            utenteDAO.readUtenteRegistrato(email, Arrays.toString(password));
        } catch (DatabaseException e) {
            throw new LoginUserException("Login Utente fallito: " + e.getMessage());
        }
    }
}