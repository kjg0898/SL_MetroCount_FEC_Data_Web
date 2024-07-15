package org.neighbor21.sl_metrocount_fec_data_web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.neighbor21.sl_metrocount_fec_data_web.dto.individualVehicles.IndividualVehiclesDTO;
import org.neighbor21.sl_metrocount_fec_data_web.dto.listSite.ListSiteDTO;
import org.neighbor21.sl_metrocount_fec_data_web.service.conversion.SiteService;
import org.neighbor21.sl_metrocount_fec_data_web.service.conversion.SurveyPeriodService;
import org.neighbor21.sl_metrocount_fec_data_web.service.conversion.VehiclePassService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * packageName    : org.neighbor21.sl_metrocount_fec_data_web.service
 * fileName       : FileProcessingService.java
 * author         : kjg08
 * date           : 2024-07-15
 * description    : 파일 처리 서비스를 담당하는 클래스입니다.
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 7.15.        kjg08           최초 생성
 */
@Service
public class FileProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(FileProcessingService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SiteService siteService;

    @Autowired
    private VehiclePassService vehiclePassService;

    @Autowired
    private SurveyPeriodService surveyPeriodService;

    /**
     * 파일 배열을 처리하는 메서드
     * @param files 업로드된 파일 배열
     * @throws IOException 파일 처리 중 발생할 수 있는 예외
     */
    public void processFiles(MultipartFile[] files) throws IOException {
        for (MultipartFile file : files) {
            processFile(file);
        }
    }

    /**
     * 개별 파일을 처리하는 메서드
     * @param file 업로드된 파일
     * @throws IOException 파일 처리 중 발생할 수 있는 예외
     */
    public void processFile(MultipartFile file) throws IOException {
        logger.info("Processing file: {}", file.getOriginalFilename());
        // 파일의 내용을 한 줄씩 읽기 위해 BufferedReader,readLine 을 사용합니다.
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line.trim());
            }

            String jsonString = jsonBuilder.toString();
            logger.debug("Parsed JSON string: {}", jsonString);

            if (jsonString.contains("site_id")) {
                ListSiteDTO siteDTO = objectMapper.readValue(jsonString, ListSiteDTO.class);
                saveListSite(siteDTO);
            } else if (jsonString.contains("survey_period")) {
                ListSiteDTO surveyDTO = objectMapper.readValue(jsonString, ListSiteDTO.class);
                saveSurveyPeriod(surveyDTO);
            } else {
                IndividualVehiclesDTO vehicleDTO = objectMapper.readValue(jsonString, IndividualVehiclesDTO.class);
                saveIndividualVehicle(vehicleDTO);
            }
        }
    }

    private void saveListSite(ListSiteDTO listSite) {
        logger.info("Saving list site: {}", listSite.getName());
        List<ListSiteDTO> listSites = new ArrayList<>();
        listSites.add(listSite);
        siteService.saveSiteLogs(listSites);
    }

    private void saveSurveyPeriod(ListSiteDTO surveyPeriod) {
        logger.info("Saving survey period for site ID: {}", surveyPeriod.getSite_id());
        List<ListSiteDTO> surveyPeriods = new ArrayList<>();
        surveyPeriods.add(surveyPeriod);
        surveyPeriodService.saveSurveyPeriods(surveyPeriods);
    }

    private void saveIndividualVehicle(IndividualVehiclesDTO vehicle) {
        logger.info("Saving individual vehicle with ID: {}", vehicle.getSiteId());
        List<IndividualVehiclesDTO> individualVehicles = new ArrayList<>();
        individualVehicles.add(vehicle);
        vehiclePassService.saveVehiclePasses(individualVehicles);
    }
}