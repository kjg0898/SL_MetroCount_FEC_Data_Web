package org.neighbor21.sl_metrocount_fec_data_web.service.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

/**
 * packageName    : org.neighbor21.sl_metrocount_fec_data_web.service.util
 * fileName       : VehicleUtils.java
 * author         : kjg08
 * date           : 2024-07-15
 * description    : 여러 가지 유틸리티 클래스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 7.15.        kjg08           최초 생성
 */

/**
 * 여러 가지 유틸리티 클래스
 */
public class VehicleUtils {

    private static final Logger logger = LoggerFactory.getLogger(VehicleUtils.class);

    /**
     * 두 차량의 Timestamp를 받아 차량 간 간격(초)을 계산합니다.
     *
     * @param currentTimestamp  현재 차량의 타임스탬프
     * @param previousTimestamp 이전 차량의 타임스탬프
     * @return 차량 간 간격(초)
     */
    public static int calculateIntervarSeconds(Timestamp currentTimestamp, Timestamp previousTimestamp) {
        if (previousTimestamp == null) {
            logger.debug("첫 번째 차량인 경우 간격은 0으로 설정");
            return 0;  // 첫 번째 차량인 경우 간격은 0으로 설정
        }
        long headwayMilliseconds = currentTimestamp.getTime() - previousTimestamp.getTime();
        logger.debug("차량 간 간격(밀리초): {}", headwayMilliseconds);
        return (int) (headwayMilliseconds / 1000);  // 밀리초를 초로 변환
    }

}