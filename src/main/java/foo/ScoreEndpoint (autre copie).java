import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GooglePublicKeysManager;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class ScoreEndpoint {

    private static final String CLIENT_ID = "927375242383-jm45ei76rdsfv7tmjv58tcsjjpvgkdje.apps.googleusercontent.com"; // Remplacer par le bon client ID

    // Méthode pour vérifier le jeton Google
    private GoogleIdToken verifyToken(String idTokenString) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
            .setAudience(Collections.singletonList(CLIENT_ID))
            .build();

        return verifier.verify(idTokenString);
    }

    @ApiMethod(name = "addScoreSec", httpMethod = HttpMethod.GET)
    public Entity addScoreSec(@Named("score") int score, @Named("token") String token) throws UnauthorizedException, GeneralSecurityException, IOException {
        GoogleIdToken idToken = verifyToken(token);

        if (idToken == null) {
            throw new UnauthorizedException("Invalid token");
        }

        // Extraire les informations de l'utilisateur du jeton
        GoogleIdToken.Payload payload = idToken.getPayload();
        String userId = payload.getSubject();  // Identifiant unique de l'utilisateur
        String email = payload.getEmail();  // Email de l'utilisateur

        Entity e = new Entity("Score", "" + email + score);
        e.setProperty("name", email);
        e.setProperty("score", score);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(e);

        return e;
    }
}

