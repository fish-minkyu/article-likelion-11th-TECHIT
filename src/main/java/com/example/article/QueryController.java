package com.example.article;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class QueryController {
  private final ArticleService service;

  // Query Parameter
  // 어떠한 기준을 받을 것인지 RequestParam으로 넣어줄 수 있다.
  // GET /query-example?query=keyword&limit=20 HTTP/1.1
  @GetMapping("/query-example")
  public String queryParams(
    @RequestParam("query")
    String query,
    // 받을 자료형 선택 가능
    // 만약 변환 불가일 경우, Bad Request(400)
    // Ex) Integer limit이지만 limit=3.5로 줄 경우, Bad Request
    @RequestParam("limit")
    Integer limit,
    // required
    // : 반드시 포함해야 하는지 아닌지를 required로 정의 가능
    @RequestParam(value = "notReq", required = false)
    String notRequired, // value값과 param이 일치하여야 한다. notReq="test"
    // defaultValue
    // : 기본값 설정을 원한다면 defaultValue
    // 값은 반드시 문자열로 들어간다.
    @RequestParam(value = "default", defaultValue = "hello")
    String defaultVal
  ) {
    log.info("query: " + query);
    log.info("limit: " + limit);
    log.info("notRequired: " + notRequired);
    log.info("default: " + defaultVal);

    return "done";
  }

  // Pagination
  // GET /query-page?page=1&perpage=25
  @GetMapping("/query-page")
  public Object queryPage(
    @RequestParam(value = "page", defaultValue = "1")
    Integer page,
    @RequestParam(value = "perpage", defaultValue = "25")
    Integer perPage
  ) {
    log.info("page: " + page);
    log.info("perPage: " + perPage);

    // 일단 데이터를 다 조회한 후, 일부분만 조회 (그다지 좋은 방법이 아님)
    service.readTop20();
    // List<ArticleDto>로 반환
    service.readArticlePagedList(page, perPage);
    // Page<ArticleDto>로 반환
    return service.readArticlePaged(page, perPage);
  }

  // 간단한 Search Query
  // GET /query-search?q=keyword&cat=writer
  @GetMapping("/query-search")
  public String querySearch(
    @RequestParam("q")
    String keyword,
    @RequestParam(value = "cat", defaultValue = "title")
    String category
  ) {
    log.info("keyword: " + keyword);
    log.info("category: " + category);
    return "done";
  }
}
