package org.example.chatservice.global.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.example.chatservice.domain.emum.Gender;
import org.example.chatservice.domain.emum.Role;
import org.example.chatservice.domain.entity.Member;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * @author jiyoung
 */
public class MemberFactory {

    public static Member create(OAuth2User oAuth2User, OAuth2UserRequest userRequest) {
        return switch (userRequest.getClientRegistration().getRegistrationId()) {
            case "kakao" -> {
                Map<String, Object> attributeMap = oAuth2User.getAttribute("kakao_account");
                yield Member.builder()
                        .email((String) attributeMap.get("email"))
                        .nickname((String) ((Map) attributeMap.get("profile")).get("nickname"))
                        .name((String) attributeMap.get("name"))
                        .phoneNumber((String) attributeMap.get("phone_number"))
                        .gender(Gender.valueOf(((String) attributeMap.get("gender")).toUpperCase()))
                        .birthDay(getBirthDay(attributeMap))
                        .role(Role.USER.getCode())
                        .build();
            }
            case "google" -> {
                Map<String, Object> attributeMap = oAuth2User.getAttributes();
                yield Member.builder()
                        .email((String) attributeMap.get("email"))
                        .nickname((String) attributeMap.get("given_name"))
                        .name((String) attributeMap.get("name"))
                        .role(Role.USER.getCode())
                        .build();
            }
            default -> throw new IllegalArgumentException("연동되지 않은 서비스 입니다.");
        };
    }

    private static LocalDate getBirthDay(Map<String, Object> attributeMap) {
        String birthYear = attributeMap.get("birthyear").toString();
        String birthDay = attributeMap.get("birthday").toString();

        return LocalDate.parse(birthYear + birthDay, DateTimeFormatter.BASIC_ISO_DATE);
    }
}
