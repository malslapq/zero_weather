package com.zero_weather.Repository;

import com.zero_weather.domain.DateWeather;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DateWeatherRepository extends JpaRepository<DateWeather, LocalDate> {

    List<DateWeather> findAllByDate(LocalDate date);

}
