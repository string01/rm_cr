package com.ocr.cash_register;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * This started out as a spring-boot app using spring-shell v1
 * However, that shell wouldn't allow for the input format
 * needed.
 * The project cash-register-shell uses cash-register as a dependency.
 */
@Deprecated
@SpringBootApplication
@Slf4j
public class CashRegisterApplication /*implements CommandLineRunner*/ {

    @Autowired
    private CashRegister cashRegister;

    private static ApplicationContext ctx;

    public static void main(String[] args) {
        SpringApplication.run(CashRegisterApplication.class, args);
    }
}
