package com.zero_weather.service;

import com.zero_weather.Repository.DateWeatherRepository;
import com.zero_weather.Repository.DiaryRepository;
import com.zero_weather.ZeroWeatherApplication;
import com.zero_weather.domain.DateWeather;
import com.zero_weather.domain.Diary;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;
    private static final Logger logger = LoggerFactory.getLogger(ZeroWeatherApplication.class);

    @Value("${openApi.key}")
    private String apiKey;


    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) {

        DateWeather dateWeather = getDateWeather(date);

        Diary diary = Diary.builder()
                .text(text)
                .build();
        diary.setDateWeather(dateWeather);
        diaryRepository.save(diary);
        logger.info("created diary");
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
        logger.info("read diary");
        return diaryRepository.findAllByDate(date);
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        logger.info("read diaries");
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    @Transactional
    public void updateDiary(LocalDate date, String text) {
        Diary diary = diaryRepository.getFirstByDate(date);
        diary.updateDiary(text);
        diaryRepository.save(diary);
        logger.info("updated diary");
    }

    @Transactional
    public void delete(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
        logger.info("deleted diary");
    }

    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherDate() {
        dateWeatherRepository.save(getWeatherFromApi());
        logger.info("save weatherApi data");
    }

    private DateWeather getWeatherFromApi() {
        String weatherData = getWeatherString();
        Map<String, Object> parsedWeather = parserWeather(weatherData);
        return DateWeather.builder()
                .date(LocalDate.now())
                .weather(parsedWeather.get("main").toString())
                .icon(parsedWeather.get("icon").toString())
                .temperature((double) parsedWeather.get("temp"))
                .build();
    }

    private DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeathersFromDb = dateWeatherRepository.findAllByDate(date);

        if (dateWeathersFromDb.size() == 0) {
            return getWeatherFromApi();
        } else {
            return dateWeathersFromDb.get(0);
        }

    }

    private String getWeatherString() {
        String api = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;
        try {
            URL url = new URL(api);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            return response.toString();
        } catch (IOException e) {
            return "failed to get response";
        }

    }

    private Map<String, Object> parserWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject mainData = (JSONObject) jsonObject.get("main");
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("temp", mainData.get("temp"));
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));

        return resultMap;
    }

}
