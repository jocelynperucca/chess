package service;

import model.RegisterRequest;
import model.RegisterResult;

import java.util.UUID;

public class RegisterService {
    public void delete() throws Exception {
        throw new Exception("test delete");
    }

    public void post() throws Exception {
        throw new Exception("test");
    }

    public RegisterResult register(RegisterRequest registerRequest) {
        if(registerRequest.email() == null || registerRequest.userName() == null || registerRequest.password() == null) {

        }
    }
}
