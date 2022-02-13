/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bb;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.samples.youtube.cmdline.Auth;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author yef9b
 */
@Named(value = "testBean")
@ViewScoped
public class TestBean implements Serializable{
    
    /**
     * Define a global variable that identifies the name of a file that
     * contains the developer's API key.
     */
    private static final String PROPERTIES_FILENAME = "youtube.properties";

    private static final long NUMBER_OF_VIDEOS_RETURNED = 5;
    
    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;

    private static Properties properties = null;
    
    private String nextPageToken = null;
    
    private String prevPageToken = null;
    
    private List<SearchResult> list = new ArrayList<>();
    static {
        // Read the developer key from the properties file.
        properties = new Properties();
        try {
            InputStream in = TestBean.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
            properties.load(in);

            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("youtube-cmdline-search-sample").build();

        } catch (IOException e) {
            System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
                    + " : " + e.getMessage());
        }
    }
    /**
     * Creates a new instance of TestBean
     */
    public TestBean() {
    }
    
    private void common(Consumer< YouTube.Search.List> consumer){
        
        try {
            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");

            String apiKey = properties.getProperty("youtube.apikey");
            search.setKey(apiKey);
            String channelId = "UCyG7hwRLEfHLiK4lPt5g3vw";
            search.setChannelId(channelId);
            search.setType("video");
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url),nextPageToken");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
            consumer.accept(search);
            
            SearchListResponse searchResponse = search.execute();
            this.nextPageToken = searchResponse.getNextPageToken();
            this.prevPageToken = searchResponse.getPrevPageToken();
            this.list = searchResponse.getItems();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public String next(){
        this.common(search -> search.setPageToken(this.nextPageToken));
        return null;
    }
    
    public String prev(){
        this.common(search -> search.setPageToken(this.prevPageToken));
        return null;
    }
    
    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }
    
    public List<SearchResult> getList() {
        return list;
    }

    public void setList(List<SearchResult> list) {
        this.list = list;
    }

    public String getPrevPageToken() {
        return prevPageToken;
    }

    public void setPrevPageToken(String prevPageToken) {
        this.prevPageToken = prevPageToken;
    }
    
}
