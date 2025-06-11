package backendProyectoParqueo.security;


public class Constants {
    public static final String ENCODER_ID = "bcrypt";
    public static final String API_URL_PREFIX = "/**";
    public static final String TOKEN_URL = "/auth/signIn";
    public static final String AUTHORIZATION = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final long EXPIRATION_TIME_ACCESS_TOKEN = 1000L * 60 * 15; // 15 mins
    public static final long EXPIRATION_TIME_REFRESH_TOKEN = 1000L * 60 * 60 * 24 * 7; // 7 dias
    public static final String ROLE_CLAIM = "roles";
    public static final String ID_CLAIM = "userId";
    public static final String AUTHORITY_PREFIX = "ROLE_";
}
