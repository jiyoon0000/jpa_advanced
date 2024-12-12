package com.example.demo.service;

import com.example.demo.entity.RentalLog;
import com.example.demo.repository.RentalLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RentalLogService {
    private final RentalLogRepository rentalLogRepository;

    public RentalLogService(RentalLogRepository rentalLogRepository) {
        this.rentalLogRepository = rentalLogRepository;
    }

    @Transactional
    public void save(RentalLog rentalLog) {
        rentalLogRepository.save(rentalLog);
        //주석 처리하지 않아도 All or Nothing 원칙은 지켜지지만, 트랜잭션이 무조건 롤백되어 데이터 저장이 되지 않음
        //주석 처리를 하여 예외 없이 정상적인 데이터 저장이 되는걸 확인
//        if (rentalLog != null) {
//            throw new RuntimeException();
//        }
    }
}
