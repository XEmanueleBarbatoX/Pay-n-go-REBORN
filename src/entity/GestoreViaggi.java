package entity;

import database.ViaggioDAO;
import dto.MyDto;
import exceptions.DatabaseException;
import exceptions.RicercaViaggioFailedException;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class GestoreViaggi {

    private static GestoreViaggi uniqueInstance;

    private GestoreViaggi() {}

    public static GestoreViaggi getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new GestoreViaggi();
        }
        return uniqueInstance;
    }

    public Float GeneraReportIncassi(){
        return 0f;
    }

    public ArrayList<MyDto> ricercaViaggio(String luogoPartenza, String luogoDestinazione,
                                           LocalDate dataPartenza) throws RicercaViaggioFailedException {

        ArrayList<MyDto> viaggiTrovati = new ArrayList<>();
        try {
            ArrayList<ViaggioDAO> listaViaggi = ViaggioDAO.getViaggi();

            for (ViaggioDAO viaggio : listaViaggi) {
                if (viaggio.getLuogoPartenza().equalsIgnoreCase(luogoPartenza) &&
                    viaggio.getLuogoDestinazione().equalsIgnoreCase(luogoDestinazione) &&
                    viaggio.getDataPartenza().toLocalDate().isEqual(dataPartenza)) {

                        MyDto viaggioDTO = caricaDTO(viaggio);
                        viaggiTrovati.add(viaggioDTO);
                }
            }
        } catch (DatabaseException e) {
            throw new RicercaViaggioFailedException("Ricerca viaggio fallito: " + e.getMessage());
        }

        return viaggiTrovati;
    }

    private MyDto caricaDTO(ViaggioDAO viaggio) {
        MyDto viaggioDTO = new MyDto();
        viaggioDTO.setCampo1(viaggio.getLuogoPartenza());
        viaggioDTO.setCampo2(viaggio.getLuogoDestinazione());
        viaggioDTO.setCampo3(viaggio.getDataPartenza().toString());
        viaggioDTO.setCampo4(viaggio.getDataArrivo().toString());
        viaggioDTO.setCampo5(String.valueOf(viaggio.getIdViaggio()));
        viaggioDTO.setCampo6(String.valueOf(viaggio.getIdAutista()));
        viaggioDTO.setCampo7(String.valueOf(viaggio.getContributoSpese()));
        return viaggioDTO;
    }
}
