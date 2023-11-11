package ru.ryazancev.config.testutils.paths;

public class APIPaths {

    public static final String ADMIN_CARS = APIPathBuilder.builder()
            .withBase(APIBase.V1)
            .withAdmin()
            .withPath(PathParts.CARS)
            .build();
    public static final String ADMIN_USERS = APIPathBuilder.builder()
            .withBase(APIBase.V1)
            .withAdmin()
            .withPath(PathParts.USERS)
            .build();
    public static final String ADMIN_ZONES = APIPathBuilder.builder()
            .withBase(APIBase.V1)
            .withAdmin()
            .withPath(PathParts.ZONES)
            .build();
    public static final String ADMIN_RESERVATIONS = APIPathBuilder.builder()
            .withBase(APIBase.V1)
            .withAdmin()
            .withPath(PathParts.RESERVATIONS)
            .build();

    public static final String ADMIN_RESERVATION_BY_ID = APIPathBuilder.builder()
            .withBase(APIBase.V1)
            .withAdmin()
            .withPath(PathParts.RESERVATIONS)
            .withId()
            .build();

    public static final String ADMIN_PLACE_BY_ID = APIPathBuilder.builder()
            .withBase(APIBase.V1)
            .withAdmin()
            .withPath(PathParts.PLACES)
            .withId()
            .build();

    public static final String ADMIN_ZONE_BY_ID = APIPathBuilder.builder()
            .withBase(APIBase.V1)
            .withAdmin()
            .withPath(PathParts.ZONES)
            .withId()
            .build();

    public static final String ADMIN_ZONE_PLACES = APIPathBuilder.builder()
            .withBase(APIBase.V1)
            .withAdmin()
            .withPath(PathParts.ZONES)
            .withId()
            .withPath(PathParts.PLACES)
            .build();

    public static final String ADMIN_PLACE_STATUS = APIPathBuilder.builder()
            .withBase(APIBase.V1)
            .withAdmin()
            .withPath(PathParts.PLACES)
            .withId()
            .withPath(PathParts.STATUS)
            .build();

    public static final String CARS = APIPathBuilder.builder()
            .withBase(APIBase.V1)
            .withPath(PathParts.CARS)
            .build();
    public static final String RESERVATIONS = APIPathBuilder.builder()
            .withBase(APIBase.V1)
            .withPath(PathParts.RESERVATIONS)
            .build();
    public static final String USERS = APIPathBuilder.builder()
            .withBase(APIBase.V1)
            .withPath(PathParts.USERS)
            .build();
    public static final String ZONES = APIPathBuilder.builder()
            .withBase(APIBase.V1)
            .withPath(PathParts.ZONES)
            .build();

    public static final String CAR_BY_ID = APIPathBuilder.builder()
            .withBase(APIBase.V1)
            .withPath(PathParts.CARS)
            .withId()
            .build();
    public static final String RESERVATION_BY_ID = APIPathBuilder.builder()
            .withBase(APIBase.V1)
            .withPath(PathParts.RESERVATIONS)
            .withId()
            .build();
    public static final String USER_BY_ID = APIPathBuilder.builder()
            .withBase(APIBase.V1)
            .withPath(PathParts.USERS)
            .withId()
            .build();
    public static final String ZONE_BY_ID = APIPathBuilder.builder()
            .withBase(APIBase.V1)
            .withPath(PathParts.ZONES)
            .withId()
            .build();

    public static final String USER_CARS = APIPathBuilder.builder()
            .withBase(APIBase.V1)
            .withPath(PathParts.USERS)
            .withId()
            .withPath(PathParts.CARS)
            .build();

    public static final String USER_RESERVATIONS = APIPathBuilder.builder()
            .withBase(APIBase.V1)
            .withPath(PathParts.USERS)
            .withId()
            .withPath(PathParts.RESERVATIONS)
            .build();

    public static final String ZONE_PLACES = APIPathBuilder.builder()
            .withBase(APIBase.V1)
            .withPath(PathParts.ZONES)
            .withId()
            .withPath(PathParts.PLACES)
            .build();

    public static final String ZONE_FREE_PLACES = APIPathBuilder.builder()
            .withBase(APIBase.V1)
            .withPath(PathParts.ZONES)
            .withId()
            .withPath(PathParts.PLACES)
            .withPath(PathParts.FREE)
            .build();
}
