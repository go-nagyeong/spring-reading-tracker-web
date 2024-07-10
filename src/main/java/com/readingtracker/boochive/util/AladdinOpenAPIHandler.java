package com.readingtracker.boochive.util;

import com.readingtracker.boochive.dto.AladdinAPIResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class AladdinOpenAPIHandler {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;

    public AladdinOpenAPIHandler(RestTemplate restTemplate,
                                 @Value("${open-api.aladin.url}") String baseUrl,
                                 @Value("${open-api.aladin.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public AladdinAPIResponseDto searchBooks(Integer page, String query, QueryType queryType) {
        URI url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/ItemSearch.aspx")
                .queryParam("ttbkey", apiKey)
                .queryParam("Query", query)                 // 검색어
                .queryParam("QueryType", queryType.get())   // 검색어 종류
                .queryParam("Start", page)                  // 검색결과 시작페이지
                .queryParam("MaxResults", 20)      // 검색결과 한 페이지당 최대 출력 개수 (20 고정)
//                .queryParam("Sort", query)                      // 정렬 순서 (기본값: 관련도)
                .queryParam("Cover", "Big")        // 표지 이미지 크기 ("Big" 고정)
                .queryParam("Output", "JS")        // 출력방법 ("JS" 고정)
                .queryParam("Version", "20131101") // 검색 API의 Version(날짜형식) ("20131101" 고정)
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        AladdinAPIResponseDto response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                AladdinAPIResponseDto.class
        ).getBody();

        return formatData(response);
    }

    /**
     * 응답 데이터 전처리 메서드
     */
    private AladdinAPIResponseDto formatData(AladdinAPIResponseDto data) {
        Integer totalCount = data.getTotalResults();
        if (totalCount > 0) {
            // 도서 검색 결과 개수 전처리
            data.setTotalResults(Math.min(totalCount, 200)); // (알라딘 API 주요사항 - 총 결과는 200개까지만 검색 가능)
            // 전체 페이지 수 계산 (= 총 결과 개수 / 페이지 당 개수)
            Integer totalPages = data.getTotalResults() / data.getItemsPerPage();
            data.setTotalPages(totalPages);
        } else {
            data.setTotalPages(1);
        }

        for (AladdinAPIResponseDto.Item item : data.getItem()) {
            // 저자 정보 전처리 (지은이, 옮긴이, 그림 분리)
            String authorWithOthers = item.getAuthor().replaceAll("\\s*\\(", "");

            if (authorWithOthers.contains(")")) {
                String[] authorWithOthersSplit = authorWithOthers.split("\\)");
                // 각 역할에 따라 처리
                for (String part : authorWithOthersSplit) {
                    if (part.contains("지은이")) {
                        item.setFormatAuthor(part.replaceAll("지은이", ""));
                    } else if (part.contains("옮긴이")) {
                        item.setTranslator(part.replaceAll(",\\s|옮긴이", ""));
                    }
                }
            }
        }

        return data;
    }
}
