package ru.ryazancev.parkingreservationsystem.web.dto.auth;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtRequest {

    @NotNull(message = "Username must be not null")
    private String username;

    @NotNull(message = "Password must be not null")
    private String password;
}
