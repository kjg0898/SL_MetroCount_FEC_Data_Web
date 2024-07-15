package org.neighbor21.sl_metrocount_fec_data_web.service.conversion;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.neighbor21.sl_metrocount_fec_data_web.dto.listSite.ListSiteDTO;
import org.neighbor21.sl_metrocount_fec_data_web.entity.TL_MVMNEQ_CUREntity;
import org.neighbor21.sl_metrocount_fec_data_web.entity.TL_MVMNEQ_LOGEntity;
import org.neighbor21.sl_metrocount_fec_data_web.entity.compositeKey.TL_MVMNEQ_CUR_IdEntity;
import org.neighbor21.sl_metrocount_fec_data_web.entity.compositeKey.TL_MVMNEQ_LOG_IdEntity;
import org.neighbor21.sl_metrocount_fec_data_web.service.BatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * packageName    : org.neighbor21.sl_metrocount_fec_data_web.service.conversion
 * fileName       : SiteService.java
 * author         : kjg08
 * date           : 2024-07-15
 * description    : 사이트 로그 저장 서비스 클래스입니다.
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 7.15.        kjg08           최초 생성
 */
/**
 * 사이트 로그 저장 서비스 클래스입니다.
 */
@Service
public class SiteService {

    private static final Logger logger = LoggerFactory.getLogger(SiteService.class);

    @Autowired
    private BatchService batchService;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 사이트 로그를 저장하는 메서드
     * @param locations 저장할 위치 정보 리스트
     * @return 저장된 엔티티 수
     */
    @Transactional
    public int saveSiteLogs(List<ListSiteDTO> locations) {
        List<TL_MVMNEQ_CUREntity> curEntities = new ArrayList<>();
        List<TL_MVMNEQ_LOGEntity> logEntities = new ArrayList<>();

        for (ListSiteDTO location : locations) {
            try {
                TL_MVMNEQ_CUREntity curEntity = createCurrentEntity(location);
                curEntities.add(curEntity);

                TL_MVMNEQ_LOGEntity logEntity = createLogEntity(location);
                logEntities.add(logEntity);
            } catch (Exception e) {
                logger.error("TL_MVMNEQ_CUR/LOG 처리 중 오류 발생", e);
            }
        }

        try {
            batchService.batchInsertWithRetry(curEntities, entityManager::persist);
            entityManager.flush();
            entityManager.clear();
            logger.info("TL_MVMNEQ_CUR 배치 삽입 성공");
        } catch (Exception e) {
            logger.error("TL_MVMNEQ_CUR 배치 삽입 실패", e);
        }

        try {
            batchService.batchInsertWithRetry(logEntities, entityManager::persist);
            entityManager.flush();
            entityManager.clear();
            logger.info("TL_MVMNEQ_LOG 배치 삽입 성공");
        } catch (Exception e) {
            logger.error("TL_MVMNEQ_LOG 배치 삽입 실패", e);
        }
        return curEntities.size() + logEntities.size();
    }

    private TL_MVMNEQ_CUREntity createCurrentEntity(ListSiteDTO location) {
        TL_MVMNEQ_CUREntity tlMvmneqCurEntity = new TL_MVMNEQ_CUREntity();
        TL_MVMNEQ_CUR_IdEntity newId = new TL_MVMNEQ_CUR_IdEntity(location.getSite_id().toString());

        tlMvmneqCurEntity.setInstllcId(newId);
        tlMvmneqCurEntity.setInstllcNm(location.getName());
        tlMvmneqCurEntity.setInstllcDescr(location.getDescription());
        tlMvmneqCurEntity.setLatitude(BigDecimal.valueOf(location.getLatitude()));
        tlMvmneqCurEntity.setLongitude(BigDecimal.valueOf(location.getLongitude()));
        tlMvmneqCurEntity.setEqpmntId(location.getAsset_management_id());
        tlMvmneqCurEntity.setCollectionDatetime(new Timestamp(System.currentTimeMillis()));

        logger.debug("Created TL_MVMNEQ_CUR entity: {}", tlMvmneqCurEntity);
        return tlMvmneqCurEntity;
    }

    private TL_MVMNEQ_LOGEntity createLogEntity(ListSiteDTO location) {
        TL_MVMNEQ_LOGEntity tlMvmneqLogEntity = new TL_MVMNEQ_LOGEntity();
        TL_MVMNEQ_LOG_IdEntity logIdEntity = new TL_MVMNEQ_LOG_IdEntity();

        logIdEntity.setCollectionDatetime(new Timestamp(System.currentTimeMillis()));
        logIdEntity.setInstllcId(location.getSite_id().toString());
        tlMvmneqLogEntity.setId(logIdEntity);

        tlMvmneqLogEntity.setInstllcNm(location.getName());
        tlMvmneqLogEntity.setInstllcDescr(location.getDescription());
        tlMvmneqLogEntity.setEqpmntId(location.getAsset_management_id());
        tlMvmneqLogEntity.setLatitude(BigDecimal.valueOf(location.getLatitude()));
        tlMvmneqLogEntity.setLongitude(BigDecimal.valueOf(location.getLongitude()));

        logger.debug("Created TL_MVMNEQ_LOG entity: {}", tlMvmneqLogEntity);
        return tlMvmneqLogEntity;
    }
}