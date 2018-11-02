package me.wirries.coffeemonitor.coffeeservice.model;

/**
 * This is the response of an request via the REST-API.
 *
 * @author denisw
 * @version 1.0
 * @since 02.11.2018
 */
public interface RestResponse<T> {

    /**
     * Return the HTTP Status Code.
     *
     * @return HTTP Status Code
     */
    int getStatusCode();

    /**
     * Return additional information to the sender.
     *
     * @return Optional additional information
     */
    String getMessage();


    /**
     * In case of an error, this contains a error message.
     *
     * @return Optional message with error details
     */
    String getError();

    /**
     * Returns an object for the sender.
     *
     * @return Optional additional object
     */
    T getObject();

}
