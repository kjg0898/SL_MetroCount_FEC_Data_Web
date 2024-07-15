package org.neighbor21.sl_metrocount_fec_data_web.dto.listSite;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * packageName    : org.neighbor21.sl_metrocount_fec_data_web.dto.listSite
 * fileName       : SurveyPeriodDTO.java
 * author         : kjg08
 * date           : 2024-07-15
 * description    : 조사 기간 정보를 담은 DTO 클래스입니다.
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 7.15.        kjg08           최초 생성
 */

//ISO-8601 표준의 조사 시작 시간 및 종료 시간(YYYY-MM-DDTHH:MM:SS+tz 형식)
//interval_statistics의 시작 시간, 종료 시간 결정
//일시적으로 데이터 수집하는 장소의 경우 각 설문조사 기간마다 survey_periods 배열에 별도로 추가
//영구적으로 수집하는 장소는 survey_periods 배열에 해당하는 요소에 새 데이터가 들어올 때마다 설문조사 기간의 종료 시간 업데이트함
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyPeriodDTO {
    @JsonProperty("start_time")
    private String start_time;
    @JsonProperty("end_time")
    private String end_time;
}