package ca.ucalgary.edu.ensf380.news;

import ca.ucalgary.edu.ensf380.models.Article;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * The ArticleDisplay class is responsible for displaying news articles
 * in a JPanel. It cycles through the articles every 10 seconds, displaying
 * each article's title and description.
 */
public class ArticleDisplay extends JPanel {
    /**
     * The Area where the article is displayed.
     */
    private final JTextArea articleTitleLabel;

    /**
     * The Area where the description of article is displayed.
     */
    private final JTextArea articleDescriptionArea;

    /**
     * List of Articles fetched from the API.
     */
    private final List<Article> articles;

    /**
     * The index of currently displayed article.
     */
    private int currentArticleIndex = 0;

    /**
     * Constructs an ArticleDisplay panel with a list of articles.
     *
     * @param articles The list of articles to display.
     */
    public ArticleDisplay(List<Article> articles) {
        this.articles = articles;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(255, 250, 240));
        setBorder(BorderFactory.createTitledBorder("Latest News"));

        articleTitleLabel = new JTextArea(2, 20);
        articleTitleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        articleTitleLabel.setLineWrap(true);
        articleTitleLabel.setWrapStyleWord(true);
        articleTitleLabel.setEditable(false);
        articleTitleLabel.setForeground(new Color(70, 130, 180));
        articleTitleLabel.setBackground(new Color(255, 250, 240));

        articleDescriptionArea = new JTextArea(3, 20);
        articleDescriptionArea.setFont(new Font("Arial", Font.ITALIC, 14));
        articleDescriptionArea.setLineWrap(true);
        articleDescriptionArea.setWrapStyleWord(true);
        articleDescriptionArea.setEditable(false);
        articleDescriptionArea.setBackground(new Color(255, 250, 240));

        add(articleTitleLabel);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(articleDescriptionArea);

        // Set up timer for article rotation
        new Timer(10000, e -> displayNextArticle()).start();

        // Initial display
        displayNextArticle();
    }

    /**
     * Displays the next article from the list. Cycles through the list of articles.
     */
    private void displayNextArticle() {
        if (articles.isEmpty()) return;

        Article article = articles.get(currentArticleIndex);
        articleTitleLabel.setText(article.title());
        articleDescriptionArea.setText(article.description());

        currentArticleIndex = (currentArticleIndex + 1) % articles.size();
    }
}
