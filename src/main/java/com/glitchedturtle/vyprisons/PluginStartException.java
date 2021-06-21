package com.glitchedturtle.vyprisons;

public class PluginStartException extends Exception {

    private String _title;
    private String _description;

    public PluginStartException(String title, String description) {
        _title = title;
        _description = description;
    }

    public PluginStartException(Throwable cause, String title, String description) {
        super(cause);
        _title = title;
        _description = description;
    }

    public String getTitle() {
        return _title;
    }

    public String getDescription() {
        return _description;
    }

}
