package com.example.jablog;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:jablog-context;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "minio.endpoint=http://localhost:9000",
        "minio.access.key=minio",
        "minio.secret.key=test-secret",
        "jwt.key=./key.pem"
})
@Disabled("Тесты отключены временно")
class JablogApplicationTests {

    @Test
    void contextLoads() {
    }

}
