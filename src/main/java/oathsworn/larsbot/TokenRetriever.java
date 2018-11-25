package oathsworn.larsbot;

interface TokenRetriever {
    String getBotToken();
    String getBlizzardClientId();
    String getBlizzardClientSecret();
}
