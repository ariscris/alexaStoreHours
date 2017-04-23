package com.ariscris.storeHours;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.PlacesApi;
import com.google.maps.model.*;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoogleMapsClient {
    private static final String API_KEY = "AIzaSyBp1rZSe6Vq0bKIurb0v8Tiriikf5fjuM8";
    private static final String DEFAULT_ZIPCODE = "07013";

    private static final Logger log = LoggerFactory.getLogger(GoogleMapsClient.class);

    public static String getOpeningTime(String place, String zipCode) throws Exception {
        log.info("getOpeningTime place={}", place);
        OpeningHours.Period openingPeriod = getOpeningHours(place, zipCode);
        if (openingPeriod != null) {
            String openingTime = getTimeSpeech(openingPeriod.open.time);
            log.info("getOpeningTime place={}, openingTime={}", place, openingTime);
            return openingTime;
        } else {
            throw new Exception("Couldn't figure out the opening time.");
        }
    }

    public static String getClosingTime(String place, String zipCode) throws Exception {
        log.info("getClosingTime place={}, zipCode={}", place, zipCode);
        OpeningHours.Period openingPeriod = getOpeningHours(place, zipCode);
        String openingTime =  getTimeSpeech(openingPeriod.close.time);
        log.info("getClosingTime place={}, openingTime={}", place, openingTime);
        return openingTime;
    }

    private static OpeningHours.Period getOpeningHours(String place, String zipCode) throws Exception {
        PlacesSearchResult placesSearchResult = searchForPlace(place, zipCode);
        if (placesSearchResult != null && placesSearchResult.openingHours != null && placesSearchResult.openingHours.periods.length > 0) {
            OpeningHours.Period openingHours = placesSearchResult.openingHours.periods[0];
            log.info("openingHours = {}", openingHours);
            return openingHours;
        } else {
            return null;
        }
    }

    private static PlacesSearchResult searchForPlace(String keyword, String zipCode) throws Exception {
        log.info("searchForPlace keyword={}, zipCode={}", keyword, zipCode);

        //https://maps.googleapis.com/maps/api/geocode/json?address=07013&key=AIzaSyBp1rZSe6Vq0bKIurb0v8Tiriikf5fjuM8
        GeoApiContext geoApicontext = new GeoApiContext().setApiKey(API_KEY);
        GeocodingResult[] results = GeocodingApi.geocode(geoApicontext, zipCode).await();
        LatLng location;
        if (results.length > 0) {
            location = results[0].geometry.location;
            log.info("latitude={}, longitude={}", location.lat, location.lng);
        } else {
            log.warn("geocoding failed. Falling back to " + DEFAULT_ZIPCODE);
            results = GeocodingApi.geocode(geoApicontext, DEFAULT_ZIPCODE).await();
            location = results[0].geometry.location;
        }

        //https://maps.googleapis.com/maps/api/place/textsearch/xml?query=target&location=40.8663719,-74.1768412&key=AIzaSyBp1rZSe6Vq0bKIurb0v8Tiriikf5fjuM8
        PlacesSearchResponse response  = PlacesApi.textSearchQuery(geoApicontext, keyword).await(); //.location(location).await();
        log.info("PlacesApi.textSearchQuery found {} results ", response.results.length);
        if (results.length > 0) {
            PlacesSearchResult result = response.results[0];
            log.info("PlacesApi.textSearchQuery found {} at {} ", result.name, result.formattedAddress);
            return response.results[0];
        } else {
            return null;
        }
    }

    private static String getTimeSpeech(LocalTime time) {
        String speech = "";
        boolean isMorning;
        if (time.getHourOfDay() <= 12) {
            speech += " " + String.valueOf(time.getHourOfDay());
            isMorning = true;
        } else {
            speech += " " + String.valueOf(time.getHourOfDay() - 12);
            isMorning = false;
        }

        if (time.getMinuteOfHour() != 0) {
            speech += " " + time.getMinuteOfHour();
        }

        if (isMorning) {
            speech += " AM";
        } else {
            speech += " PM";
        }
        return speech;
    }

}
