package org.neighbor21.sl_metrocount_fec_data_web.service.conversion;

import org.neighbor21.sl_metrocount_fec_data_web.dto.individualVehicles.IndividualVehiclesDTO;
import org.neighbor21.sl_metrocount_fec_data_web.entity.TL_MVMNEQ_PASSEntity;
import org.neighbor21.sl_metrocount_fec_data_web.entity.compositeKey.TL_MVMNEQ_PASS_IdEntity;
import org.neighbor21.sl_metrocount_fec_data_web.service.BatchService;
import org.neighbor21.sl_metrocount_fec_data_web.service.util.VehicleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * packageName    : org.neighbor21.sl_metrocount_fec_data_web.service.conversion
 * fileName       : VehiclePassService.java
 * author         : kjg08
 * date           : 2024-07-15
 * description    : 차량 통과 정보 저장 서비스 클래스입니다.
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 7.15.        kjg08           최초 생성
 */
/**
 * 차량 통과 정보 저장 서비스 클래스입니다.
 */
@Service
public class VehiclePassService {

    private static final Logger logger = LoggerFactory.getLogger(VehiclePassService.class);

    private final Map<Integer, Timestamp> lastPassTimeMap = new ConcurrentHashMap<>();

    @Autowired
    private BatchService batchService;

    /**
     * 차량 통과 정보를 저장하는 메서드
     * @param vehicles 저장할 차량 정보 리스트
     */
    @Transactional
    public void saveVehiclePasses(List<IndividualVehiclesDTO> vehicles) {
        List<TL_MVMNEQ_PASSEntity> passEntities = new ArrayList<>();
        vehicles.forEach(vehicle -> {
            try {
                Integer siteId = vehicle.getSiteId();
                Timestamp lastPassTime = lastPassTimeMap.getOrDefault(siteId, new Timestamp(System.currentTimeMillis()));
                Timestamp currentTimestamp = new Timestamp(vehicle.getTimestamp().getTime());

                TL_MVMNEQ_PASSEntity tlMvmneqPassEntity = new TL_MVMNEQ_PASSEntity();
                TL_MVMNEQ_PASS_IdEntity passIdEntity = new TL_MVMNEQ_PASS_IdEntity(currentTimestamp, vehicle.getHeading(), vehicle.getLaneIndex(), siteId.toString());

                passIdEntity.setPassTime(currentTimestamp);
                passIdEntity.setVehicleDirection(vehicle.getHeading());
                passIdEntity.setPassLane(vehicle.getLaneIndex());
                passIdEntity.setInstllcId(siteId.toString());

                tlMvmneqPassEntity.setId(passIdEntity);
                tlMvmneqPassEntity.setVehicleSpeed(vehicle.getVelocity());
                tlMvmneqPassEntity.setVehicleLength(vehicle.getLength());
                tlMvmneqPassEntity.setVehicleIntervalSeconds(VehicleUtils.calculateIntervarSeconds(currentTimestamp, lastPassTime));
                tlMvmneqPassEntity.setVehicleClass(vehicle.getVehicleClass());
                tlMvmneqPassEntity.setCollectionDatetime(new Timestamp(System.currentTimeMillis()));

                lastPassTimeMap.put(siteId, currentTimestamp);

                passEntities.add(tlMvmneqPassEntity);
            } catch (Exception e) {
                logger.error("TL_MVMNEQ_PASS 처리 중 오류 발생", e);
            }
        });

        try {
            batchService.insertPassBatch(passEntities);
            logger.info("TL_MVMNEQ_PASS 배치 삽입 성공");
        } catch (Exception e) {
            logger.error("TL_MVMNEQ_PASS 배치 삽입 실패", e);
        }
    }
}