package org.neighbor21.sl_metrocount_fec_data_web.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.neighbor21.sl_metrocount_fec_data_web.entity.compositeKey.TL_MVMNEQ_PERIOD_IdEntity;

import java.time.LocalDateTime;

/**
 * packageName    : org.neighbor21.sl_metrocount_fec_data_web.entity
 * fileName       : TL_MVMNEQ_PERIODEntity.java
 * author         : kjg08
 * date           : 2024-07-15
 * description    : 이동형 장비 조사 기간 정보 엔티티 클래스입니다.
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 7.15.        kjg08           최초 생성
 */
@Entity
@Getter
@Setter
@Table(name = "TL_MVMNEQ_PERIOD", schema = "srlk")
public class TL_MVMNEQ_PERIODEntity {

    @EmbeddedId
    private TL_MVMNEQ_PERIOD_IdEntity id;  // 사용할 복합 키

    // 시작 일시
    @Column(name = "START_DT")
    private LocalDateTime startTime;
    // 종료 일시
    @Column(name = "END_DT")
    private LocalDateTime endTime;
}
