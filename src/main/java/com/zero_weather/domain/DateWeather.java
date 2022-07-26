package com.zero_weather.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity(name = "date_weather")
public class DateWeather {

    @Id
    private LocalDate date;
    private String weather;
    private String icon;
    private double temperature;

}

