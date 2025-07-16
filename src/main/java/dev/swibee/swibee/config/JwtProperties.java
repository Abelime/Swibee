package dev.swibee.swibee.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    
    private String secret;
    private AccessToken accessToken = new AccessToken();
    private RefreshToken refreshToken = new RefreshToken();
    
    @Getter
    @Setter
    public static class AccessToken {
        private long expiration;
    }
    
    @Getter
    @Setter
    public static class RefreshToken {
        private long expiration;
    }
} 