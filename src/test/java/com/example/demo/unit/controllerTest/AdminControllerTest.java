package com.example.demo.unit.controllerTest;

import com.example.demo.config.WebConfig;
import com.example.demo.controller.AdminController;
import com.example.demo.dto.ReportRequestDto;
import com.example.demo.service.AdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AdminController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebConfig.class})
})
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @Test
    @DisplayName("사용자 신고 성공")
    void reportUsersSuccess() throws Exception{
        //given
        ReportRequestDto reportRequestDto = new ReportRequestDto(List.of(1L,2L,3L));
        //content에 JSON 데이터를 제공해야 하므로 JSON 데이터를 문자열로 작성(controller 계층)
        //dto 사용은 service 계층
        String requestBody = """
                {
                    "userIds": [1,2,3]
                }
                """;

        doNothing().when(adminService).reportUsers(anyList());

        //when, then
        mockMvc.perform(post("/admins/report-users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("사용자를 성공적으로 불러왔습니다."));

        verify(adminService, times(1)).reportUsers(anyList());
    }
}
