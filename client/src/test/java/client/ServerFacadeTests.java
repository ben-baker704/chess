package client;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + Integer.toString(port));
    }

    @AfterEach
    void clearFacade() {
        try {
            facade.clear();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void registerPositive() throws Exception {
        UserData data = new UserData("user", "pass", "mail");
        var authData = facade.register(data);
        Assertions.assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerNegative() throws Exception {
        UserData data = new UserData(null, "pass", "mail");
        Assertions.assertThrows(Exception.class, () -> facade.register(data));
    }

    @Test
    void loginPositive() throws Exception {
        UserData data = new UserData("user", "pass", "mail");
        facade.register(data);
        var authData = facade.login("user", "pass");
        Assertions.assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void loginNegative() throws Exception {
        Assertions.assertThrows(Exception.class, () -> facade.login("user", "pass"));
    }

    @Test
    void logoutPositive() throws Exception {
        UserData userData = new UserData("user", "pass", "mail");
        AuthData data = facade.register(userData);
        Assertions.assertDoesNotThrow(() -> facade.logout(data.authToken()));
    }

    @Test
    void logoutNegative() throws Exception {
        Assertions.assertThrows(Exception.class, () ->
                facade.logout("invalid"));
    }

    @Test
    void clearPositive() throws Exception {
        Assertions.assertDoesNotThrow(() -> facade.clear());
    }

}
