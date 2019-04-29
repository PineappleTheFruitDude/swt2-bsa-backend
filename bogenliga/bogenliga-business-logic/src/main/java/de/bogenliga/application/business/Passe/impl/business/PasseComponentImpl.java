package de.bogenliga.application.business.Passe.impl.business;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import de.bogenliga.application.business.Passe.api.PasseComponent;
import de.bogenliga.application.business.Passe.api.types.PasseDO;
import de.bogenliga.application.business.Passe.impl.dao.PasseDAO;
import de.bogenliga.application.business.Passe.impl.entity.PasseBE;
import de.bogenliga.application.business.Passe.impl.mapper.PasseMapper;

/**
 * @author Kay Scheerer
 */
@Component
public class PasseComponentImpl implements PasseComponent {

    private static final String PRECONDITION_MSG_TEMPLATE = "Passe: %s must not be null and must not be negative";
    public static final String PRECONDITION_PASSEDO = "PasseDO must not be null";
    public static final String PRECONDITION_PASSE_MANNSCHAFT_ID = String.format(
            PRECONDITION_MSG_TEMPLATE, "mannschaftId"
    );
    public static final String PRECONDITION_DSBMITGLIED_MITGLIED_ID = String.format(
            PRECONDITION_MSG_TEMPLATE, "dsbMitgliedId"
    );
    public static final String PRECONDITION_PASSE_MANNSCHAFT_ID_NEGATIV = String.format(
            PRECONDITION_MSG_TEMPLATE, "MannschaftsmitgliedDO_Mannschaft_ID"
    );
    public static final String PRECONDITION_PASSE_DSB_MITGLIED_ID_NEGATIV = String.format(
            PRECONDITION_MSG_TEMPLATE, "PasseDO_Dsb_Mitglied_ID "
    );

    private final PasseDAO passeDAO;


    /**
     * Constructor
     * <p>
     * dependency injection with {@link Autowired}
     *
     * @param passeDAO to access the database and return passe representations
     */
    @Autowired
    public PasseComponentImpl(final PasseDAO passeDAO) {
        this.passeDAO = passeDAO;
    }


    /**
     * Return all passe entries.
     *
     * @return list of all passe in the database; empty list, if no passe is found
     */
    @Override
    public List<PasseDO> findAll() {
        final List<PasseBE> passeBEList = passeDAO.findAll();
        return passeBEList.stream().map(PasseMapper.toPasseDO).collect(Collectors.toList());

    }


    /**
     * Return all passe from one Wettkampf
     *
     * @param wettkampfId
     *
     * @return list of all passe from one Wettkampf in the database; empty list, if no passe are found
     */
    @Override
    public List<PasseDO> findByWettkampfId(Long wettkampfId) {
        final List<PasseBE> passeBEList = passeDAO.findByWettkampfId(wettkampfId);
        return passeBEList.stream().map(PasseMapper.toPasseDO).collect(Collectors.toList());
    }


    /**
     * Return all passe from one Wettkampf
     *
     * @param teamId of the mannschaft
     *
     * @return list of all passe from one Wettkampf in the database; empty list, if no passe are found
     */
    @Override
    public List<PasseDO> findByTeamId(Long teamId) {
        final List<PasseBE> passeBEList = passeDAO.findByTeamId(teamId);
        return passeBEList.stream().map(PasseMapper.toPasseDO).collect(Collectors.toList());
    }


    /**
     * Return all passe from one Wettkampf
     *
     * @param memberId of the member
     *
     * @return list of all passe from one Wettkampf in the database; empty list, if no passe are found
     */
    @Override
    public List<PasseDO> findByMemberId(Long memberId) {
        final List<PasseBE> passeBEList = passeDAO.findByMemberId(memberId);
        return passeBEList.stream().map(PasseMapper.toPasseDO).collect(Collectors.toList());
    }


    /**
     * Return a passe entry with the given ids.
     *
     * @param dsbMemberId  of the mannschaftsmitglied,
     * @param mannschaftId of the mannschaft
     *
     * @return list of passe from one mitglied in a mannschaft ; empty list, if no passe are found
     */
    @Override
    public List<PasseDO> findByMemberMannschaftId(Long dsbMemberId, Long mannschaftId) {
        final List<PasseBE> passeBEList = passeDAO.findByMemberMannschaftId(dsbMemberId, mannschaftId);
        return passeBEList.stream().map(PasseMapper.toPasseDO).collect(Collectors.toList());
    }


    /**
     * Return a passe entry with the given ids.
     *
     * @param dsbMemberId of the mannschaftsmitglied,
     * @param matchId     of the mannschaft
     *
     * @return list of passe from one mitglied in a mannschaft ; empty list, if no passe are found
     */
    @Override
    public List<PasseDO> findByMitgliedMatchId(Long dsbMemberId, Long matchId) {
        final List<PasseBE> passeBEList = passeDAO.findByMitgliedMatchId(dsbMemberId, matchId);
        return passeBEList.stream().map(PasseMapper.toPasseDO).collect(Collectors.toList());
    }


    /**
     * Return a passe entry with the given ids.
     *
     * @param mannschaftId of the mannschaftsmitglied,
     * @param matchId      of the match
     *
     * @return list of passe from one mannschaft in a match; empty list, if no passe are found
     */
    @Override
    public List<PasseDO> findByMannschaftMatchId(Long mannschaftId, Long matchId) {
        final List<PasseBE> passeBEList = passeDAO.findByMannschaftMatchId(mannschaftId, matchId);
        return passeBEList.stream().map(PasseMapper.toPasseDO).collect(Collectors.toList());
    }


    /**
     * Return a passe entry with the given ids.
     *
     * @param matchId of the match
     *
     * @return list of passe from one mannschaft in a match; empty list, if no passe are found
     */
    @Override
    public List<PasseDO> findByMatchId(Long matchId) {
        final List<PasseBE> passeBEList = passeDAO.findByMatchId(matchId);
        return passeBEList.stream().map(PasseMapper.toPasseDO).collect(Collectors.toList());
    }


    public PasseDO findByPk(Long wettkampfId, Long matchNr, Long mannschaftId, Long passeLfdNr, Long dsbMitgliedId) {
        final PasseBE passeBE = passeDAO.findByPk(wettkampfId, matchNr, mannschaftId, passeLfdNr, dsbMitgliedId);
        return PasseMapper.toPasseDO.apply(passeBE);
    }


    /**
     * Create a new passe in the database.
     *
     * @param passeDO       new passeDO
     * @param currentUserId the current user creating
     *
     * @return persisted version of the passe
     */
    @Override
    public PasseDO create(PasseDO passeDO, final Long currentUserId) {
        final PasseBE passeBE = new PasseBE();
        passeBE.setCreatedByUserId(passeDO.getCreatedByUserId());

        passeBE.setPfeil1(passeDO.getPfeil1());
        passeBE.setPfeil2(passeDO.getPfeil2());
        passeBE.setPfeil3(passeDO.getPfeil3());
        passeBE.setPfeil4(passeDO.getPfeil4());
        passeBE.setPfeil5(passeDO.getPfeil5());
        passeBE.setPfeil6(passeDO.getPfeil6());
        final PasseBE persistedPasseBE = passeDAO.create(passeBE, currentUserId);
        return PasseMapper.toPasseDO.apply(persistedPasseBE);
    }


    /**
     * Update an existing passe. The passe is identified by the id's set in passeDO.
     *
     * @param passeDO         existing passeDO to update
     * @param currentMemberId id of the member currently updating the passe
     *
     * @return persisted version of the passe
     */
    @Override
    public PasseDO update(PasseDO passeDO, Long currentMemberId) {
        return null;
    }


    /**
     * Delete an existing passe. The passe is identified by the id's set in passeDO.
     *
     * @param passeDO         passe to delete
     * @param currentMemberId id of the member currently updating the passe
     */
    @Override
    public void delete(PasseDO passeDO, Long currentMemberId) {

    }
}