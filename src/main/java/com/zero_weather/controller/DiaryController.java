package com.zero_weather.controller;

import com.zero_weather.domain.Diary;
import com.zero_weather.service.DiaryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class DiaryController {

    private final DiaryService diaryService;
    
    @ApiOperation(value = "텍스트와 날씨를 이용해 DB에 일기 저장")
    @PostMapping("/diary")
    void createDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                     @ApiParam(value = "날짜형식 : yyyy-MM-dd", example = "2022-09-14")
                             LocalDate date,
                     @RequestBody String text) {
        diaryService.createDiary(date, text);
    }

    @ApiOperation(value = "선택한 날에 모든 일기를 가져옵니다.")
    @GetMapping("/diary")
    List<Diary> read(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                     @ApiParam(value = "날짜형식 : yyyy-MM-dd", example = "2022-09-14")
                             LocalDate date) {
        return diaryService.readDiary(date);
    }

    @ApiOperation(value = "선택한 기간의 모든 일기를 가져옵니다.")
    @GetMapping("/diaries")
    List<Diary> readDiaries(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                            @ApiParam(value = "조회할 기간의 시작일", example = "2022-09-13")
                                    LocalDate startDate,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                            @ApiParam(value = "조회할 기간의 종료일", example = "2022-09-15")
                                    LocalDate endDate) {
        return diaryService.readDiaries(startDate, endDate);
    }

    @ApiOperation(value = "선택한 날의 작성한 일기를 수정합니다.")
    @PutMapping("diary")
    void update(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                @ApiParam(value = "날짜형식 : yyyy-MM-dd", example = "2022-09-14")
                        LocalDate date,
                @RequestBody String text) {
        diaryService.updateDiary(date, text);
    }
    @ApiOperation(value = "선택한 날의 일기를 모두 삭제합니다.")
    @DeleteMapping("diary")
    void delete(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                @ApiParam(value = "날짜형식 : yyyy-MM-dd", example = "2022-09-14")
                        LocalDate date) {
        diaryService.delete(date);
    }
}
