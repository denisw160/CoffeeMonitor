package me.wirries.coffeemonitor.coffeeservice.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A generic implementation of the {@link RestResponse}.
 *
 * @param <T> Type of the object
 * @author denisw
 * @version 1.0
 * @since 02.11.2018
 */
public class GenericResponse<T> implements RestResponse<T> {

    private int statusCode;
    private String message;
    private String error;
    private T object;

    public GenericResponse(int statusCode) {
        this.statusCode = statusCode;
    }

    public GenericResponse(int statusCode, String error) {
        this.statusCode = statusCode;
        this.message = error;
    }

    public GenericResponse(int statusCode, T object) {
        this.statusCode = statusCode;
        this.object = object;
    }

    public GenericResponse(int statusCode, String message, T object) {
        this.statusCode = statusCode;
        this.message = message;
        this.object = object;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getError() {
        return error;
    }

    @Override
    public T getObject() {
        return object;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("statusCode", statusCode)
                .append("message", message)
                .append("error", error)
                .toString();
    }

}
