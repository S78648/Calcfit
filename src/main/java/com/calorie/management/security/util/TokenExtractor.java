package com.calorie.management.security.util;

import jakarta.servlet.http.HttpServletRequest;

public interface TokenExtractor {

    String extract(HttpServletRequest request);

}

