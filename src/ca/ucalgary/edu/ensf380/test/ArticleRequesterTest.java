package ca.ucalgary.edu.ensf380.test;

import ca.ucalgary.edu.ensf380.models.Article;
import ca.ucalgary.edu.ensf380.models.FetchNewsCallback;
import ca.ucalgary.edu.ensf380.news.ArticleRequester;

import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * The ArticleRequesterTest class contains the unit tests for the ArticleRequester class.
 */
public class ArticleRequesterTest {

    /**
     * Constructs an ArticleRequesterTest object.
     */
    public ArticleRequesterTest() {
    }

    /**
     * Tests the fetch articles functionality.
     *
     * @throws InterruptedException If the operation is interrupted.
     */
    @Test
    public void testFetchArticles() throws InterruptedException {
        final int EXPECTED_ARTICLES = 10;
        final CountDownLatch latch = new CountDownLatch(1);
        final List<Article>[] result = new List[1];

        ArticleRequester articleRequester = new ArticleRequester();
        articleRequester.fetchNewsAsync("Calgary", "popularity", EXPECTED_ARTICLES, new FetchNewsCallback() {
            @Override
            public void onFetchNewsComplete(List<Article> articles) {
                result[0] = articles;
                latch.countDown();
            }
        });

        assertTrue("Fetch operation timed out", latch.await(60, TimeUnit.SECONDS));

        assertNotNull("Fetched articles list should not be null", result[0]);
        assertFalse("Fetched articles list should not be empty", result[0].isEmpty());
        assertEquals("Fetched articles list size should be as expected", EXPECTED_ARTICLES, result[0].size());

        // Check the first article
        Article firstArticle = result[0].get(0);
        assertNotNull("Article should not be null", firstArticle);
        assertNotNull("Article title should not be null", firstArticle.title());
        assertNotNull("Article description should not be null", firstArticle.description());
        assertNotNull("Article content should not be null", firstArticle.content());
    }

    /**
     * Tests the fetch articles functionality with an invalid API key.
     *
     * @throws InterruptedException If the operation is interrupted.
     */
    @Test
    public void testFetchArticlesInvalidApiKey() throws InterruptedException {
        final int EXPECTED_ARTICLES = 10;
        final CountDownLatch latch = new CountDownLatch(1);
        final List<Article>[] result = new List[1];

        ArticleRequester articleRequester = new ArticleRequester();
        articleRequester.setApiKey("INVALID_API_KEY");
        articleRequester.fetchNewsAsync("Calgary", "popularity", EXPECTED_ARTICLES, new FetchNewsCallback() {
            @Override
            public void onFetchNewsComplete(List<Article> articles) {
                result[0] = articles;
                latch.countDown();
            }
        });

        assertTrue("Fetch operation timed out", latch.await(60, TimeUnit.SECONDS));

        assertNull("Fetched articles list should be null for an invalid API key", result[0]);
    }

    /**
     * Tests the fetch articles functionality with different query strings.
     *
     * @throws InterruptedException If the operation is interrupted.
     */
    @Test
    public void testFetchArticlesWithDifferentQueries() throws InterruptedException {
        final int EXPECTED_ARTICLES = 5;
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        final List<Article>[] result1 = new List[1];
        final List<Article>[] result2 = new List[1];

        ArticleRequester articleRequester = new ArticleRequester();
        articleRequester.fetchNewsAsync("Technology", "popularity", EXPECTED_ARTICLES, new FetchNewsCallback() {
            @Override
            public void onFetchNewsComplete(List<Article> articles) {
                result1[0] = articles;
                latch1.countDown();
            }
        });
        articleRequester.fetchNewsAsync("Sports", "popularity", EXPECTED_ARTICLES, new FetchNewsCallback() {
            @Override
            public void onFetchNewsComplete(List<Article> articles) {
                result2[0] = articles;
                latch2.countDown();
            }
        });

        assertTrue("Fetch operation for Technology timed out", latch1.await(60, TimeUnit.SECONDS));
        assertTrue("Fetch operation for Sports timed out", latch2.await(60, TimeUnit.SECONDS));

        assertNotNull("Fetched articles list for Technology should not be null", result1[0]);
        assertFalse("Fetched articles list for Technology should not be empty", result1[0].isEmpty());

        assertNotNull("Fetched articles list for Sports should not be null", result2[0]);
        assertFalse("Fetched articles list for Sports should not be empty", result2[0].isEmpty());
    }

    /**
     * Tests the fetch articles functionality with null response.
     */
    @Test
    public void testFetchArticlesNullResponse() {
        ArticleRequester articleRequester = new ArticleRequester() {
            @Override
            public List<Article> fetchNews(String query, String sortBy, int numberOfArticles) {
                return null; // Simulate null response
            }
        };

        List<Article> articles = articleRequester.fetchNews("Test", "relevancy", 10);
        assertNull("Fetched articles list should be null", articles);
    }

    /**
     * Tests the fetch articles functionality with an empty response.
     */
    @Test
    public void testFetchArticlesEmptyResponse() {
        ArticleRequester articleRequester = new ArticleRequester() {
            @Override
            public List<Article> fetchNews(String query, String sortBy, int numberOfArticles) {
                return Collections.emptyList(); // Simulate empty response
            }
        };

        List<Article> articles = articleRequester.fetchNews("Test", "relevancy", 10);
        assertNotNull("Fetched articles list should not be null", articles);
        assertTrue("Fetched articles list should be empty", articles.isEmpty());
    }

    /**
     * Tests the fetch articles functionality synchronously.
     */
    @Test
    public void testFetchArticlesSync() {
        final int EXPECTED_ARTICLES = 10;

        ArticleRequester articleRequester = new ArticleRequester();
        List<Article> articles = articleRequester.fetchNews("Calgary", "popularity", EXPECTED_ARTICLES);

        assertNotNull("Fetched articles list should not be null", articles);
        assertFalse("Fetched articles list should not be empty", articles.isEmpty());
        assertEquals("Fetched articles list size should be as expected", EXPECTED_ARTICLES, articles.size());

        // Check the first article
        Article firstArticle = articles.get(0);
        assertNotNull("Article should not be null", firstArticle);
        assertNotNull("Article title should not be null", firstArticle.title());
        assertNotNull("Article description should not be null", firstArticle.description());
        assertNotNull("Article content should not be null", firstArticle.content());
    }

    /**
     * Tests the constructor of Article class.
     */
    @Test
    public void testArticleConstructor() {
        String title = "Sample Title";
        String description = "Sample Description";
        String content = "Sample Content";

        Article article = new Article(title, description, content);

        assertEquals("Article title should match", title, article.title());
        assertEquals("Article description should match", description, article.description());
        assertEquals("Article content should match", content, article.content());
    }
}
