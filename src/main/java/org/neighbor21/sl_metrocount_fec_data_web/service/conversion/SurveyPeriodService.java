package org.neighbor21.sl_metrocount_fec_data_web.service.conversion;

import org.neighbor21.sl_metrocount_fec_data_web.dto.listSite.ListSiteDTO;
import org.neighbor21.sl_metrocount_fec_data_web.dto.listSite.SurveyPeriodDTO;
import org.neighbor21.sl_metrocount_fec_data_web.entity.TL_MVMNEQ_PERIODEntity;
import org.neighbor21.sl_metrocount_fec_data_web.entity.compositeKey.TL_MVMNEQ_PERIOD_IdEntity;
import org.neighbor21.sl_metrocount_fec_data_web.jpaRepository.TL_MVMNEQ_PERIODRepository;
import org.neighbor21.sl_metrocount_fec_data_web.service.BatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * packageName    : org.neighbor21.sl_metrocount_fec_data_web.service.conversion
 * fileName       : SurveyPeriodService.java
 * author         : kjg08
 * date           : 2024-07-15
 * description    : 조사 기간 저장 서비스 클래스입니다.
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 7.15.        kjg08           최초 생성
 */
/**
 * 조사 기간 저장 서비스 클래스입니다.
 */
@Service
public class SurveyPeriodService {

    private static final Logger logger = LoggerFactory.getLogger(SurveyPeriodService.class);

    @Autowired
    private TL_MVMNEQ_PERIODRepository tlMvmneqPeriodRepository;

    @Autowired
    private BatchService batchService;

    /**
     * 조사 기간을 저장하는 메서드
     * @param periods 저장할 조사 기간 리스트
     * @return 저장된 엔티티 수
     */
    @Transactional
    public int saveSurveyPeriods(List<ListSiteDTO> periods) {
        List<TL_MVMNEQ_PERIODEntity> periodEntities = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<String> instllcIds = periods.stream()
                .map(period -> period.getSite_id().toString())
                .distinct()
                .collect(Collectors.toList());

        Map<String, Integer> maxSequenceMap = findMaxSequenceNoByInstllcIdsWithLogging(instllcIds);

        for (ListSiteDTO period : periods) {
            List<SurveyPeriodDTO> surveyPeriods = period.getSurvey_periods().stream()
                    .sorted(Comparator.comparing(SurveyPeriodDTO::getStart_time))
                    .collect(Collectors.toList());

            String instllcId = period.getSite_id().toString();
            Integer currentMaxSequence = maxSequenceMap.getOrDefault(instllcId, 0);

            for (int i = 0; i < surveyPeriods.size(); i++) {
                SurveyPeriodDTO periodDTO = surveyPeriods.get(i);
                TL_MVMNEQ_PERIODEntity periodEntity = new TL_MVMNEQ_PERIODEntity();
                TL_MVMNEQ_PERIOD_IdEntity periodIdEntity = new TL_MVMNEQ_PERIOD_IdEntity();

                periodIdEntity.setCollectionDatetime(new Timestamp(System.currentTimeMillis()));
                periodIdEntity.setSequenceNo(currentMaxSequence + i + 1);
                periodIdEntity.setInstllcId(instllcId);

                String startTimeStr = periodDTO.getStart_time().replace("T", " ");
                String endTimeStr = periodDTO.getEnd_time().replace("T", " ");

                try {
                    LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
                    LocalDateTime endTime = LocalDateTime.parse(endTimeStr, formatter);

                    periodEntity.setId(periodIdEntity);
                    periodEntity.setStartTime(Timestamp.valueOf(startTime).toLocalDateTime());
                    periodEntity.setEndTime(Timestamp.valueOf(endTime).toLocalDateTime());

                    periodEntities.add(periodEntity);
                } catch (DateTimeParseException e) {
                    logger.error("날짜를 파싱하지 못했습니다: 시작 시간 - {}, 종료 시간 - {}", startTimeStr, endTimeStr, e);
                    throw e;
                }
            }
        }

        try {
            batchService.insertPeriodeBatch(periodEntities);
            logger.info("TL_MVMNEQ_PERIOD 배치 삽입 성공");
        } catch (Exception e) {
            logger.error("TL_MVMNEQ_PERIOD 배치 삽입 실패", e);
        }

        return periodEntities.size();
    }

    /**
     * 설치 ID 리스트로부터 최대 시퀀스 번호를 찾는 메서드
     * @param instllcIds 설치 ID 리스트
     * @return 최대 시퀀스 번호를 담은 맵
     */
    public Map<String, Integer> findMaxSequenceNoByInstllcIdsWithLogging(List<String> instllcIds) {
        Map<String, Integer> maxSequenceMap = new HashMap<>();

        int batchSize = 10000;
        for (int i = 0; i < instllcIds.size(); i += batchSize) {
            int end = Math.min(i + batchSize, instllcIds.size());
            List<String> batch = instllcIds.subList(i, end);
            List<Object[]> results = tlMvmneqPeriodRepository.findMaxSequenceNoByInstllcIds(batch);

            for (Object[] result : results) {
                maxSequenceMap.put((String) result[0], ((Number) result[1]).intValue());
            }
        }

        logger.debug("Max sequence map: {}", maxSequenceMap);
        return maxSequenceMap;
    }
}