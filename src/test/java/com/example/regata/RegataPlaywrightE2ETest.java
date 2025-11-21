package com.example.regata;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegataPlaywrightE2ETest {

    private static Playwright playwright;
    private static Browser browser;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(true)); // pon false para ver el navegador
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @Test
    void login_y_ver_lobby_de_partidas() {
        BrowserContext context = browser.newContext();
        Page page = context.newPage();

        // 1) Ir al front de Angular
        page.navigate("http://localhost:4200/");

        // 2) Llenar login con Player1 (creado en DbInitializer)
        page.locator("[data-testid='login-email']")
                .fill("player1@regata.com");
        page.locator("[data-testid='login-password']")
                .fill("Player1123");
        page.locator("[data-testid='login-submit']").click();

        // 3) Esperar que cargue el lobby
        page.waitForSelector("[data-testid='lobby-title']");

        String titulo = page.locator("[data-testid='lobby-title']").innerText();
        assertThat(titulo).containsIgnoringCase("partida");

        // 4) Comprobar que hay tabla de partidas (inicialmente puede estar vacía)
        boolean tablaVisible = page.locator("[data-testid='tabla-partidas']").isVisible();
        assertThat(tablaVisible).isTrue();

        context.close();
    }

    @Test
    void crear_partida_desde_la_ui() {
        BrowserContext context = browser.newContext();
        Page page = context.newPage();

        // Login igual que antes
        page.navigate("http://localhost:4200/");
        page.locator("[data-testid='login-email']").fill("player1@regata.com");
        page.locator("[data-testid='login-password']").fill("Player1123");
        page.locator("[data-testid='login-submit']").click();
        page.waitForSelector("[data-testid='lobby-title']");

        // 1) Click en "Crear partida"
        page.locator("[data-testid='crear-partida-btn']").click();

        // Aquí depende de tu modal/formulario de creación.
        // Ejemplo: si tienes un campo nombre y un botón guardar con data-testid:
        page.locator("[data-testid='crear-nombre-partida']")
                .fill("Partida Playwright");
        page.locator("[data-testid='crear-partida-confirmar']").click();

        // 2) Verificamos que en la tabla aparezca una fila con ese nombre
        page.waitForTimeout(1000); // pequeño delay para que refresque
        String tableText = page.locator("[data-testid='tabla-partidas']").innerText();
        assertThat(tableText).contains("Partida Playwright");

        context.close();
    }
}
