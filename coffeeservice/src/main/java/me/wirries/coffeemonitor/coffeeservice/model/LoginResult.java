package me.wirries.coffeemonitor.coffeeservice.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * This class is a simple POJO for the result of the login.
 *
 * @author denisw
 * @version 1.0
 * @since 06.01.2019
 */
public class LoginResult {

    private boolean success;

    public LoginResult(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("success", success)
                .toString();
    }

}
