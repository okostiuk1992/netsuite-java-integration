package com.netsuite.java.integration;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.signature.AuthorizationHeaderSigningStrategy;
import oauth.signpost.signature.HmacSha256MessageSigner;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;
import java.rmi.ServerException;

@Slf4j
public class Integration {
    private static final String CONSUMER_KEY = "XXXX";
    private static final String CONSUMER_SECRET = "XXXX";
    private static final String ACCESS_TOKEN = "XXXX";
    private static final String ACCESS_SECRET = "XXXX";
    public static final String USER = "XXXX";
    private static final String NETSUITE_BASE_URL = "https://7930879-sb1.restlets.api.netsuite.com/app/site/hosting/restlet.nl?script=396&deploy=2";

    @SneakyThrows
    public String makeAuthenticatedRequest(String payload) {
        StringEntity requestEntity = new StringEntity(payload, ContentType.APPLICATION_JSON);
        HttpResponse response = makeRequest(requestEntity);

        StatusLine statusLine = response.getStatusLine();
        HttpEntity responseEntity = response.getEntity();
        if (statusLine.getStatusCode() > 300) {
            log.error("Can't create entity in NS for payload {} due to status code: {} and reason: {}",
                    payload,
                    statusLine.getStatusCode(),
                    EntityUtils.toString(responseEntity, StandardCharsets.UTF_8));
            throw new ServerException("NS_ERROR");
        }

        return EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
    }

    private HttpResponse makeRequest(StringEntity requestEntity) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(NETSUITE_BASE_URL);
            httpPost.setEntity(requestEntity);
            getOAuthConsumer().sign(httpPost);

            return httpClient.execute(httpPost);
        } catch (Exception e) {
            log.error("Can't send request to NS", e);
            throw new RuntimeException("NS_ERROR", e);
        }
    }

    public OAuthConsumer getOAuthConsumer() {
        OAuthConsumer consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        consumer.setTokenWithSecret(ACCESS_TOKEN, ACCESS_SECRET);
        HmacSha256MessageSigner messageSigner = new HmacSha256MessageSigner();
        messageSigner.setTokenSecret(ACCESS_SECRET); //this is required due to a bug in library
        consumer.setMessageSigner(messageSigner);
        consumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());

        HttpParameters params = new HttpParameters();
        params.put("oauth_timestamp", String.valueOf(System.currentTimeMillis() / 1000L));
        params.put("oauth_signature_method", "HMAC-SHA256");
        params.put("oauth_nonce", generateNonce());
        params.put("oauth_version", "1.0");
        params.put("realm", USER);
        consumer.setAdditionalParameters(params);

        return consumer;
    }

    public static String generateNonce() {
        var allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            result.append(allowedChars[(int) (Math.random() * allowedChars.length)]);
        }

        return result.toString();
    }
}
