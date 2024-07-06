package dev.thource.runelite.dudewheresmystuff.export.utils;

import static net.runelite.client.RuneLite.RUNELITE_DIR;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import dev.thource.runelite.dudewheresmystuff.export.writers.GoogleSheetsWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleSheetConnectionUtils {
  private static final String APPLICATION_NAME = "Where's My Stuff Dev";
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final String TOKENS_DIRECTORY_PATH =
      new File(RUNELITE_DIR, "dudewheresmystuff/tokens").getAbsolutePath();

  private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.DRIVE_FILE);
  private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

  private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, String userEmail)
      throws IOException {
    // Load client secrets.
    InputStream in = GoogleSheetsWriter.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    if (in == null) {
      throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
    }
    GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build();

    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize(userEmail);
  }

  public static Sheets getSheetsConnection(String userEmail) {
    final NetHttpTransport HTTP_TRANSPORT;
    try {
      HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      return new Sheets.Builder(
              HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, userEmail))
          .setApplicationName(APPLICATION_NAME)
          .build();
    } catch (GeneralSecurityException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void invalidateCredentials() {
    File f = new File(TOKENS_DIRECTORY_PATH, "StoredCredential");
    f.delete();
  }
}
