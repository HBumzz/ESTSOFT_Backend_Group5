package com.app.salty.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long id;

    @Column(nullable = false)
    private LocalDate attendanceDate;

    @Transient
    private final Long rewardPoint = 100L;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private Users user;

    @Builder
    public Attendance(LocalDate attendanceDate, int attendanceDays, Users user) {
        this.attendanceDate = attendanceDate;
        this.user = user;
    }

    //연관관계 메서드
    public void addUser(Users user){
        this.user = user;
    }

    public void deleteUser() {
        this.user = null;
    }
}
