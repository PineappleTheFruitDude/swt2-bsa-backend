package de.bogenliga.application.services.v1.liga.service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import de.bogenliga.application.business.liga.api.LigaComponent;
import de.bogenliga.application.business.liga.api.types.LigaDO;
import de.bogenliga.application.common.service.ServiceFacade;
import de.bogenliga.application.common.service.UserProvider;
import de.bogenliga.application.common.validation.Preconditions;
import de.bogenliga.application.services.v1.liga.mapper.LigaDTOMapper;
import de.bogenliga.application.services.v1.liga.model.LigaDTO;
import de.bogenliga.application.springconfiguration.security.permissions.RequiresPermission;
import de.bogenliga.application.springconfiguration.security.types.UserPermission;

/**
 * I'm a REST resource and handle liga CRUD requests over the HTTP protocol
 *
 * @author Giuseppe Ferrera, giuseppe.ferrera@student.reutlingen-university.de
 */
@RestController
@CrossOrigin
@RequestMapping("v1/liga")
public class LigaService implements ServiceFacade {
    private static final String PRECONDITION_MSG_LIGA = "Liga must not be null";
    private static final String PRECONDITION_MSG_LIGA_ID = "Liga Id must not be negative";
    private static final String PRECONDITION_MSG_LIGA_REGION = "Region can not be null";
    private static final String PRECONDITION_MSG_LIGA_REGION_ID_NEG = "Region id can not be negative";
    private static final String PRECONDITION_MSG_LIGA_UEBERGEORDNET_ID_NEG = "Region id can not be negative";
    private static final String PRECONDITION_MSG_LIGA_VERANTWORTLICH_ID_NEG = "Verantwortlich id can not be negative";

    private final Logger logger = LoggerFactory.getLogger(LigaService.class);

    private final LigaComponent ligaComponent;


    /**
     * Constructor with dependency injection
     *
     * @param ligaComponent to handle the database CRUD requests
     */
    @Autowired
    public LigaService(final LigaComponent ligaComponent) {
        this.ligaComponent = ligaComponent;
    }


    /**
     * I return all klasse entries of the database.
     *
     * @return lost of {@link de.bogenliga.application.services.v1.liga.model.LigaDTO} as JSON
     */
    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermission(UserPermission.CAN_READ_DEFAULT)
    public List<LigaDTO> findAll() {
        final List<LigaDO> ligaDOList = ligaComponent.findAll();
        return ligaDOList.stream().map(LigaDTOMapper.toDTO).collect(Collectors.toList());
    }


    /**
     * Returns a liga entry of the given id
     *
     * @param id id of the klasse to be returned
     *
     * @return returns a klasse
     */
    @RequestMapping(
            value = "{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermission(UserPermission.CAN_READ_DEFAULT)
    public LigaDTO findById(@PathVariable("id") final long id) {
        Preconditions.checkArgument(id >= 0, PRECONDITION_MSG_LIGA_ID);

        logger.debug("Receive 'findById' request with ID '{}'", id);

        final LigaDO ligaDO = ligaComponent.findById(id);

        return LigaDTOMapper.toDTO.apply(ligaDO);
    }


    /**
     * I persist a new liga and return this liga entry
     *
     * @param ligaDTO
     * @param principal
     *
     * @return list of {@link LigaDTO} as JSON
     */
    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermission(UserPermission.CAN_MODIFY_STAMMDATEN)
    public LigaDTO create(@RequestBody final LigaDTO ligaDTO, final Principal principal) {
        logger.debug(
                "Receive 'create' request with ligaId '{}', ligaName '{}', regionId '{}', ligaUebergeordnetId '{}', verantwortlichId '{}' ",
                ligaDTO.getId(),
                ligaDTO.getName(),
                ligaDTO.getRegionId(),
                ligaDTO.getLigaUebergeordnetId(),
                ligaDTO.getLigaVerantwortlichId());

        checkPreconditions(ligaDTO);

        final LigaDO newLigaDO = LigaDTOMapper.toDO.apply(ligaDTO);
        final long currentDsbMitglied = UserProvider.getCurrentUserId(principal);

        final LigaDO savedLigaDO = ligaComponent.create(newLigaDO,
                currentDsbMitglied);
        return LigaDTOMapper.toDTO.apply(savedLigaDO);
    }


    /**
     * I persist a newer version of the CompetitionClass in the database.
     */
    @RequestMapping(method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermission(UserPermission.CAN_MODIFY_STAMMDATEN)
    public LigaDTO update(@RequestBody final LigaDTO ligaDTO,
                          final Principal principal) {

        logger.debug(
                "Receive 'create' request with ligaId '{}', ligaName '{}', regionId '{}', ligaUebergeordnetId '{}', verantwortlichId '{}' ",
                ligaDTO.getId(),
                ligaDTO.getName(),
                ligaDTO.getRegionId(),
                ligaDTO.getLigaUebergeordnetId(),
                ligaDTO.getLigaVerantwortlichId());


        final LigaDO newLigaDO = LigaDTOMapper.toDO.apply(ligaDTO);
        final long currentDsbMitglied = UserProvider.getCurrentUserId(principal);

        final LigaDO updatedLigaDO = ligaComponent.update(newLigaDO,
                currentDsbMitglied);
        return LigaDTOMapper.toDTO.apply(updatedLigaDO);

    }

    /**
     * I delete an existing Liga entry from the DB.
     */
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @RequiresPermission(UserPermission.CAN_DELETE_STAMMDATEN)
    public void delete (@PathVariable("id") final Long id, final Principal principal){
        Preconditions.checkArgument(id >= 0, "ID must not be negative.");

        logger.debug("Receive 'delete' request with id '{}'", id);

        final LigaDO ligaDO = new LigaDO(id);
        final long userId = UserProvider.getCurrentUserId(principal);
        ligaComponent.delete(ligaDO,userId);
    }


    private void checkPreconditions(@RequestBody final LigaDTO ligaDTO) {
        Preconditions.checkNotNull(ligaDTO, PRECONDITION_MSG_LIGA);
        Preconditions.checkNotNull(ligaDTO.getRegionId(), PRECONDITION_MSG_LIGA_REGION);

        Preconditions.checkArgument(ligaDTO.getRegionId() >= 0, PRECONDITION_MSG_LIGA_REGION_ID_NEG);

        // These are not mandatory fields. Only check if filled.
        if (ligaDTO.getLigaUebergeordnetId() != null) {
            Preconditions.checkArgument(ligaDTO.getLigaUebergeordnetId() >= 0,
                    PRECONDITION_MSG_LIGA_UEBERGEORDNET_ID_NEG);
        } else if (ligaDTO.getLigaVerantwortlichId() != null) {
            Preconditions.checkArgument(ligaDTO.getLigaVerantwortlichId() >= 0,
                    PRECONDITION_MSG_LIGA_VERANTWORTLICH_ID_NEG);
        }
    }
}
