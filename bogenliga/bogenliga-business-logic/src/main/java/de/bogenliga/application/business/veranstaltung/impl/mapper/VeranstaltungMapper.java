package de.bogenliga.application.business.veranstaltung.impl.mapper;

import de.bogenliga.application.business.liga.impl.entity.LigaBE;
import de.bogenliga.application.business.wettkampftyp.impl.entity.WettkampfTypBE;
import de.bogenliga.application.common.component.mapping.ValueObjectMapper;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.function.Function;
import de.bogenliga.application.business.veranstaltung.api.types.VeranstaltungDO;
import de.bogenliga.application.business.veranstaltung.impl.entity.VeranstaltungBE;
import de.bogenliga.application.business.user.impl.entity.UserBE;

import de.bogenliga.application.common.time.DateProvider;

/**
 * @author Daniel Schott, daniel.schott@student.reutlingen-university.de
 */
public class VeranstaltungMapper implements ValueObjectMapper {


    /**
     * Converts a {@link VeranstaltungBE} to a {@link VeranstaltungDO}
     *
     */
    public static final VeranstaltungDO toVeranstaltungDO(VeranstaltungBE veranstaltungBE, UserBE userBE, WettkampfTypBE wettkamptypBE, LigaBE ligaBE){

        OffsetDateTime createdAtUtc = DateProvider.convertTimestamp(veranstaltungBE.getCreatedAtUtc());
        OffsetDateTime lastModifiedAtUtc = DateProvider.convertTimestamp(veranstaltungBE.getLastModifiedAtUtc());

        VeranstaltungDO veranstaltungDO = new VeranstaltungDO(
                veranstaltungBE.getVeranstaltung_id(),
                veranstaltungBE.getVeranstaltung_wettkampftyp_id(),
                veranstaltungBE.getVeranstaltung_name(),
                veranstaltungBE.getVeranstaltung_sportjahr(),
                veranstaltungBE.getVeranstaltung_meldedeadline(),
                veranstaltungBE.getVeranstaltung_ligaleiter_id(),
                veranstaltungBE.getVeranstaltung_liga_id(),
                userBE.getUserEmail(),
                wettkamptypBE.getwettkampftypname(),
                ligaBE.getLigaName()
        );
        veranstaltungDO.setCreatedAtUtc(createdAtUtc);
        veranstaltungDO.setLastModifiedAtUtc(lastModifiedAtUtc);
        return veranstaltungDO;

    }


    /**
     * mapps a Veranstaltung Data Object into a Veranstaltung Business Entity
     */
    public static final Function<VeranstaltungDO, VeranstaltungBE> toVeranstaltungBE= veranstaltungDO -> {

        Timestamp createdAtUtcTimestamp = DateProvider.convertOffsetDateTime(veranstaltungDO.getCreatedAtUtc());
        Timestamp lastModifiedAtUtcTimestamp = DateProvider.convertOffsetDateTime(veranstaltungDO.getLastModifiedAtUtc());

       VeranstaltungBE veranstaltungBE = new VeranstaltungBE();
       veranstaltungBE.setVeranstaltung_id(veranstaltungDO.getVeranstaltungID());
       veranstaltungBE.setVeranstaltung_wettkampftyp_id(veranstaltungDO.getVeranstaltungWettkampftypID());
       veranstaltungBE.setVeranstaltung_name(veranstaltungDO.getVeranstaltungName());
       veranstaltungBE.setVeranstaltung_meldedeadline(veranstaltungDO.getVeranstaltungMeldeDeadline());
       veranstaltungBE.setVeranstaltung_ligaleiter_id(veranstaltungDO.getVeranstaltungLigaleiterID());
       veranstaltungBE.setVeranstaltung_liga_id(veranstaltungDO.getVeranstaltungLigaID());
       veranstaltungBE.setVeranstaltung_sportjahr(veranstaltungDO.getVeranstaltungSportJahr());


       veranstaltungBE.setCreatedAtUtc(createdAtUtcTimestamp);
       veranstaltungBE.setCreatedByUserId(veranstaltungDO.getCreatedByUserId());
       veranstaltungBE.setLastModifiedAtUtc(lastModifiedAtUtcTimestamp);
       veranstaltungBE.setLastModifiedByUserId(veranstaltungDO.getLastModifiedByUserId());
       veranstaltungBE.setVersion(veranstaltungDO.getVersion());

        return veranstaltungBE;
    };


    private VeranstaltungMapper(){
        //empty Constructor
    }
}
