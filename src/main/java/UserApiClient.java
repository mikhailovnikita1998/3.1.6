import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserApiClient {
    private static final String BASE_URL = "http://94.198.50.185:7081/api/users";
    private static String sessionId = null;

    public static void main(String[] args) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        // Получаю список пользователей и сохранняю
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(BASE_URL, HttpMethod.GET, entity, String.class);

        sessionId = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        if (sessionId != null && sessionId.contains(";")) {
            sessionId = sessionId.split(";")[0];
        }

        System.out.println("Session ID: " + sessionId);

        // Сохраненияю нового пользователя
        HttpHeaders postHeaders = new HttpHeaders();
        postHeaders.add(HttpHeaders.COOKIE, sessionId);
        postHeaders.setContentType(MediaType.APPLICATION_JSON);

        User newUser = new User(3L, "James", "Brown", (byte) 25);
        String newUserJson = objectMapper.writeValueAsString(newUser);
        HttpEntity<String> postEntity = new HttpEntity<>(newUserJson, postHeaders);

        ResponseEntity<String> postResponse = restTemplate.exchange(BASE_URL, HttpMethod.POST, postEntity, String.class);
        System.out.println("POST Response: " + postResponse.getBody());

        // Меняю пользователя
        User updatedUser = new User(3L, "Thomas", "Shelby", (byte) 25);
        String updatedUserJson = objectMapper.writeValueAsString(updatedUser);
        HttpEntity<String> putEntity = new HttpEntity<>(updatedUserJson, postHeaders);

        ResponseEntity<String> putResponse = restTemplate.exchange(BASE_URL, HttpMethod.PUT, putEntity, String.class);
        System.out.println("PUT Response: " + putResponse.getBody());

        // Удаляю
        HttpHeaders deleteHeaders = new HttpHeaders();
        deleteHeaders.add(HttpHeaders.COOKIE, sessionId);
        HttpEntity<String> deleteEntity = new HttpEntity<>(deleteHeaders);

        ResponseEntity<String> deleteResponse = restTemplate.exchange(BASE_URL + "/3", HttpMethod.DELETE, deleteEntity, String.class);
        System.out.println("DELETE Response: " + deleteResponse.getBody());

        String code = postResponse.getBody() + putResponse.getBody() + deleteResponse.getBody();
        System.out.println("Resulting code: " + code);
    }
}
