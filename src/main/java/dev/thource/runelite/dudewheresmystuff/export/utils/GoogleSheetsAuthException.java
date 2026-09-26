package dev.thource.runelite.dudewheresmystuff.export.utils;

/**
 * Thrown when the Google Sheets REST API rejects a request because the access token is invalid,
 * expired, or has been revoked (HTTP 401). This replaces {@code
 * com.google.api.client.auth.oauth2.TokenResponseException}, which the old google-api-client
 * {@code Credential} class used to throw automatically when it couldn't refresh a token.
 *
 * <p>Unlike the old {@code Credential} object, {@link GoogleSheetConnectionUtils.SheetsClient}
 * does not silently re-authenticate mid-call, so callers that want to recover (delete the stored
 * token and retry with a freshly-built client) should catch this specifically.
 */
public class GoogleSheetsAuthException extends RuntimeException {
  public GoogleSheetsAuthException(String message) {
    super(message);
  }
}
