package oathsworn.larsbot;

import com.amazonaws.services.s3.AmazonS3Client;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.nio.charset.Charset;

@Log4j2
final class TokenRetriever {

    private final static String TOKEN_BUCKET = "larsbotcredentials";
    private final static String TOKEN_PATH = "botTokens/botToken";

    private final AmazonS3Client s3Client;

    public TokenRetriever() {
        s3Client = new AmazonS3Client();
    }

    public String getBotToken() {

        try {
            return IOUtils.toString(
                    s3Client.getObject(TOKEN_BUCKET, TOKEN_PATH).getObjectContent(),
                    Charset.defaultCharset());
        } catch (IOException e) {
            log.log(Level.ERROR, e);
            throw new RuntimeException(e);
        }
    }
}