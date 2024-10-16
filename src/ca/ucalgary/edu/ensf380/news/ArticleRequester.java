package ca.ucalgary.edu.ensf380.news;

import ca.ucalgary.edu.ensf380.models.Article;
import ca.ucalgary.edu.ensf380.models.FetchNewsCallback;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The ArticleRequester class is responsible for fetching news articles from
 * an online API. It uses a background thread to perform the fetch operation
 * and a callback to notify when the fetch is complete.
 */
public class ArticleRequester {
    private final Logger logger = Logger.getLogger(ArticleRequester.class.getName());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private String API_KEY = null; // API key for news API

    /**
     * Private constructor to prevent instantiation of the class.
     */
    public ArticleRequester() {}

    /**
     * Returns the api key.
     *
     * @return The API key as a string.
     */
    private String getApiKey() {
        return "eef7bdc9dce94017be3ae4b4b63177f5";
    }

    /**
     * Sets the API key for the news API.
     *
     * @param apiKey The API key to set.
     */
    public void setApiKey(String apiKey) {
        this.API_KEY = apiKey;
    }

    /**
     * Fetches news articles asynchronously and calls the provided callback
     * when the fetch is complete.
     *
     * @param query The query string to search for articles.
     * @param sortBy The sort order for the articles (relevancy, popularity, publishedAt).
     * @param numberOfArticles The number of articles to fetch.
     * @param callback The callback to call when the fetch is complete.
     */
    public void fetchNewsAsync(String query, String sortBy, int numberOfArticles, FetchNewsCallback callback) {
        executorService.submit(() -> {
            List<Article> articles = fetchNews(query, sortBy, numberOfArticles);
            SwingUtilities.invokeLater(() -> callback.onFetchNewsComplete(articles));
        });
    }

    /**
     * Fetches news articles synchronously.
     *
     * @param query The query string to search for articles.
     * @param sortBy The sort order for the articles (relevancy, popularity, publishedAt).
     * @param numberOfArticles The number of articles to fetch.
     * @return A list of fetched articles.
     */
    public List<Article> fetchNews(String query, String sortBy, int numberOfArticles) {
        if (API_KEY == null) {
            API_KEY = getApiKey();
        }

        String apiUrl = "https://newsapi.org/v2/everything?q=" + query + "&sortBy=" + sortBy + "&apiKey=" + API_KEY;

        try {
            URL url = new URI(apiUrl).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int statusCode = conn.getResponseCode();

            if (statusCode == 200) {
                return getArticles(conn, numberOfArticles);
            } else {
                System.out.println("Request failed with status code: " + statusCode);
            }

            conn.disconnect();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred while loading Articles", e);
        }

        return null;
    }

    /**
     * Parses the response from the API and extracts a list of articles.
     *
     * @param conn The HttpURLConnection object.
     * @param numberOfArticles The number of articles to fetch.
     * @return A list of Article objects.
     * @throws IOException If an I/O error occurs.
     */
    private List<Article> getArticles(HttpURLConnection conn, int numberOfArticles) throws IOException {
        JSONArray articlesArray = getObjects(conn);

        List<Article> articles = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        for (int i = 0; i < articlesArray.length(); i++) {
            indices.add(i);
        }

        // Shuffle the indices to randomize the order
        Collections.shuffle(indices);

        // Ensure we don't try to get more articles than are available
        if (articlesArray.length() <= numberOfArticles) {
            numberOfArticles = articlesArray.length();
        }

        for (int i = 0; i < numberOfArticles; i++) {
            JSONObject articleJson = articlesArray.getJSONObject(indices.get(i));
            String title = articleJson.getString("title");
            String description = articleJson.getString("description");
            String content = articleJson.getString("content");
            Article article = new Article(title, description, content);
            articles.add(article);
        }

        return articles;
    }

    /**
     * Reads the response from the API and returns it as a JSON array.
     *
     * @param conn The HttpURLConnection object.
     * @return A JSON array of articles.
     * @throws IOException If an I/O error occurs.
     */
    private JSONArray getObjects(HttpURLConnection conn) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        String responseData = response.toString();

        JSONObject jsonResponse = new JSONObject(responseData);
        return jsonResponse.getJSONArray("articles");
    }
}
