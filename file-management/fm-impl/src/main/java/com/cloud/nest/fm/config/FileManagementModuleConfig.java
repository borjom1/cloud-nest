package com.cloud.nest.fm.config;

import com.cloud.nest.platform.infrastructure.auth.AuthSessionConverter;
import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;
import com.cloud.nest.um.UserManagementApiConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.MinioClient;
import org.jooq.DSLContext;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

import static java.util.Objects.requireNonNull;

@Configuration
@Import(UserManagementApiConfig.class)
public class FileManagementModuleConfig {

    @Bean
    ObjectMapper objectMapper() {
        final var objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConfigurationProperties("business.storage")
    StorageProperties storageProperties() {
        return new StorageProperties();
    }

    @Bean
    @ConfigurationProperties("minio")
    MinIOProperties minIOProperties() {
        return new MinIOProperties();
    }

    @Bean
    MinioClient minioClient(MinIOProperties properties) {
        return MinioClient.builder()
                .endpoint(properties.getUrl())
                .credentials(properties.getUsername(), properties.getPassword())
                .build();
    }

    @Bean
    Converter<String, UserAuthSession> authSessionConverter(ObjectMapper objectMapper) {
        return new AuthSessionConverter(objectMapper);
    }

    @Bean
    DataSourceConnectionProvider connectionProvider(DataSource dataSource) {
        return new DataSourceConnectionProvider(dataSource);
    }

    @Bean
    DefaultConfiguration configuration(DataSourceConnectionProvider connectionProvider) {
        final DefaultConfiguration configuration = new DefaultConfiguration();
        configuration.set(connectionProvider)
                .set(new DefaultExecuteListenerProvider(new JooqExceptionTranslator()))
                .set(SQLDialect.POSTGRES);

        return configuration;
    }

    @Bean
    DSLContext dslContext(DefaultConfiguration configuration) {
        return new DefaultDSLContext(configuration);
    }

    static class JooqExceptionTranslator implements ExecuteListener {

        @Override
        public void exception(ExecuteContext ctx) {
            final SQLDialect dialect = ctx.configuration().dialect();
            final SQLExceptionTranslator translator = new SQLErrorCodeSQLExceptionTranslator(dialect.name());
            final DataAccessException exception = translator.translate(
                    "Access database using jOOQ",
                    ctx.sql(),
                    requireNonNull(ctx.sqlException())
            );
            ctx.exception(exception);
        }

    }

}
