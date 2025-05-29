package client;

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

    @AfterAll
    static void stopServer() {
        try {
            facade.clear();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
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
    void clearPositive() throws Exception {
        Assertions.assertDoesNotThrow(() -> facade.clear());
    }

}
