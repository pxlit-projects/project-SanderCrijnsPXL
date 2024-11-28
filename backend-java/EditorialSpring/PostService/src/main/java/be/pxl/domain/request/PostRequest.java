package be.pxl.domain.request;

import java.util.Date;

public record PostRequest(String title, String content, String author) {}