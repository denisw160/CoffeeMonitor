package me.wirries.coffeemonitor.coffeeservice.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

/**
 * This is a model class shows the coffee consumption of one day.
 *
 * @author denisw
 * @version 1.0
 * @since 02.11.2018
 */
public class Consumption {

    private Date day;

    private Integer consumption;

    public Consumption() {
    }

    public Consumption(Date day) {
        this.day = day;
        consumption = 0;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public Integer getConsumption() {
        return consumption;
    }

    public void setConsumption(Integer consumption) {
        this.consumption = consumption;
    }

    public void incrementConsumption() {
        if (consumption == null) {
            consumption = 1;
        } else {
            consumption++;
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("day", day)
                .append("consumption", consumption)
                .toString();
    }
}
