package ru.ryazancev.parkingreservationsystem.web.dto.auth;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtResponse {

    private Long id;
    private String username;
    private String accessToken;
    private String refreshToken;
}
