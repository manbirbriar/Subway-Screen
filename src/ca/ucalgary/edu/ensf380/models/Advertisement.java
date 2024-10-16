package ca.ucalgary.edu.ensf380.models;

/**
 * The Advertisement class represents an advertisement with a title,
 * description, and media path.
 *
 * @param title The title of the advertisement.
 * @param subtitle The subtitle of the advertisement.
 * @param description The description of the advertisement.
 * @param mediaPath The path to the media associated with the advertisement.
 */
public record Advertisement(String title, String description, String mediaPath) {
}
