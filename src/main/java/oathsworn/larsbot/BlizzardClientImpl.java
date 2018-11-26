package oathsworn.larsbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import lombok.Builder;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Log4j2
final class BlizzardClientImpl implements BlizzardClient {

    private static final String TOKEN_API_URL = "https://eu.battle.net/oauth/token";
    private final static String GUILD_NEWS_URL = "https://%s.api.blizzard.com/wow/guild/";
    private final static String ITEM_API_URL = "https://%s.api.blizzard.com/wow/item/";
    private final static String CHARACTER_INFO_URL = "https://%s.api.blizzard.com/wow/character/";
    private final static int MAX_RETRIES = 5;

    private final String clientId;
    private final String clientSecret;

    private String accessToken = null;

    @Inject
    public BlizzardClientImpl(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public GuildNews getGuildNews(String guildRegion, String guildRealm, String guildName) {

        if (accessToken == null) {
            accessToken = getBlizzardAuthToken();
            if (accessToken == null) {
                return  null;
            }
        }

        GetRequestResponse<GuildNews> response =
                sendGetRequest(
                        String.format(GUILD_NEWS_URL, guildRegion) + guildRealm + "/" + guildName,
                        Arrays.asList(
                                new BasicNameValuePair("fields", "news"),
                                new BasicNameValuePair("access_token", accessToken)),
                        GuildNews.class);

        if (response.responseCode == HttpStatus.SC_UNAUTHORIZED) {
            accessToken = getBlizzardAuthToken();
        }

        if (response.responseCode != HttpStatus.SC_OK) {
            return null;
        }
        return response.response;
    }

    @Override
    public WowItem getWowItem(String region, int itemId, List<Integer> bonusLists) {

        if (accessToken == null) {
            accessToken = getBlizzardAuthToken();
            if (accessToken == null) {
                return  null;
            }
        }

        StringBuilder bonusListsString = new StringBuilder();
        bonusLists.forEach(i -> bonusListsString.append(i).append(","));

        GetRequestResponse<WowItem> response =
                sendGetRequest(
                        String.format(ITEM_API_URL, region) + itemId,
                        Arrays.asList(
                                new BasicNameValuePair("access_token", accessToken),
                                new BasicNameValuePair("bl", bonusListsString.toString())),
                        WowItem.class);

        if (response.responseCode == HttpStatus.SC_UNAUTHORIZED) {
            accessToken = getBlizzardAuthToken();
        }

        if (response.responseCode != HttpStatus.SC_OK) {
            return null;
        }
        return response.response;
    }

    @Override
    public WowCharacterInfo getWowCharacterInfo(String region, String realm, String characterName) {

        if (accessToken == null) {
            accessToken = getBlizzardAuthToken();
            if (accessToken == null) {
                return  null;
            }
        }

        GetRequestResponse<WowCharacterInfo> response =
                sendGetRequest(
                        String.format(CHARACTER_INFO_URL, region) + realm + "/" + characterName,
                        Collections.singletonList(new BasicNameValuePair("access_token", accessToken)),
                        WowCharacterInfo.class);

        if (response.responseCode == HttpStatus.SC_UNAUTHORIZED) {
            accessToken = getBlizzardAuthToken();
        }

        if (response.responseCode != HttpStatus.SC_OK) {
            return null;
        }
        return response.response;
    }

    private String getBlizzardAuthToken() {

        GetRequestResponse<AccessTokenResponse> tokenResponse =
                sendGetRequest(
                        TOKEN_API_URL,
                        Arrays.asList(
                                new BasicNameValuePair("client_id", clientId),
                                new BasicNameValuePair("client_secret", clientSecret),
                                new BasicNameValuePair("grant_type", "client_credentials")),
                        AccessTokenResponse.class);

        if (tokenResponse.responseCode != HttpStatus.SC_OK) {
            log.log(Level.ERROR, "Failed to retrieve access token");
            return null;
        }
        return tokenResponse.response.access_token;
    }

    private <T> GetRequestResponse<T> sendGetRequest(String url, List<NameValuePair> params, Class<T> clazz) {

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.setParameters(params);
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.setHeader("Cache-Control", "no-cache");
            httpGet.setHeader("Pragma", "no-cache");
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Accept-Encoding", "gzip, deflate, br");
            httpGet.setHeader("Accept-Language", "en-US,en;q=0.9,en-GB;q=0.8");
            httpGet.setHeader("Connection", "keep-alive");
            httpGet.setHeader("Host", "eu.api.blizzard.com");
            httpGet.setHeader("Upgrade-Insecure-Requests", "1");

            HttpResponse response = null;
            for (int i = 0; i < MAX_RETRIES; i++) {

                response = httpClient.execute(httpGet);

                if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    break;
                }
                Thread.sleep(50);
            }

            GetRequestResponse.GetRequestResponseBuilder<T> responseBuilder =
                    GetRequestResponse.builder();

            if (response != null && response.getStatusLine() != null) {

                responseBuilder.responseCode(response.getStatusLine().getStatusCode());

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK && response.getEntity() != null) {

                    Gson gson = new GsonBuilder().create();
                    try {
                        responseBuilder.response(
                                gson.fromJson(EntityUtils.toString(response.getEntity()), clazz));
                    } catch (JsonSyntaxException | IOException e) {
                        log.log(Level.ERROR, "Failed to parse response.", e);
                        responseBuilder.responseCode(-1);
                    }
                }
            }

            return responseBuilder.build();
        } catch (InterruptedException | IOException | URISyntaxException e) {
            log.log(Level.ERROR, "Unexpected exception, continuing...", e);
        }

        return GetRequestResponse.<T>builder()
                .responseCode(HttpStatus.SC_METHOD_FAILURE)
                .build();
    }

    @Value
    @Builder
    private static class GetRequestResponse<T> {
        private final int responseCode;
        private final T response;
    }

    @Value
    @Builder
    private static class AccessTokenResponse {
        private final String access_token;
    }
}
