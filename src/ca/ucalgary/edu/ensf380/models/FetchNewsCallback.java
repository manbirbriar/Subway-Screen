package ca.ucalgary.edu.ensf380.models;

import java.util.List;

/**
 * The FetchNewsCallback interface defines a callback method for handling
 * the completion of a news fetch operation.
 */
public interface FetchNewsCallback {
    /**
     * Called when the news fetch operation is complete.
     *
     * @param articles The list of fetched articles.
     */
    void onFetchNewsComplete(List<Article> articles);
}
