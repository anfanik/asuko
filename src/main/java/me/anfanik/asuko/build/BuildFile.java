package me.anfanik.asuko.build;

import me.anfanik.asuko.AsukoLogger;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.nio.file.Path;

public class BuildFile {

    private final String id;
    private final String url;
    private final String hashUrl;
    private final File destination;
    private final FileCredentials credentials;
    private String downloadHash;

    public BuildFile(String id, String url, String hashUrl, File destination) {
        this.id = id;
        this.url = url;
        this.hashUrl = hashUrl;
        this.destination = destination;
        credentials = null;
    }

    public BuildFile(String id, String url, String hashUrl, File destination, FileCredentials credentials) {
        this.id = id;
        this.url = url;
        this.hashUrl = hashUrl;
        this.destination = destination;
        this.credentials = credentials;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean download() {
        if (credentials == null) {
            AsukoLogger.info("Downloading \"%s\" file from %s.", id, url);
        } else {
            AsukoLogger.info("Downloading \"%s\" file from %s with username \"%s\".",
                    id, url, credentials.getUsername());
        }

        try {
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpUriRequest request = new HttpGet(url);
                if (credentials != null) {
                    request.addHeader("Authorization", "Basic " + credentials.getEncoded());
                }
                try (CloseableHttpResponse response = client.execute(request)) {
                    StatusLine status = response.getStatusLine();
                    if (status.getStatusCode() == 200) {
                        HttpEntity entity = response.getEntity();

                        Path parent = destination.toPath().getParent();
                        parent.toFile().mkdirs();
                        destination.createNewFile();

                        try (FileOutputStream outputStream = new FileOutputStream(destination)) {
                            entity.writeTo(outputStream);
                            downloadHash = getHash();
                            if (downloadHash != null) {
                                AsukoLogger.info("\"%s\" file hash: %s.", id, downloadHash);
                            } else {
                                AsukoLogger.error("Unable to get \"%s\" file hash.", downloadHash);
                            }
                            return true;
                        }
                    } else {
                        AsukoLogger.error("Unable to download \"%s\" file! Response: %d %s",
                                id, status.getStatusCode(), status.getReasonPhrase());
                        return false;
                    }
                }
            }
        } catch (IOException exception) {
            AsukoLogger.error("Unable to download \"%s\" file!", exception, id);
            return false;
        }
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public File getDestination() {
        return destination;
    }

    public FileCredentials getCredentials() {
        return credentials;
    }

    public String getHash() {
        try {
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpUriRequest request = new HttpGet(hashUrl);
                if (credentials != null) {
                    request.addHeader("Authorization", "Basic " + credentials.getEncoded());
                }
                try (CloseableHttpResponse response = client.execute(request)) {
                    StatusLine status = response.getStatusLine();
                    if (status.getStatusCode() == 200) {
                        HttpEntity entity = response.getEntity();

                        Path parent = destination.toPath().getParent();
                        parent.toFile().mkdirs();
                        destination.createNewFile();

                        StringBuilder hashBuilder = new StringBuilder();
                        try (Reader reader = new InputStreamReader(entity.getContent())) {
                            try (BufferedReader bufferedReader = new BufferedReader(reader)) {
                                String line;
                                while ((line = bufferedReader.readLine()) != null) {
                                    hashBuilder.append(line);
                                }
                            }
                        }

                        return hashBuilder.toString();
                    } else {
                        AsukoLogger.error("Unable to get hash of \"%s\" file! Response: %d %s",
                                id, status.getStatusCode(), status.getReasonPhrase());
                        return null;
                    }
                }
            }
        } catch (IOException exception) {
            AsukoLogger.error("Unable to get hash of \"%s\" file!", exception, id);
            return null;
        }
    }

    public boolean isUpdated() {
        String hash = getHash();
        if (hash != null && downloadHash != null && !hash.equals(downloadHash)) {
            downloadHash = hash;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "BuildFile{" +
                "link='" + url + '\'' +
                ", hashUrl=" + hashUrl +
                ", destination=" + destination +
                '}';
    }

}
