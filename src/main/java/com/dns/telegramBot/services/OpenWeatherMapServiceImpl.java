package com.dns.telegramBot.services;

import com.dns.telegramBot.weatherElements.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class OpenWeatherMapServiceImpl implements OpenWeatherMapService{

    @Value("c72948f1a9951ab973c4bf812f5d21f8")
    private String appId;

    private final MessageSource messageSource;

    public OpenWeatherMapServiceImpl(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String getWeatherInfo(String message) {
        try {
            return createString(getResponse(message));
        }catch (FileNotFoundException e) {
            return messageSource.getMessage("notFound", null, Locale.ENGLISH);
        } catch (IOException e) {
            return "Something went wrong :(";
        }
    }

    private Result getResponse(String message) throws IOException {
        String s = String.format("http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s", message, appId);
        var url = new URL(s);
        return new ObjectMapper().readValue(url, Result.class);
    }

    private String createString(Result result) {
        String main = result.getName();
        double temp = result.getMain().getTemp();
        String description = result.getWeather().get(0).getDescription();
        int pressure = result.getMain().getPressure();
        int humidity = result.getMain().getHumidity();
        String sunrise = getTime(Long.sum(result.getSys().getSunrise(), result.getTimezone()));
        String sunset = getTime(Long.sum(result.getSys().getSunset(), result.getTimezone()));

        return "<i>Place: </i><b>" + main + "</b>\n" +
                "<i>Temperature: </i><b>" + temp + " C</b>\n" +
                "<i>Description: </i><b>" + description + "</b>\n" +
                "<i>Pressure: </i><b>" + pressure + " Pa</b>\n" +
                "<i>Humidity: </i><b>" + humidity + "%</b>\n" +
                "<i>Sunrise: </i><b>" + sunrise + "</b>\n" +
                "<i>Sunset: </i><b>" + sunset + "</b>";
    }

    private String getTime(long time) {
        var formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH)
                .withZone(ZoneId.of("UTC"));
        return formatter.format(Instant.ofEpochSecond(time));
    }
}