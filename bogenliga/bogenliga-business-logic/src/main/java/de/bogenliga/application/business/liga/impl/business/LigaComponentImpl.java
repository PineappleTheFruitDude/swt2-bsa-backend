package de.bogenliga.application.business.liga.impl.business;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.bogenliga.application.business.liga.api.LigaComponent;
import de.bogenliga.application.business.liga.api.types.LigaDO;
import de.bogenliga.application.business.liga.impl.dao.LigaDAO;
import de.bogenliga.application.business.liga.impl.entity.LigaBE;
import de.bogenliga.application.business.liga.impl.mapper.LigaMapper;
import de.bogenliga.application.business.regionen.api.types.RegionenDO;
import de.bogenliga.application.business.regionen.impl.dao.RegionenDAO;
import de.bogenliga.application.business.regionen.impl.entity.RegionenBE;
import de.bogenliga.application.business.user.impl.dao.UserDAO;
import de.bogenliga.application.business.user.impl.entity.UserBE;
import de.bogenliga.application.common.errorhandling.ErrorCode;
import de.bogenliga.application.common.errorhandling.exception.BusinessException;
import de.bogenliga.application.common.validation.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;

public class LigaComponentImpl implements LigaComponent {

    private static final String PRECONDITION_MSG_LIGA = "ligaDO must not be Null";
    private static final String PRECONDITION_MSG_LIGA_ID = "ligaId must not be Null";
    private static final String PRECONDITION_MSG_LIGA_NAME = "ligaName must not be Null";
    private static final String PRECONDITION_MSG_REGION_ID = "ligaRegionId must not be Null";
    private static final String PRECONDITION_MSG_REGION_NAME = "ligaRegionName must not be Null";
    private static final String PRECONDITION_MSG_LIGA_UEBERGEORDNET_ID = "ligaUebergeordnetId must not be Null";
    private static final String PRECONDITION_MSG_LIGA_UEBERGEORDNET_NAME = "ligaUebergeordnetName must not be Null";
    private static final String PRECONDITION_MSG_LIGA_VERANTWORTLICH_ID = "ligaVerantwortlichId must not be Null";
    private static final String PRECONDITION_MSG_LIGA_VERANTWORTLICH_MAIL = "ligaVerantwortlichMail must not be Null";
    private static final String PRECONDITION_MSG_CURRENT_LIGA_ID = "Current liga id must not be Null";

    private final LigaDAO ligaDAO;
    private final RegionenDAO regionenDAO;
    private final UserDAO userDAO;

    @Autowired
    public LigaComponentImpl(final LigaDAO ligaDAO, final RegionenDAO regionenDAO, final UserDAO userDAO) {
        this.ligaDAO = ligaDAO;
        this.regionenDAO = regionenDAO;
        this.userDAO = userDAO;
    }

    @Override
    public List<LigaDO> findAll() {
        final ArrayList<LigaDO> returnList = new ArrayList<LigaDO>();
        final List<LigaBE> ligaBEList = ligaDAO.findAll();

        for (int i = 0; i < ligaBEList.size(); i++) {
            returnList.add(i, LigaMapper.toLigaDO(ligaBEList.get(i),
                    regionenDAO.findById(ligaBEList.get(i).getLigaRegionId()),
                    userDAO.findById(ligaBEList.get(i).getLigaUserId())));

        }
        return returnList;
    }


    @Override
    public LigaDO findById(long id) {

        Preconditions.checkArgument(id >= 0, PRECONDITION_MSG_LIGA_ID);

        final LigaBE result = ligaDAO.findById(id);

        if (result == null) {
            throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND_ERROR,
                    String.format("No result found for ID '%s'", id));
        }

        return LigaMapper.toLigaDO(result, regionenDAO.findById(id), userDAO.findById(id));
    }


    @Override
    public LigaDO create(LigaDO ligaDO, long currentDsbMitgliedId) {

        checkLigaDO(ligaDO, currentDsbMitgliedId);
        final LigaBE ligaBE = LigaMapper.toLigaBE.apply(ligaDO);
        final LigaBE persistedLigaBE = ligaDAO.create(ligaBE,currentDsbMitgliedId);
        final RegionenBE regionenBE = regionenDAO.findById(persistedLigaBE.getLigaRegionId());
        final UserBE userBE = userDAO.findById(persistedLigaBE.getLigaUserId());



        return LigaMapper.toLigaDO(persistedLigaBE,regionenBE,userBE);
    }


    @Override
    public LigaDO update(LigaDO ligaDO, long currentDsbMitgliedId) {
        checkLigaDO(ligaDO,currentDsbMitgliedId);
        Preconditions.checkArgument(ligaDO.getId() >= 0, PRECONDITION_MSG_LIGA_ID);

        final LigaBE ligaBE = LigaMapper.toLigaBE.apply(ligaDO);
        final LigaBE persistedLigaBE = ligaDAO.create(ligaBE,currentDsbMitgliedId);
        final RegionenBE regionenBE = regionenDAO.findById(persistedLigaBE.getLigaRegionId());
        final UserBE userBE = userDAO.findById(persistedLigaBE.getLigaUserId());

        return LigaMapper.toLigaDO(persistedLigaBE,regionenBE,userBE);
    }


    @Override
    public void delete(LigaDO ligaDO, long currentDsbMitgliedId) {
        Preconditions.checkNotNull(ligaDO, PRECONDITION_MSG_LIGA);
        Preconditions.checkArgument(ligaDO.getId() >= 0, PRECONDITION_MSG_LIGA_ID);
        Preconditions.checkArgument(currentDsbMitgliedId >= 0, PRECONDITION_MSG_CURRENT_LIGA_ID);

        final LigaBE ligaBE = LigaMapper.toLigaBE.apply(ligaDO);

        ligaDAO.delete(ligaBE,currentDsbMitgliedId);
    }

    private void checkLigaDO(final LigaDO ligaDO, final long currentDsbMitgliedId) {
        Preconditions.checkNotNull(ligaDO, PRECONDITION_MSG_LIGA);
        Preconditions.checkArgument(currentDsbMitgliedId >= 0, PRECONDITION_MSG_LIGA_ID);
        Preconditions.checkNotNull(ligaDO.getName(), PRECONDITION_MSG_LIGA_NAME);
        Preconditions.checkArgument(ligaDO.getRegion_id() >= 0, PRECONDITION_MSG_REGION_ID);
        Preconditions.checkNotNull(ligaDO.getRegion_name(), PRECONDITION_MSG_REGION_NAME);
        Preconditions.checkArgument(ligaDO.getLiga_uebergeordnet_id() >= 0, PRECONDITION_MSG_LIGA_UEBERGEORDNET_ID);
        Preconditions.checkNotNull(ligaDO.getLiga_uebergeordnet_name(), PRECONDITION_MSG_LIGA_UEBERGEORDNET_NAME);
        Preconditions.checkArgument(ligaDO.getLiga_verantwortlich_id() >= 0, PRECONDITION_MSG_LIGA_VERANTWORTLICH_ID);
        Preconditions.checkNotNull(ligaDO.getLiga_verantwortlich_mail(), PRECONDITION_MSG_LIGA_VERANTWORTLICH_MAIL);
    }
}
