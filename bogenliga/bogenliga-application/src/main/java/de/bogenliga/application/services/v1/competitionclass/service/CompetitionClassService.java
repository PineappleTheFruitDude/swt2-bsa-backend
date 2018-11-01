package de.bogenliga.application.services.v1.competitionclass.service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import de.bogenliga.application.business.competitionclass.api.CompetitionClassComponent;
import de.bogenliga.application.business.competitionclass.api.types.CompetitionClassDO;
import de.bogenliga.application.common.service.ServiceFacade;
import de.bogenliga.application.common.service.UserProvider;
import de.bogenliga.application.common.validation.Preconditions;
import de.bogenliga.application.services.v1.competitionclass.mapper.CompetitionClassDTOMapper;
import de.bogenliga.application.services.v1.competitionclass.model.CompetitionClassDTO;
import de.bogenliga.application.springconfiguration.security.permissions.RequiresPermission;
import de.bogenliga.application.springconfiguration.security.types.UserPermission;

/**
 * I'm a REST resource and handle competition class CRUD requests over the HTTP protocol
 *
 * @author Giuseppe Ferrera, giuseppe.ferrera@student.reutlingen-university.de
 */

@RestController
@CrossOrigin
@RequestMapping("v1/competitionclass")
public class CompetitionClassService implements ServiceFacade {
    private static final String PRECONDITION_MSG_KLASSE = "CompetitionClass must not be null";
    private static final String PRECONDITION_MSG_KLASSE_ID = "CompetitionClass ID must not be negative";
    private static final String PRECONDITION_MSG_KLASSE_ALTER_MIN = "Minimum Age must not be negative";
    private static final String PRECONDITION_MSG_KLASSE_ALTER_MAX = "Max Age must be higher than Min Age and must not be negative";
    private static final String PRECONDITION_MSG_KLASSE_NR = "Something is wrong with the CompetitionClass Number";
    private static final String PRECONDITION_MSG_NAME = "The CompetitionClass must be given a name";


    private final Logger LOGGER = LoggerFactory.getLogger(CompetitionClassService.class);

    private final CompetitionClassComponent competitionClassComponent;


    /**
     * Constructor with dependency injection
     *
     * @param competitionClassComp to handle the database CRUD requests
     */
    @Autowired
    public CompetitionClassService(final CompetitionClassComponent competitionClassComp) {
        this.competitionClassComponent = competitionClassComp;
    }


    /**
     * I return all klasse entries of the database.
     *
     * @return lost of {@link CompetitionClassDTO} as JSON
     */
    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermission(UserPermission.CAN_MODIFY_STAMMDATEN)
    public List<CompetitionClassDTO> findAll() {
        final List<CompetitionClassDO> competitionClassDOList = competitionClassComponent.findAll();
        return competitionClassDOList.stream().map(CompetitionClassDTOMapper.toDTO).collect(Collectors.toList());
    }


    /**
     * I persist a newer version of the CompetitionClass in the database.
     */

    @RequestMapping(method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermission(UserPermission.CAN_MODIFY_SYSTEMDATEN)
    public CompetitionClassDTO update(final CompetitionClassDTO competitionClassDTO, final Principal principal){
    checkPreconditions(competitionClassDTO);

        LOGGER.debug("Receive 'create' request with  id '{}', name '{}', alter_Min '{}', alter_Max '{}', number '{}'",

                competitionClassDTO.getId(),
                competitionClassDTO.getKlasseName(),
                competitionClassDTO.getKlasseAlterMin(),
                competitionClassDTO.getKlasseAlterMax(),
                competitionClassDTO.getKlasseNr());


         final CompetitionClassDO newCompetitionClassDO = CompetitionClassDTOMapper.toDO.apply(competitionClassDTO);
         final long ClassId = UserProvider.getCurrentUserId(principal);

         final CompetitionClassDO updatedCompetitionClassDO = competitionClassComponent.update(newCompetitionClassDO,ClassId );
        return CompetitionClassDTOMapper.toDTO.apply(updatedCompetitionClassDO);

    }




    private void checkPreconditions(@RequestBody final CompetitionClassDTO competitionClassDTO){
        Preconditions.checkNotNull(competitionClassDTO, PRECONDITION_MSG_KLASSE);
        Preconditions.checkNotNull(competitionClassDTO.getId(), PRECONDITION_MSG_KLASSE_ID);
        Preconditions.checkNotNull(competitionClassDTO.getKlasseAlterMin(), PRECONDITION_MSG_KLASSE_ALTER_MIN);
        Preconditions.checkNotNull(competitionClassDTO.getKlasseAlterMax(), PRECONDITION_MSG_KLASSE_ALTER_MAX);
        Preconditions.checkNotNull(competitionClassDTO.getKlasseNr(), PRECONDITION_MSG_KLASSE_NR);
        Preconditions.checkNotNull(competitionClassDTO.getKlasseName(), PRECONDITION_MSG_NAME);

        Preconditions.checkArgument(competitionClassDTO.getId() < 0, PRECONDITION_MSG_KLASSE_ID);
        Preconditions.checkArgument(competitionClassDTO.getKlasseAlterMin() < 0, PRECONDITION_MSG_KLASSE_ALTER_MIN);
        Preconditions.checkArgument(competitionClassDTO.getKlasseAlterMin() > competitionClassDTO.getKlasseAlterMax(),PRECONDITION_MSG_KLASSE_ALTER_MIN);

    }
}
