package de.bogenliga.application.services.v1.liga.mapper;

import de.bogenliga.application.common.service.mapping.DataTransferObjectMapper;
import de.bogenliga.application.services.v1.liga.model.LigaDTO;

/**
 * I map the {@link LigaDO} and {@link LigaDTO} objects
 *
 * @author Giuseppe Ferrera, giuseppe.ferrera@student.reutlingen-university.de
 */
public class LigaDTOMapper implements DataTransferObjectMapper {
    /**
     * I map the {@link LigaDO} object to the {@link LigaDTO} object
     */
    public static final Function<LigaDO, LigaDTO> toDTO = ligaDO -> {

        final Long ligaId = ligaDO.getId();
        final String ligaName = ligaDO.getName();
        final Long regionId = ligaDO.getRegionId();
        final String regionName = ligaDO.getRegionName();
        final Long liga_uebergeordnet_id = ligaDO.getLigaUebergeordnetId();
        final String liga_uebergeordnet_name = ligaDO.getLigaUebergeordnetName();
        final Long liga_verantwortlich_id = ligaDO.getLigaVerantwortlichId();
        final String liga_verantwortlich_mail = ligaDO.getLigaVerantwortlichMail();


        return new LigaDTO(ligaId, ligaName, regionId, regionName, liga_uebergeordnet_id, liga_uebergeordnet_name,
                liga_verantwortlich_id, liga_verantwortlich_mail);
    };
    /**
     * I map the {@link LigaDTO} object to the {@link LigaDO} object
     */
    public static final Function<LigaDTO, LigaDO> toDO = dto -> {

        final Long ligaId = dto.getId();
        final String ligaName = dto.getName();
        final Long regionId = dto.getRegionId();
        final Long ligaUebergeordnetId = dto.getRegionUebergeordnetId();
        final Long ligaVerantwortlichId = dto.getLigaVerantwortlichId();

        return new LigaDO(ligaId, ligaName, regionId, ligaUebergeordnetId, ligaVerantwortlichId);
    };


    /**
     * Constructor
     */
    private LigaDTOMapper() {
        // empty private constructor
    }
}
