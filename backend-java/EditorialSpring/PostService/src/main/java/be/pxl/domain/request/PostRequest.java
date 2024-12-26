package be.pxl.domain.request;

import be.pxl.domain.PostStatus;

public record PostRequest(String title, String content, String author, PostStatus status) {}