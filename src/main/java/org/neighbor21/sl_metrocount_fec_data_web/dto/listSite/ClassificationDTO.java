package org.neighbor21.sl_metrocount_fec_data_web.dto.listSite;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * packageName    : org.neighbor21.sl_metrocount_fec_data_web.dto.listSite
 * fileName       : ClassificationDTO.java
 * author         : kjg08
 * date           : 2024-07-15
 * description    : 장소 목록  저장하는 DTO 클래스입니다.
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 7.15.        kjg08           최초 생성
 */
//class_scheme의 차량 클래스 목록
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassificationDTO {
    @JsonProperty("name")
    private String name;
}