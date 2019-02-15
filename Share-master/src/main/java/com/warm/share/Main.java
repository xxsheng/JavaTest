package com.warm.share;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class Main {

    public static void main(String[] args) {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString("http://example.com/hotels/{hotel}/bookings/{booking}").build();
        URI uri = uriComponents.expand("42", "21").encode().toUri();
        System.out.println(uri.toString());
    }

}
