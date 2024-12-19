package com.example.demo.unit.controllerTest;

import com.example.demo.config.WebConfig;
import com.example.demo.controller.ItemController;
import com.example.demo.dto.ItemRequestDto;
import com.example.demo.service.ItemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ItemController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebConfig.class})
})
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    @Test
    @DisplayName("아이템 생성 성공")
    void createItemSuccess() throws Exception{
        //given
        ItemRequestDto requestDto = new ItemRequestDto("ItemName", "Description", 1L, 2L);
        String requestBody = """
                {
                    "name": "Test Item",
                    "description": "Test Description",
                    "managerId": 1,
                    "ownerId": 2
                }
                """;

        doNothing().when(itemService).createItem("ItemName", "Description", 1L, 2L);

        //when, then
        mockMvc.perform(post("/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("아이템이 성공적으로 생성되었습니다."));
    }

    @Test
    @DisplayName("아이템 생성 실패 - 필드 누락")
    void createItemFailMissField() throws Exception{
        //given
        ItemRequestDto requestDto = new ItemRequestDto("ItemName", "Description", 1L, 2L);
        String requestBody = """
                {
                    "description": "Test Description",
                    "managerId": 1,
                    "ownerId": 2
                }
                """;

        //when, then
        mockMvc.perform(post("/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("아이템 생성 실패 - 잘못된 데이터 형식")
    void createItemFailInvaildData() throws Exception{
        //given
        ItemRequestDto requestDto = new ItemRequestDto("ItemName", "Description", 1L, 2L);
        String requestBody = """
                {
                    "name": "Test Item",
                    "description": "Test Description",
                    "managerId": "invalid",
                    "ownerId": 2
                }
                """;

        //when, then
        mockMvc.perform(post("/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
