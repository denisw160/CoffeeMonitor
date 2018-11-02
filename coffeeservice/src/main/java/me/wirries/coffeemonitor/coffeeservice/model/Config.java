package me.wirries.coffeemonitor.coffeeservice.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * This is a model class for the configuration of the system.
 *
 * @author denisw
 * @version 1.0
 * @since 02.11.2018
 */
@Document(collection = "config")
public class Config {

    @Id
    private String id;

    private Date timestamp;
    private Double maxWeight;

    public Config() {
        // default constructor
    }

    public Config(Double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(Double maxWeight) {
        this.maxWeight = maxWeight;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("timestamp", timestamp)
                .append("maxWeight", maxWeight)
                .toString();
    }

}
