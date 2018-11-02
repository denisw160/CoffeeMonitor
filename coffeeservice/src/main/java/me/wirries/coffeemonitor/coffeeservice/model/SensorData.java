package me.wirries.coffeemonitor.coffeeservice.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * This is a model class for the values of the coffee sensors.
 *
 * @author denisw
 * @version 1.0
 * @since 31.10.2018
 */
@Document(collection = "data")
public class SensorData {

    @Id
    private String id;

    private Date timestamp;
    private Double weight;
    private Boolean allocated;

    public SensorData() {
        // default constructor
    }

    public SensorData(Date timestamp) {
        this.timestamp = timestamp;
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

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Boolean getAllocated() {
        return allocated;
    }

    public void setAllocated(Boolean allocated) {
        this.allocated = allocated;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("timestamp", timestamp)
                .append("weight", weight)
                .append("allocated", allocated)
                .toString();
    }

}
