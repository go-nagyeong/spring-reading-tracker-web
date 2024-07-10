package com.readingtracker.bookarchive;

import com.readingtracker.boochive.dto.AladdinAPIResponseDto;
import com.readingtracker.boochive.util.AladdinOpenAPIHandler;
import com.readingtracker.boochive.util.QueryType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@SpringBootTest(classes = OpenApiTest.class)
@Slf4j
public class OpenApiTest {

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private Environment environment;

    @Test
    void testSearchBooks() {
        log.info("testSearchBooks");

        // 환경 변수로부터 URL과 Key를 가져옵니다.
        String apiUrl = environment.getProperty("open-api.aladin.url");
        String apiKey = environment.getProperty("open-api.aladin.key");

        // AladdinOpenAPIHandler 인스턴스를 생성합니다.
        AladdinOpenAPIHandler apiHandler = new AladdinOpenAPIHandler(restTemplate, apiUrl, apiKey);

        // 검색 결과를 가져옵니다.
        AladdinAPIResponseDto result = apiHandler.searchBooks(1, "배움", QueryType.TITLE);

        // 로그에 결과를 출력합니다.
        log.info(result.getStartIndex().toString());
        log.info(result.getTotalResults().toString());
        log.info(result.getItemsPerPage().toString());
        log.info(result.getTotalPages().toString());
        result.getItem().forEach(item -> {
            log.info(item.getAuthor());
            log.info(item.getPubDate());
        });
    }
}
