package com.app.salty.user.service;

import com.app.salty.user.entity.Users;
import com.app.salty.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(UserServiceTest.class);


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // @Mock으로 설정된 객체 초기화
    }

    @Test
    public void userList() throws Exception{
        //given

        //when
        userRepository.findAll().forEach(System.out::println);
    }

}