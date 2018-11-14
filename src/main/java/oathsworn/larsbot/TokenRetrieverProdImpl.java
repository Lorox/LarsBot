package oathsworn.larsbot;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.nio.charset.Charset;

@Log4j2
final class TokenRetrieverProdImpl implements TokenRetriever {

    private final static String TOKEN_BUCKET = "larsbotcredentials";
    private final static String TOKEN_PATH = "botTokens/botToken";

    private final AmazonS3Client  s3Client;

    public TokenRetrieverProdImpl() {
        s3Client = new AmazonS3Client();
    }

    public String getBotToken() {

        try {
            return IOUtils.toString(
                    s3Client.getObject(new GetObjectRequest(TOKEN_BUCKET, TOKEN_PATH)).getObjectContent(),
                    Charset.defaultCharset());
        } catch (IOException e) {
            log.log(Level.ERROR, e);
            throw new RuntimeException(e);
        }
    }
}
