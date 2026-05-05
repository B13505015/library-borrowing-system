package library_api.dto;

// 對應前端的 AuthSession
public class AuthSessionResponse {

    private String token;
    private AppUserResponse user;

    public AuthSessionResponse() {
    }

    public AuthSessionResponse(String token, AppUserResponse user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AppUserResponse getUser() {
        return user;
    }

    public void setUser(AppUserResponse user) {
        this.user = user;
    }
}