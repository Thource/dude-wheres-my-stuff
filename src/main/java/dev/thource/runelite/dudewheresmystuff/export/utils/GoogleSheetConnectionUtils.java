package dev.thource.runelite.dudewheresmystuff.export.utils;

import static net.runelite.client.RuneLite.RUNELITE_DIR;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import lombok.Setter;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Handles Google OAuth2 authorization and provides a Sheets API client, implemented directly on
 * top of OkHttp instead of the google-api-client / google-api-services-sheets libraries.
 */
public class GoogleSheetConnectionUtils {

  /** The Google account used for all Sheets exports; shared so callers can invalidate its token. */
  public static final String EXPORT_ACCOUNT_EMAIL = "rldudewms@gmail.com";

  private static final File TOKENS_DIRECTORY = new File(RUNELITE_DIR, "dudewheresmystuff/tokens");
  private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

  private static final String SCOPE = "https://www.googleapis.com/auth/drive.file";
  private static final String AUTH_URI = "https://accounts.google.com/o/oauth2/v2/auth";
  private static final String TOKEN_URI = "https://oauth2.googleapis.com/token";
  private static final int LOCAL_SERVER_PORT = 8888;
  private static final String REDIRECT_URI = "http://localhost:" + LOCAL_SERVER_PORT + "/Callback";

  @Setter private static OkHttpClient HTTP_CLIENT;
  @Setter private static Gson GSON;
  private static final MediaType JSON_MEDIA_TYPE =
      MediaType.parse("application/json; charset=utf-8");

  private GoogleSheetConnectionUtils() {}

  /** The pieces of a Google OAuth client_secrets.json we actually need. */
  private static class ClientSecrets {
    String clientId;
    String clientSecret;
  }

  /** A stored OAuth token set, persisted as JSON next to where google-api-client used to store it. */
  private static class StoredToken {
    String accessToken;
    String refreshToken;
    long expiresAtEpochSeconds;
  }

  private static ClientSecrets loadClientSecrets() throws IOException {
    try (InputStream in =
        GoogleSheetConnectionUtils.class.getResourceAsStream(CREDENTIALS_FILE_PATH)) {
      if (in == null) {
        throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
      }
      JsonObject root =
          new JsonParser()
              .parse(new InputStreamReader(in, StandardCharsets.UTF_8))
              .getAsJsonObject();
      // A standard Google credentials.json looks like {"installed": {"client_id": ..., ...}}
      JsonObject section = root.has("installed") ? root.getAsJsonObject("installed")
          : root.getAsJsonObject("web");
      ClientSecrets secrets = new ClientSecrets();
      secrets.clientId = section.get("client_id").getAsString();
      secrets.clientSecret = section.get("client_secret").getAsString();
      return secrets;
    }
  }

  private static File tokenFile(String userEmail) {
    return new File(TOKENS_DIRECTORY, "StoredCredential-" + userEmail);
  }

  private static StoredToken loadStoredToken(String userEmail) {
    File file = tokenFile(userEmail);
    if (!file.exists()) {
      return null;
    }
    try (InputStreamReader reader =
        new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8)) {
      return GSON.fromJson(reader, StoredToken.class);
    } catch (IOException e) {
      return null;
    }
  }

  private static void saveStoredToken(String userEmail, StoredToken token) throws IOException {
    if (!TOKENS_DIRECTORY.exists() && !TOKENS_DIRECTORY.mkdirs()) {
      throw new IOException("Could not create tokens directory: " + TOKENS_DIRECTORY);
    }
    Files.write(
        tokenFile(userEmail).toPath(), GSON.toJson(token).getBytes(StandardCharsets.UTF_8));
  }

  private static StoredToken exchangeCodeForToken(ClientSecrets secrets, String code)
      throws IOException {
    RequestBody body =
        new FormBody.Builder()
            .add("code", code)
            .add("client_id", secrets.clientId)
            .add("client_secret", secrets.clientSecret)
            .add("redirect_uri", REDIRECT_URI)
            .add("grant_type", "authorization_code")
            .build();
    return sendTokenRequest(body);
  }

  private static StoredToken refreshAccessToken(ClientSecrets secrets, String refreshToken)
      throws IOException {
    RequestBody body =
        new FormBody.Builder()
            .add("refresh_token", refreshToken)
            .add("client_id", secrets.clientId)
            .add("client_secret", secrets.clientSecret)
            .add("grant_type", "refresh_token")
            .build();
    StoredToken token = sendTokenRequest(body);
    if (token.refreshToken == null) {
      // Google doesn't always re-issue a refresh token; keep using the existing one.
      token.refreshToken = refreshToken;
    }
    return token;
  }

  private static StoredToken sendTokenRequest(RequestBody body) throws IOException {
    Request request = new Request.Builder().url(TOKEN_URI).post(body).build();
    try (Response response = HTTP_CLIENT.newCall(request).execute()) {
      if (!response.isSuccessful() || response.body() == null) {
        throw new IOException(
            "Token request failed: " + response.code() + " " + response.message());
      }
      JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();
      StoredToken token = new StoredToken();
      token.accessToken = json.get("access_token").getAsString();
      if (json.has("refresh_token")) {
        token.refreshToken = json.get("refresh_token").getAsString();
      }
      int expiresIn = json.has("expires_in") ? json.get("expires_in").getAsInt() : 3600;
      token.expiresAtEpochSeconds = Instant.now().getEpochSecond() + expiresIn;
      return token;
    }
  }

  private static String runAuthorizationFlow(ClientSecrets secrets) throws IOException {
    CompletableFuture<String> codeFuture = new CompletableFuture<>();
    HttpServer server = HttpServer.create(new InetSocketAddress(LOCAL_SERVER_PORT), 0);
    server.createContext(
        "/Callback",
        exchange -> {
          String code = extractParam(exchange.getRequestURI().getQuery(), "code");
          String responseText =
              code != null
                  ? "Authorization complete. You may close this window."
                  : "Authorization failed. You may close this window.";
          byte[] responseBytes = responseText.getBytes(StandardCharsets.UTF_8);
          exchange.sendResponseHeaders(200, responseBytes.length);
          try (var os = exchange.getResponseBody()) {
            os.write(responseBytes);
          }
          if (code != null) {
            codeFuture.complete(code);
          } else {
            codeFuture.completeExceptionally(new IOException("Authorization failed, no code returned"));
          }
        });
    server.start();

    try {
      String authUrl =
          AUTH_URI
              + "?client_id=" + URLEncoder.encode(secrets.clientId, StandardCharsets.UTF_8)
              + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8)
              + "&response_type=code"
              + "&scope=" + URLEncoder.encode(SCOPE, StandardCharsets.UTF_8)
              + "&access_type=offline"
              + "&prompt=consent";
      if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
        Desktop.getDesktop().browse(URI.create(authUrl));
      }
      return codeFuture.get();
    } catch (Exception e) {
      throw new IOException("Authorization flow failed", e);
    } finally {
      server.stop(0);
    }
  }

  private static String extractParam(String query, String key) {
    if (query == null) {
      return null;
    }
    for (String pair : query.split("&")) {
      String[] kv = pair.split("=", 2);
      if (kv.length == 2 && kv[0].equals(key)) {
        return URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
      }
    }
    return null;
  }

  private static String getAccessToken(String userEmail) throws IOException {
    ClientSecrets secrets = loadClientSecrets();
    StoredToken token = loadStoredToken(userEmail);

    if (token != null && token.expiresAtEpochSeconds > Instant.now().getEpochSecond() + 60) {
      return token.accessToken;
    }

    if (token != null && token.refreshToken != null) {
      try {
        StoredToken refreshed = refreshAccessToken(secrets, token.refreshToken);
        saveStoredToken(userEmail, refreshed);
        return refreshed.accessToken;
      } catch (IOException e) {
        // Refresh failed (e.g. token revoked) - fall through to a fresh authorization flow.
      }
    }

    String code = runAuthorizationFlow(secrets);
    StoredToken fresh = exchangeCodeForToken(secrets, code);
    saveStoredToken(userEmail, fresh);
    return fresh.accessToken;
  }

  /**
   * Returns an OkHttp-backed client for the Google Sheets REST API v4, replacing the old
   * google-api-client {@code Sheets} object.
   */
  public static SheetsClient getSheetsConnection(String userEmail) {
    try {
      String accessToken = getAccessToken(userEmail);
      return new SheetsClient(HTTP_CLIENT, accessToken);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void invalidateCredentials(String userEmail) {
    tokenFile(userEmail).delete();
  }

  /** Minimal OkHttp-based wrapper around the Google Sheets REST API v4. */
  public static class SheetsClient {
    private static final String BASE_URL = "https://sheets.googleapis.com/v4/spreadsheets";

    private final OkHttpClient client;
    private final String accessToken;

    SheetsClient(OkHttpClient client, String accessToken) {
      this.client = client;
      this.accessToken = accessToken;
    }

    public JsonObject createSpreadsheet(JsonObject spreadsheetBody) throws IOException {
      Request request =
          authorizedRequest(BASE_URL)
              .post(RequestBody.create(JSON_MEDIA_TYPE, GSON.toJson(spreadsheetBody)))
              .build();
      return execute(request);
    }

    public JsonObject getSpreadsheet(String spreadsheetId) throws IOException {
      String url = BASE_URL + "/" + spreadsheetId;
      return execute(authorizedRequest(url).get().build());
    }

    public JsonObject clearValues(String spreadsheetId, String range) throws IOException {
      String url = BASE_URL + "/" + spreadsheetId + "/values/" + urlEncode(range) + ":clear";
      Request request =
          authorizedRequest(url).post(RequestBody.create((MediaType) null, new byte[0])).build();
      return execute(request);
    }

    public JsonObject getValues(String spreadsheetId, String range) throws IOException {
      String url = BASE_URL + "/" + spreadsheetId + "/values/" + urlEncode(range);
      return execute(authorizedRequest(url).get().build());
    }

    public JsonObject updateValues(String spreadsheetId, String range, JsonObject valueRangeBody)
        throws IOException {
      String url =
          BASE_URL + "/" + spreadsheetId + "/values/" + urlEncode(range)
              + "?valueInputOption=USER_ENTERED";
      Request request =
          authorizedRequest(url)
              .put(RequestBody.create(JSON_MEDIA_TYPE, GSON.toJson(valueRangeBody)))
              .build();
      return execute(request);
    }

    public JsonObject appendValues(String spreadsheetId, String range, JsonObject valueRangeBody)
        throws IOException {
      String url =
          BASE_URL + "/" + spreadsheetId + "/values/" + urlEncode(range)
              + ":append?valueInputOption=USER_ENTERED";
      Request request =
          authorizedRequest(url)
              .post(RequestBody.create(JSON_MEDIA_TYPE, GSON.toJson(valueRangeBody)))
              .build();
      return execute(request);
    }

    public JsonObject batchUpdate(String spreadsheetId, JsonObject batchUpdateBody)
        throws IOException {
      String url = BASE_URL + "/" + spreadsheetId + ":batchUpdate";
      Request request =
          authorizedRequest(url)
              .post(RequestBody.create(JSON_MEDIA_TYPE, GSON.toJson(batchUpdateBody)))
              .build();
      return execute(request);
    }

    private Request.Builder authorizedRequest(String url) {
      return new Request.Builder().url(url).header("Authorization", "Bearer " + accessToken);
    }

    private JsonObject execute(Request request) throws IOException {
      try (Response response = client.newCall(request).execute()) {
        String bodyString = response.body() != null ? response.body().string() : "";
        if (response.code() == 401) {
          throw new GoogleSheetsAuthException(
              "Google rejected the access token (401): " + bodyString);
        }
        if (!response.isSuccessful()) {
          throw new IOException(
              "Sheets API request failed: " + response.code() + " " + bodyString);
        }
        return bodyString.isEmpty()
            ? new JsonObject()
            : new JsonParser().parse(bodyString).getAsJsonObject();
      }
    }

    private static String urlEncode(String value) {
      return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }
  }
}
