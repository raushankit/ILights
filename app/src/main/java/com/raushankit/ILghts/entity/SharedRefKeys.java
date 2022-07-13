package com.raushankit.ILghts.entity;

public enum SharedRefKeys {
    DEFAULT_VALUE("default-value"),
    AUTH_SUCCESSFUL("auth-successful"),
    AUTH_TYPE("auth-type"),
    USER_NAME("user-name"),
    USER_EMAIL("user-email"),
    FIRST_OPEN("first-open"),
    NOTIFY_UPDATE("notify-update"),
    SHOW_ANALYTICS_DIALOG("show-analytics-dialog"),
    PREV_SHOWN_ANALYTICS_DIALOG("prev_show_analytics_dialog"),
    EMAIL_VERIFICATION("email-verification"),
    BOARD_FORM_EMAIL("board_form_email"),
    BOARD_FORM_PASSWORD("board_form_password"),
    BOARD_FORM_ID_TOKEN("board_form_id_token"),
    BOARD_FORM_REFRESH_TOKEN("board_form_refresh_token");

    SharedRefKeys(String key) {
    }
}
