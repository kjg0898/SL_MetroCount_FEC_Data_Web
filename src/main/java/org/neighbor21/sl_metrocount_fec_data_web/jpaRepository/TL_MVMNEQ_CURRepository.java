package org.neighbor21.sl_metrocount_fec_data_web.jpaRepository;

import org.neighbor21.sl_metrocount_fec_data_web.entity.TL_MVMNEQ_CUREntity;
import org.neighbor21.sl_metrocount_fec_data_web.entity.compositeKey.TL_MVMNEQ_CUR_IdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * packageName    : org.neighbor21.sl_metrocount_fec_data_web.jpaRepository
 * fileName       : TL_MVMNEQ_CURRepository.java
 * author         : kjg08
 * date           : 2024-07-15
 * description    : TL_MVMNEQ_CUR 엔티티를 위한 리포지토리 인터페이스입니다.
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 7.15.        kjg08           최초 생성
 */
public interface TL_MVMNEQ_CURRepository extends JpaRepository<TL_MVMNEQ_CUREntity, TL_MVMNEQ_CUR_IdEntity> {

}
