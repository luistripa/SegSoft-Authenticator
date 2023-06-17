package api.access_control;

public enum Operation {
    READ_PAGE,
    SUBMIT_FOLLOW_REQUEST,
    APPROVE_FOLLOW_REQUEST,
    READ_POST,
    CREATE_POST,
    DELETE_POST,
    LIKE_UNLIKE_POST,

    // Admin only
    CREATE_PAGE,
    DELETE_PAGE
}
