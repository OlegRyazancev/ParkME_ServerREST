package ru.ryazancev.parkingreservationsystem.config;

import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphqlConf {

    @Bean
    public GraphQLScalarType localDateTimeScalar(){
        return GraphQLScalarType.newScalar()
                .name("LocalDateTime")
                .description("LocalDateTimeScalar")
                .coercing(new LocalDateTimeCoercing())
                .build();
    }

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer(){
        return wiringBuilder -> wiringBuilder
                .scalar(localDateTimeScalar())
                .build();
    }
}
