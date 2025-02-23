package nextstep.security.config;

// NOTE: https://github.com/spring-projects/spring-security/blob/main/config/src/main/java/org/springframework/security/config/Customizer.java
@FunctionalInterface
public interface Customizer<T> {
    static <T> Customizer<T> withDefaults() {
        return (t) -> {};
    }

    void customize(T t);
}
