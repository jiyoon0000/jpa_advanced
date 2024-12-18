package com.example.demo.unit.serviceTest;

import com.example.demo.entity.RentalLog;
import com.example.demo.repository.RentalLogRepository;
import com.example.demo.service.RentalLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalLogServiceTest {

    @Mock
    private RentalLogRepository rentalLogRepository;

    @InjectMocks
    private RentalLogService rentalLogService;

    @Test
    @DisplayName("렌탈로그 저장 성공")
    void saveRentalLogSuccess(){
        //given
        RentalLog rentalLog = mock(RentalLog.class);

        //when
        rentalLogService.save(rentalLog);

        //then
        verify(rentalLogRepository, times(1)).save(any(RentalLog.class));
    }

    @Test
    @DisplayName("렌탈로그 저장 실패")
    void saveRentalLogFail(){
        //given
        RentalLog rentalLog = mock(RentalLog.class);

        doThrow(new RuntimeException("데이터 저장 중 예외 발생"))
                .when(rentalLogRepository).save(any(RentalLog.class));

        //when, then
        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> rentalLogService.save(rentalLog));
        verify(rentalLogRepository, times(1)).save(any(RentalLog.class));
        assert exception.getMessage().equals("데이터 저장 중 예외 발생");
    }
}
