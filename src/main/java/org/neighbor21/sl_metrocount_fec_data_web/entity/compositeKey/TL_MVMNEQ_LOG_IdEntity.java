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
 * fileName       : TL_MVMNEQ_LOG_IdEntity.java
 * author         : kjg08
 * date           : 2024-07-15
 * description    : 이동형 장비 설치 위치 관리 이력 엔티티의 복합 키를 정의하는 클래스입니다.
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 7.15.        kjg08           최초 생성
 */

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
public class TL_MVMNEQ_LOG_IdEntity implements Serializable {

    // 수집 일시 (now)
    @Column(name = "CLCT_DT")
    private Timestamp collectionDatetime;

    @Column(name = "INSTLLC_ID", length = 32) // 설치위치 아이디
    private String instllcId;

    // Default constructor
    public TL_MVMNEQ_LOG_IdEntity() {
    }


    // Constructor
    public TL_MVMNEQ_LOG_IdEntity(Timestamp collectionDatetime, String instllcId) {
        this.collectionDatetime = collectionDatetime;
        this.instllcId = instllcId;
    }
}
