package org.example.chatservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.chatservice.domain.emum.Gender;

import java.time.LocalDate;

/**
 * @author jiyoung
 */
@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    Long id;

    String email;

    String nickname;

    String name;

    @Enumerated(EnumType.STRING)
    Gender gender;

    String phoneNumber;

    LocalDate birthDay;

    String role;
}
