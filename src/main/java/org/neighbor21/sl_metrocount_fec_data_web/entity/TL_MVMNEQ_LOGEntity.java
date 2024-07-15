package org.neighbor21.sl_metrocount_fec_data_web.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.neighbor21.sl_metrocount_fec_data_web.entity.compositeKey.TL_MVMNEQ_LOG_IdEntity;

import java.math.BigDecimal;
/**
 * packageName    : org.neighbor21.sl_metrocount_fec_data_web.entity
 * fileName       : TL_MVMNEQ_LOGEntity.java
 * author         : kjg08
 * date           : 2024-07-15
 * description    : 이동형 장비 설치 위치 관리 이력 엔티티 클래스입니다.
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 7.15.        kjg08           최초 생성
 */
@Entity
@Getter
@Setter
@Table(name = "TL_MVMNEQ_LOG", schema = "srlk")
public class TL_MVMNEQ_LOGEntity {

    @EmbeddedId
    private TL_MVMNEQ_LOG_IdEntity id;  // 사용할 복합 키

    @Column(name = "INSTLLC_NM", length = 50)
    private String instllcNm;  // 설치위치 명

    @Column(name = "INSTLLC_DESCR", length = 400)
    private String instllcDescr;  // 설치위치 설명

    @Column(name = "EQPMNT_ID", length = 50)
    private String eqpmntId;  // 장비 아이디

    @Column(name = "LAT", precision = 14, scale = 8)
    private BigDecimal latitude;  // 위도

    @Column(name = "LON", precision = 14, scale = 8)
    private BigDecimal longitude;  // 경도
}
