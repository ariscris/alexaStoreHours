package com.ariscris.storeHours;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.OpeningHours;
import com.google.maps.model.PlaceDetails;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoogleMapsProxy {
    private static final String API_KEY = "AIzaSyBp1rZSe6Vq0bKIurb0v8Tiriikf5fjuM8";

    private static final Logger log = LoggerFactory.getLogger(GoogleMapsProxy.class);

    public static String getOpeningTime(String place) throws Exception {
        log.info("getOpeningTime place={}", place);
        OpeningHours.Period openingPeriod = getOpeningHours(place);
        String openingTime =  getTimeSpeech(openingPeriod.open.time);
        log.info("getOpeningTime place={}, openingTime={}", place, openingTime);
        return openingTime;
    }

    public static String getClosingTime(String place) throws Exception {
        log.info("getClosingTime place={}", place);
        OpeningHours.Period openingPeriod = getOpeningHours(place);
        String openingTime =  getTimeSpeech(openingPeriod.close.time);
        log.info("getClosingTime place={}, openingTime={}", place, openingTime);
        return openingTime;
    }

    private static OpeningHours.Period getOpeningHours(String place) throws Exception {
        GeoApiContext context = new GeoApiContext().setApiKey(API_KEY);
        PlaceDetails placeDetails = PlacesApi.placeDetails(context, "ChIJN1t_tDeuEmsRUsoyG83frY4").await();
        return placeDetails.openingHours.periods[0];
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
