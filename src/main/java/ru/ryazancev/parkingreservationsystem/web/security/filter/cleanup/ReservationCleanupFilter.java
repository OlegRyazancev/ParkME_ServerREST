package ru.ryazancev.parkingreservationsystem.web.security.filter.cleanup;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.filter.GenericFilterBean;

@AllArgsConstructor
public class ReservationCleanupFilter extends GenericFilterBean {

    private final ReservationCleanUpFilterProvider cleanUpFilterProvider;

    @SneakyThrows
    @Override
    public void doFilter(final ServletRequest request,
                         final ServletResponse response,
                         final FilterChain chain) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        try {
            if (httpRequest.getMethod().equals("GET")) {
                cleanUpFilterProvider.clean();
            }
        } catch (Exception ignored) {
        }
        chain.doFilter(request, response);
    }
}
