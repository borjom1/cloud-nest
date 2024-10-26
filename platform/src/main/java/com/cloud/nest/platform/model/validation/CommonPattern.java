package com.cloud.nest.platform.model.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonPattern {

    public static final String ONLY_LETTERS = "^[a-zA-Z]+$";
    public static final String AT_LEAST_ONE_SPECIAL_CHAR = ".*[^a-zA-Z0-9_]+.*";
    public static final String USERNAME_REGEX = "^[a-zA-Z][a-zA-Z0-9_]*$";
    public static final String FILENAME_REGEX = "^[A-Za-z0-9_]+$";
    public static final String FILE_EXTENSION_REGEX = "^[A-Za-z0-9]+$";

}
