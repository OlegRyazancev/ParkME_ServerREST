package ru.ryazancev.parkingreservationsystem.web.security.expression;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.ryazancev.parkingreservationsystem.models.user.Role;
import ru.ryazancev.parkingreservationsystem.services.UserService;
import ru.ryazancev.parkingreservationsystem.web.security.JwtEntity;

@Service("customSecurityExpression")
@RequiredArgsConstructor
public class CustomSecurityExpression {

    private final UserService userService;

    public boolean canAccessUser(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        JwtEntity user = (JwtEntity) authentication.getPrincipal();
        Long userId = user.getId();

        return userId.equals(id) || hasAnyRole(authentication, Role.ROLE_ADMIN);
    }

    private boolean hasAnyRole(Authentication authentication, Role... roles) {
        for (Role role : roles) {
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.name());
            if (authentication.getAuthorities().contains(authority))
                return true;
        }
        return false;
    }

    public boolean canAccessCar(Long carId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        JwtEntity user = (JwtEntity) authentication.getPrincipal();
        Long userId = user.getId();

        return userService.isCarOwner(userId, carId)|| hasAnyRole(authentication, Role.ROLE_ADMIN);
    }

    public boolean canAccessReservation(Long reservationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        JwtEntity user = (JwtEntity) authentication.getPrincipal();
        Long userId = user.getId();

        return userService.isReservationOwner(userId, reservationId) || hasAnyRole(authentication, Role.ROLE_ADMIN);
    }
}
