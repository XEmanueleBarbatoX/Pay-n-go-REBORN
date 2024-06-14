package entity;

import dto.MyDto;
import exceptions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class FacadeEntityUtente {

    private static FacadeEntityUtente uniqueInstance;

    private FacadeEntityUtente() {}

    public static FacadeEntityUtente getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new FacadeEntityUtente();
        }
        return uniqueInstance;
    }


    public void registraUtente(String nome, String cognome, String email, String auto, char[] password,
                                      Integer postiDisp, String telefono) throws RegistrationFailedException {
        GestoreUtenti.getInstance().registraUtente(nome, cognome, email, auto, password, postiDisp, telefono);
    }

    public void loginUtente(String email, char[] password) throws LoginFailedException {
        GestoreUtenti.getInstance().loginUtente(email, password);
    }

    public void condividiViaggio(String luogoPartenza,
                                 String luogoDestinazione,
                                 LocalDateTime dataPartenza,
                                 LocalDateTime dataArrivo,
                                 float contributoSpese) throws CondivisioneViaggioFailedException {
        GestoreUtenti.getInstance().condividiViaggio(luogoPartenza, luogoDestinazione, dataPartenza, dataArrivo, contributoSpese);
    }

    public void aggiornaDatiPersonali(String nome, String cognome, String email,
                                      String auto, char[] password, Integer postiDisp,
                                      String telefono) throws AggiornamentoDatiFailedException {

        GestoreUtenti.getInstance().aggiornaDatiPersonali(nome, cognome, email, auto, password, postiDisp, telefono);

    }

    public ArrayList<MyDto> visualizzaPrenotazioni() {
        return GestoreUtenti.getInstance().visualizzaPrenotazioni();
    }

    public ArrayList<MyDto> ricercaViaggio(String luogoPartenza, String luogoDestinazione,
                                           LocalDate dataPartenza) throws RicercaViaggioFailedException {

        return GestoreViaggi.getInstance().ricercaViaggio(luogoPartenza, luogoDestinazione, dataPartenza);
    }

    public void prenotaViaggio(long idViaggio) throws PrenotaViaggioFailedException {
        GestoreUtenti.getInstance().prenotaViaggio(idViaggio);
    }

    public void gestisciPrenotazione(long idPrenotazione, boolean accettata) throws PrenotazioneGestitaFailedException {
            GestoreUtenti.getInstance().gestisciPrenotazione(idPrenotazione,accettata);

    }


    public MyDto getSessione() {
        return GestoreUtenti.getInstance().getSessione();
    }

}
