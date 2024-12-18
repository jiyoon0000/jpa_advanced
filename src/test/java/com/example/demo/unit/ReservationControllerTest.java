package com.example.demo.unit;

import com.example.demo.config.WebConfig;
import com.example.demo.controller.ReservationController;
import com.example.demo.dto.ReservationRequestDto;
import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//ReservationController에 대한 단위 테스트
@WebMvcTest(value = ReservationController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebConfig.class})
})
class ReservationControllerTest {

    //MockMvc를 사용해 HTTP 요청 및 응답 테스트 수행
    @Autowired
    private MockMvc mockMvc;

    //ReservationService를 Mock 객체로 주입
    @MockitoBean
    private ReservationService reservationService;

    //ObjectMapper를 사용하여 객체를 JSON 문자열로 직렬화
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("예약 생성 성공 : POST")
    void createReservationSuccess() throws Exception {
        //given : 입력 데이터와 Mock 동작 설정
        ReservationRequestDto requestDto = new ReservationRequestDto(1L, 2L, LocalDateTime.now(), LocalDateTime.now());
        ReservationResponseDto responseDto = new ReservationResponseDto(1L, "testUser", "testItem", LocalDateTime.now(), LocalDateTime.now());

        given(reservationService.createReservation(anyLong(), anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(responseDto);

        //when, then : POST 요청 및 응답 확인
        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated()) //201 응답 검증
                .andExpect(jsonPath("$.id").value(1L)) //응답의 id 필드 값 검증
                .andExpect(jsonPath("$.nickname").value("testUser")) //응답의 nickname 필드 값 검증
                .andExpect(jsonPath("$.itemName").value("testItem")); //응답의 itemName 필드 값 검증
    }

    //필수 필드가 누락된 경우
    @Test
    @DisplayName("예약 생성 실패")
    void createReservationFail() throws Exception{
        //when, then : POST 요청에 빈 JSON 전달 시 400 BadRequest 응답 검증
        mockMvc.perform(post("/reservations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("예약 상태 업데이트 : PATCH")
    void updateReservationStatus() throws Exception {
        //given : Mock 동작 설정
        given(reservationService.updateReservationStatus(anyLong(), any(String.class)))
                .willReturn(null); // 성공 시 반환이 없다고 가정

        //when, then : PATCH 요청 및 응답 확인, 200OK
        mockMvc.perform(patch("/reservations/1/update-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("APPROVED"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("예약 상태 업데이트 실패")
    void updateReservationStatusFail() throws Exception{
        //given : Mock 동작 설정
        given(reservationService.updateReservationStatus(anyLong(),any(String.class)))
                .willThrow(new IllegalArgumentException("해당 예약 ID가 존재하지 않음"));

        //when, then : PATCH 요청 및 응답 확인, 400 BadRequest
        mockMvc.perform(patch("/reservations/999/update-status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("APPROVED"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("해당 예약 ID가 존재하지 않음"));
    }

    @Test
    @DisplayName("예약 전체 조회 : GET")
    void getAllReservations() throws Exception {
        //given
        ReservationResponseDto responseDto = new ReservationResponseDto(1L, "testUser", "testItem", LocalDateTime.now(), LocalDateTime.now());

        given(reservationService.getReservations()).willReturn(List.of(responseDto));

        //when, then
        mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nickname").value("testUser"))
                .andExpect(jsonPath("$[0].itemName").value("testItem"));
    }

    @Test
    @DisplayName("예약 검색 : GET")
    void searchReservations() throws Exception {
        //given
        ReservationResponseDto responseDto = new ReservationResponseDto(1L, "testUser", "testItem", LocalDateTime.now(), LocalDateTime.now());

        given(reservationService.searchAndConvertReservations(anyLong(), anyLong()))
                .willReturn(List.of(responseDto));

        //when, then : 파라미터 포함 GET 요청 및 응답 검증
        mockMvc.perform(get("/reservations/search")
                        .param("userId", "1")
                        .param("itemId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nickname").value("testUser"))
                .andExpect(jsonPath("$[0].itemName").value("testItem"));
    }

    @Test
    @DisplayName("예약 검색 전체 조회 : GET")
    void searchReservationsWithoutParams() throws Exception{
        //given
        ReservationResponseDto responseDto = new ReservationResponseDto(1L,"testUser", "testItem", LocalDateTime.now(), LocalDateTime.now());

        given(reservationService.searchAndConvertReservations(null,null))
                .willReturn(List.of(responseDto));

        //when, then : 파라미터 없이 GET 요청 및 응답 검증
        mockMvc.perform(get("/reservations/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nickname").value("testUser"))
                .andExpect(jsonPath("$[0].itemName").value("testItem"));
    }
}