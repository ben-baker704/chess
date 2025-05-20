package server;

import java.util.HashMap;

public class ErrorMessage {

    public HashMap<String, String> errorMessage(String message) {
        HashMap<String, String> result = new HashMap<>();
        result.put("message", message);
        return result;
    }
}
