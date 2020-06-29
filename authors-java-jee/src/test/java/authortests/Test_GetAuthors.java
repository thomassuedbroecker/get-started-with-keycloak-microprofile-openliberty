package authortests;

// The Author data structure class from the microservice
import com.ibm.authors.Author;

// Java
import java.net.URI;
import java.io.StringReader;

// JSON
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

// JSON-B
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
// JAX-RS

// Format data for the POST request
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.client.Entity;

// Client
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

// JUnit
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class Test_GetAuthors {

    @ParameterizedTest(name = "{index} => name=''{0},{1},{2},{3},{4},{5},{6}")
    @CsvSource({      
            "Niklas,Niklas Heidloff,author-cloud-native-starter,1234,cloudnativestarter,password,authors-client-cloud-native-starter"
    })  

    public void testAuthGetAuthor(
            final String nameAuthor, 
            final String expectedResult, 
            final String user,
            final String password,
            final String realm,
            final String grant_type,
            final String client_id
            ) {

        final String realmName = realm;
        final String tokenPath = "/protocol/openid-connect/token";
        final String authServerBaseUrl = "http://localhost:8282/auth/realms";

        // Prepare test

        // 1. Get token from Keycloak
        // Build data for auth
        final Form formData = new Form();
        formData.param("username", user)
                .param("password", password)
                .param("realm", realm)
                .param("grant_type", grant_type)
                .param("client_id", client_id);
        // Build client
        final Client client = ClientBuilder.newBuilder().build();
        final WebTarget target = client
                .target(UriBuilder.fromUri(authServerBaseUrl).path(realmName).path(tokenPath).build());
        // Get token
        final String keycloakToken = this.getToken(target, formData);
        System.out.println("[JUNIT-TEST] -> keycloakToken : " + keycloakToken);

        // 2. Use bearer Token and invoke getAuthor
        final String author_result = getAuthorAuthorized(keycloakToken, nameAuthor);
        System.out.println("[JUNIT-TEST] -> getauthor response: " + author_result);
        
        // Build Author Object from result      
        final JsonbConfig config = new JsonbConfig().withAdapters(new AuthorJsonbAdapter());	
        final Jsonb jsonb = JsonbBuilder.create(config);		
        final Author author_json = jsonb.fromJson(author_result, Author.class);	

        // Verify Author response with JUnit
        System.out.println("[JUNIT-TEST] -> Verify: " + author_json.getName()); 
        assertEquals(expectedResult, author_json.getName());
    }
    

    private String getAuthorAuthorized(final String token, final String nameAuthor) {
        final Client client = ClientBuilder.newBuilder().build();
        client.register(new KeycloakAuthRequestFilter(token));

        final String BASE_URL = "http://localhost:3000/api/v1/";
        final URI baseURI = URI.create(BASE_URL);
        final String AUTHOR_PATH = "/getauthor";

        WebTarget target = client.target(UriBuilder.fromUri(baseURI).path(AUTHOR_PATH).build());
        target = target.queryParam("name", nameAuthor);
        final Response response = target.request().accept(MediaType.APPLICATION_JSON_TYPE).get();

        System.out.println("[JUNIT-TEST] -> getAuthorAuthorized response status: " + response.getStatus());
        System.out.println("[JUNIT-TEST] -> getAuthorAuthorized response has entity: " + response.hasEntity());

        if (response.hasEntity() == true) {
            final String string_response = response.readEntity(String.class);
            System.out.println("[JUNIT-TEST] -> Get Author response has 'data': " + string_response);
            return string_response;
        } else {
            return "No, data from author";
        }
    }

    private String getToken(final WebTarget target, final Form formData) {     
        System.out.println("[JUNIT-TEST] -> Start get Token");
        final Response keycloakTokenResponse = target.request()
                .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE).post(Entity.form(formData));
        System.out.println("[JUNIT-TEST] -> Keycloak getToken response has entity: " + keycloakTokenResponse.hasEntity());

        if (keycloakTokenResponse.hasEntity() == true) {
            final String string_response = keycloakTokenResponse.readEntity(String.class);
            final JsonObject jsonObject = stringToJson(string_response);
            final String accessToken = jsonObject.getString("access_token");        
            // System.out.println("[JUNIT-TEST] -> Keycloak  'access_token': " + accessToken);
            
            return accessToken;
        } else {
            return "{'Error':'No access-token from keycloak'}";
        }
    }

    private static JsonObject stringToJson(final String thestring) {

        final JsonReader jsonReader = Json.createReader(new StringReader(thestring));
        final JsonObject object = jsonReader.readObject();
        jsonReader.close();
        // System.out.println("[JUNIT-TEST] -> stringToJson object : " + object);  
        return object;
    }
}