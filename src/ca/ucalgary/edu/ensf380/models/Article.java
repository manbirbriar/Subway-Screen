package ca.ucalgary.edu.ensf380.models;

/**
 * The Article class represents a news article with a title, description,
 * and content.
 *
 * @param title The title of the article.
 * @param description The description of the article.
 * @param content The content of the article.
 */
public record Article(String title, String description, String content) {
}
