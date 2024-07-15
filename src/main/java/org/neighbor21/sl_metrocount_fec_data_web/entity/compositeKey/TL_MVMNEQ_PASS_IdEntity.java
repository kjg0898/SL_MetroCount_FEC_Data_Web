package org.neighbor21.sl_metrocount_fec_data_web.entity.compositeKey;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * packageName    : org.neighbor21.sl_metrocount_fec_data_web.entity.compositeKey
 * fileName       : TL_MVMNEQ_PASS_IdEntity.java
 * author         : kjg08
 * date           : 2024-07-15
 * description    : 이동형 장비 통과 차량 정보 엔티티의 복합 키를 정의하는 클래스입니다.
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 7.15.        kjg08           최초 생성
 */
@Getter
@Setter
@Embeddable
@EqualsAndHashCode
public class TL_MVMNEQ_PASS_IdEntity implements Serializable {
    // 통행 일시
    @Column(name = "PASS_DT")
    private Timestamp passTime;

    // 차량 방향
    @Column(name = "VHCL_DRCT", length = 30)
    private String vehicleDirection;

    // 통행 차로
    @Column(name = "PASS_LANE", precision = 10, scale = 0)
    private Integer passLane;

    // 설치 위치 아이디
    @Column(name = "INSTLLC_ID", length = 32)
    private String instllcId;


    // Default constructor
    public TL_MVMNEQ_PASS_IdEntity() {
    }

    // Constructor
    public TL_MVMNEQ_PASS_IdEntity(Timestamp passTime, String vehicleDirection, int passLane, String instllcId) {
        this.passTime = passTime;
        this.vehicleDirection = vehicleDirection;
        this.passLane = passLane;
        this.instllcId = instllcId;
    }
}