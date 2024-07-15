package org.neighbor21.sl_metrocount_fec_data_web.service;

import io.github.resilience4j.retry.Retry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.neighbor21.sl_metrocount_fec_data_web.config.Constants;
import org.neighbor21.sl_metrocount_fec_data_web.entity.TL_MVMNEQ_PASSEntity;
import org.neighbor21.sl_metrocount_fec_data_web.entity.TL_MVMNEQ_PERIODEntity;
import org.neighbor21.sl_metrocount_fec_data_web.entity.compositeKey.TL_MVMNEQ_PASS_IdEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * packageName    : org.neighbor21.sl_metrocount_fec_data_web.service
 * fileName       : BatchService.java
 * author         : kjg08
 * date           : 2024-07-15
 * description    : 배치 처리를 담당하는 서비스 클래스입니다.
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 7.15.        kjg08           최초 생성
 */
/**
 * 배치 처리를 담당하는 서비스 클래스입니다.
 */
@Service
@Transactional
public class BatchService {
    private static final Logger logger = LoggerFactory.getLogger(BatchService.class);
    private final TransactionTemplate transactionTemplate;

    @Autowired
    private DataSource dataSource;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private Retry dbRetry;

    @Autowired
    public BatchService(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /**
     * 배치 삽입을 시도하고, 실패 시 재시도하는 메서드
     * @param entities 삽입할 엔티티 리스트
     * @param persistFunction 엔티티를 영속화하는 함수
     * @param <T> 엔티티 타입
     */
    public <T> void batchInsertWithRetry(List<T> entities, Consumer<T> persistFunction) {
        int batchSize = Constants.DEFAULT_BATCH_SIZE;
        int totalRecords = entities.size();
        final int[] lastLoggedProgress = {0};

        transactionTemplate.executeWithoutResult(status -> {
            for (int i = 0; i < totalRecords; i += batchSize) {
                int end = Math.min(i + batchSize, totalRecords);
                List<T> batchList = entities.subList(i, end);

                if (!batchList.isEmpty()) {
                    try {
                        Retry.decorateRunnable(dbRetry, () -> {
                            for (T entity : batchList) {
                                try {
                                    persistFunction.accept(entity);
                                } catch (Exception e) {
                                    logger.error("Error persisting entity {}: {}", entity, e.getMessage(), e);
                                    throw e;
                                }
                            }
                            entityManager.flush();
                            entityManager.clear();
                        }).run();
                    } catch (Exception e) {
                        logger.error("Batch insert attempt failed at index {} to {}: {}", i, end, e.getMessage(), e);
                        handleBatchException(batchList, persistFunction);
                    }
                }
            }
        });
    }

    /**
     * 배치 처리 중 예외 발생 시 개별 엔티티를 처리하는 메서드
     * @param batchList 실패한 배치 리스트
     * @param persistFunction 엔티티를 영속화하는 함수
     * @param <T> 엔티티 타입
     */
    private <T> void handleBatchException(List<T> batchList, Consumer<T> persistFunction) {
        for (T entity : batchList) {
            try {
                persistFunction.accept(entity);
                entityManager.flush();
            } catch (Exception e) {
                logger.error("Failed to insert entity {}: {}", entity, e.getMessage());
            } finally {
                entityManager.clear();
            }
        }
    }

    /**
     * 배치 업데이트 예외를 로깅하는 메서드
     * @param bue 배치 업데이트 예외
     */
    private void logBatchUpdateException(BatchUpdateException bue) {
        SQLException nextException = bue.getNextException();
        while (nextException != null) {
            logger.error("Next SQLException: SQLState: {}, ErrorCode: {}, Message: {}",
                    nextException.getSQLState(),
                    nextException.getErrorCode(),
                    nextException.getMessage());
            nextException = nextException.getNextException();
        }
    }

    /**
     * 기간 엔티티를 배치 삽입하는 메서드
     * @param periodEntities 삽입할 기간 엔티티 리스트
     * @throws SQLException SQL 예외
     */
    public void insertPeriodeBatch(List<TL_MVMNEQ_PERIODEntity> periodEntities) throws SQLException {
        String sql = "INSERT INTO srlk.tl_mvmneq_period (clct_dt, sqno, instllc_id, start_dt, end_dt) VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (clct_dt, instllc_id, sqno) DO NOTHING";

        try (Connection connection = Retry.decorateCheckedSupplier(dbRetry, dataSource::getConnection).apply()) {
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (TL_MVMNEQ_PERIODEntity entity : periodEntities) {
                    statement.setTimestamp(1, entity.getId().getCollectionDatetime());
                    statement.setInt(2, entity.getId().getSequenceNo());
                    statement.setString(3, entity.getId().getInstllcId());
                    statement.setTimestamp(4, Timestamp.valueOf(entity.getStartTime()));
                    statement.setTimestamp(5, Timestamp.valueOf(entity.getEndTime()));
                    statement.addBatch();
                }
                statement.executeBatch();
                connection.commit();
                logger.info("Period batch insert successful");
            } catch (SQLException e) {
                connection.rollback();
                logBatchUpdateException((BatchUpdateException) e);
                throw e;
            }
        } catch (SQLException e) {
            logger.error("Failed to execute period batch insert", e);
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 통과 엔티티를 배치 삽입하는 메서드
     * @param passEntities 삽입할 통과 엔티티 리스트
     * @throws SQLException SQL 예외
     */
    public void insertPassBatch(List<TL_MVMNEQ_PASSEntity> passEntities) throws SQLException {
        String sql = "INSERT INTO srlk.tl_mvmneq_pass (pass_dt, vhcl_drct, pass_lane, instllc_id, vhcl_speed, vhcl_len, vhcl_intv_ss, vhcl_clsf, clct_dt) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (pass_dt, vhcl_drct, pass_lane, instllc_id) DO NOTHING";

        try (Connection connection = Retry.decorateCheckedSupplier(dbRetry, dataSource::getConnection).apply()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (TL_MVMNEQ_PASSEntity entity : passEntities) {
                    TL_MVMNEQ_PASS_IdEntity id = entity.getId();
                    statement.setTimestamp(1, id.getPassTime());
                    statement.setString(2, id.getVehicleDirection());
                    statement.setInt(3, id.getPassLane());
                    statement.setString(4, id.getInstllcId());
                    statement.setBigDecimal(5, entity.getVehicleSpeed());
                    statement.setBigDecimal(6, entity.getVehicleLength());
                    statement.setInt(7, entity.getVehicleIntervalSeconds());
                    statement.setString(8, entity.getVehicleClass());
                    statement.setTimestamp(9, entity.getCollectionDatetime());
                    statement.addBatch();
                }
                statement.executeBatch();
                connection.commit();
                logger.info("Pass batch insert successful");
            } catch (SQLException e) {
                connection.rollback();
                logBatchUpdateException((BatchUpdateException) e);
                throw e;
            }
        } catch (SQLException e) {
            logger.error("Failed to execute pass batch insert", e);
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}