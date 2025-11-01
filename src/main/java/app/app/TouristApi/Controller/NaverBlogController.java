package app.app.TouristApi.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
public class NaverBlogController {


    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    @GetMapping("/api/naver-search-blog")
    public ResponseEntity<String> searchBlog(@RequestParam String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/search/blog?query=" + encodedQuery;

            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put("X-Naver-Client-Id", clientId);
            requestHeaders.put("X-Naver-Client-Secret", clientSecret);

            String responseBody = get(apiURL, requestHeaders);
            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("API 요청 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private String get(String apiUrl, Map<String, String> requestHeaders) throws IOException {
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }
            int responseCode = con.getResponseCode();
            return (responseCode == HttpURLConnection.HTTP_OK)
                    ? readBody(con.getInputStream())
                    : readBody(con.getErrorStream());
        } finally {
            con.disconnect();
        }
    }

    private HttpURLConnection connect(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        return (HttpURLConnection) url.openConnection();
    }

    private String readBody(InputStream body) throws IOException {
        try (BufferedReader lineReader = new BufferedReader(new InputStreamReader(body))) {
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }
            return responseBody.toString();
        }
    }
}
