package com.rbkmoney.fraudbusters.notificator.config;

import com.rbkmoney.testcontainers.annotations.DefaultSpringBootTest;
import com.rbkmoney.testcontainers.annotations.postgresql.PostgresqlTestcontainerSingleton;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PostgresqlTestcontainerSingleton
@DefaultSpringBootTest
public @interface PostgresqlSpringBootITest {
}
