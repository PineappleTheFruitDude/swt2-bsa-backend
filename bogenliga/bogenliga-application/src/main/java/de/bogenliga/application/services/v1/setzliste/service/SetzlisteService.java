package de.bogenliga.application.services.v1.setzliste.service;

import de.bogenliga.application.business.Setzliste.api.SetzlisteComponent;
import de.bogenliga.application.business.Setzliste.api.types.SetzlisteDO;
import de.bogenliga.application.business.dsbmitglied.api.types.DsbMitgliedDO;
import de.bogenliga.application.common.service.ServiceFacade;
import de.bogenliga.application.common.service.UserProvider;
import de.bogenliga.application.common.validation.Preconditions;
import de.bogenliga.application.services.v1.dsbmitglied.mapper.DsbMitgliedDTOMapper;
import de.bogenliga.application.services.v1.dsbmitglied.model.DsbMitgliedDTO;
import de.bogenliga.application.services.v1.setzliste.mapper.SetzlisteDTOMapper;
import de.bogenliga.application.services.v1.setzliste.model.SetzlisteDTO;
import de.bogenliga.application.springconfiguration.security.permissions.RequiresPermission;
import de.bogenliga.application.springconfiguration.security.types.UserPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * I´m a REST resource and handle dsbMitglied CRUD requests over the HTTP protocol.
 *
 * @author Andre Lehnert, eXXcellent solutions consulting & software gmbh
 * @see <a href="https://en.wikipedia.org/wiki/Create,_read,_update_and_delete">Wikipedia - CRUD</a>
 * @see <a href="https://en.wikipedia.org/wiki/Representational_state_transfer">Wikipedia - REST</a>
 * @see <a href="https://en.wikipedia.org/wiki/Hypertext_Transfer_Protocol">Wikipedia - HTTP</a>
 * @see <a href="https://en.wikipedia.org/wiki/Design_by_contract">Wikipedia - Design by contract</a>
 * @see <a href="https://spring.io/guides/gs/actuator-service/">
 * Building a RESTful Web Service with Spring Boot Actuator</a>
 * @see <a href="https://www.baeldung.com/building-a-restful-web-service-with-spring-and-java-based-dsbMitglied">
 * Build a REST API with Spring 4 and Java Config</a>
 * @see <a href="https://www.baeldung.com/spring-autowire">Guide to Spring @Autowired</a>
 */
@RestController
@CrossOrigin
@RequestMapping("v1/setzliste")
public class SetzlisteService implements ServiceFacade {

    private static final String PRECONDITION_MSG_DSBMITGLIED = "DsbMitgliedDO must not be null";
    private static final String PRECONDITION_MSG_DSBMITGLIED_ID = "DsbMitgliedDO ID must not be negative";
    private static final String PRECONDITION_MSG_DSBMITGLIED_VORNAME = "DsbMitglied vorname must not be null";
    private static final String PRECONDITION_MSG_DSBMITGLIED_NACHNAME = "DsbMitglied nachname must not be null";
    private static final String PRECONDITION_MSG_DSBMITGLIED_GEBURTSDATUM = "DsbMitglied geburtsdatum must not be null";
    private static final String PRECONDITION_MSG_DSBMITGLIED_NATIONALITAET = "DsbMitglied nationalitaet must not be null";
    private static final String PRECONDITION_MSG_DSBMITGLIED_MITGLIEDSNUMMER = "DsbMitglied mitgliedsnummer must not be null";
    private static final String PRECONDITION_MSG_DSBMITGLIED_VEREIN_ID = "DsbMitglied vereins id must not be null";
    private static final String PRECONDITION_MSG_DSBMITGLIED_VEREIN_ID_NEGATIVE = "DsbMitglied vereins id must not be negative";

    private static final Logger LOG = LoggerFactory.getLogger(SetzlisteService.class);

    /*
     * Business components
     *
     * dependency injection with {@link Autowired}
     */
    private final SetzlisteComponent setzlisteComponent;


    /**
     * Constructor with dependency injection
     *
     */
    @Autowired
    public SetzlisteService(final SetzlisteComponent setzlisteComponent) {
        this.setzlisteComponent = setzlisteComponent;
    }


    /**
     * I return the dsbMitglied entry of the database with a specific id.
     *
     * Usage:
     * <pre>{@code Request: GET /v1/dsbmitglied/app.bogenliga.frontend.autorefresh.active}</pre>
     * <pre>{@code Response:
     *  {
     *    "id": "app.bogenliga.frontend.autorefresh.active",
     *    "value": "true"
     *  }
     * }
     * </pre>
     *
     * @return list of as JSON
     */
    //, "wettkampftag" }
    //, @RequestParam("wettkampftag") String wettkampftag)
    //   @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(method = RequestMethod.POST)
    @RequiresPermission(UserPermission.CAN_READ_SYSTEMDATEN)
    public ResponseEntity<InputStreamResource> getTableByVars(@RequestParam Map<String,String> requestParams) {
        final List<SetzlisteDO> setzlisteDOList = setzlisteComponent.getTable();
        LOG.debug("setzliste works...");
        Resource resource = new ClassPathResource("tableForDennis.pdf");
        long r = 0;
        InputStream is=null;

        try {
            is = resource.getInputStream();
            r = resource.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().contentLength(r)
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(new InputStreamResource(is));
    }

/*    @RequestMapping(method = RequestMethod.POST)
    @RequiresPermission(UserPermission.CAN_READ_SYSTEMDATEN)
    public String getTableByVars(@RequestParam Map<String,String> requestParams) {
        LOG.debug("Receive 'find byVars' request with wettkampf " + requestParams.get("wettkampf"));
        LOG.debug("Receive 'find byVars' request with wettkampftag  " + requestParams.get("wettkampftag"));
        //final DsbMitgliedDO dsbMitgliedDO = dsbMitgliedComponent.findById(id);
        return "/setzliste passt";
    }*/


    @RequestMapping(value = "/{tag}/{wettkampf}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermission(UserPermission.CAN_READ_SYSTEMDATEN)
    public String getTableByVars2(@PathVariable("tag") final String tag, @PathVariable("wettkampf") final String wettkampf) {
        LOG.debug("Receive 'find byVars' request with wettkampf " + tag);
        LOG.debug("Receive 'find byVars' request with wettkampftag  " + wettkampf);
        //final DsbMitgliedDO dsbMitgliedDO = dsbMitgliedComponent.findById(id);
        return "/setzliste passt";
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermission(UserPermission.CAN_MODIFY_SYSTEMDATEN)
    public String create(@RequestBody final Map<String, String> mybody, final Principal principal) {
        LOG.error(mybody.get("tag"));
        return "blaaaa";
    }

    /**
     * I return all dsbMitglied entries of the database.
     * TODO ACHTUNG: Darf wegen Datenschutz in dieser Form nur vom Admin oder auf Testdaten verwendet werden!
     *
     * Usage:
     * <pre>{@code Request: GET /v1/dsbmitglied}</pre>
     * <pre>{@code Response: TODO Beispielpayload bezieht sich auf Config, muss noch für DSBMitlgied angepasst werden
     * [
     *  {
     *    "id": "app.bogenliga.frontend.autorefresh.active",
     *    "value": "true"
     *  },
     *  {
     *    "id": "app.bogenliga.frontend.autorefresh.interval",
     *    "value": "10"
     *  }
     * ]
     * }
     * </pre>
     *
     * @return list of {@link SetzlisteDTO} as JSON
     */
    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermission(UserPermission.CAN_READ_SYSTEMDATEN)
    public String getTable() {
        LOG.warn("### Setzliste Service #####");
        final List<SetzlisteDO> setzlisteDOList = setzlisteComponent.getTable();
        return "Hello Setzliste!";
    }




//    public List<SetzlisteDTO> getTable() {
//        final List<SetzlisteDO> setzlisteDOList = setzlisteComponent.getTable();
//        return null;
//        //   return setzlisteDOList.stream().map(SetzlisteDTOMapper.toDTO).collect(Collectors.toList());
//    }
}