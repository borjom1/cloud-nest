package com.cloud.nest.fm.config;

import com.cloud.nest.platform.infrastructure.auth.UserAuthSessionConverter;
import com.cloud.nest.platform.infrastructure.security.AnonymousEndpoints;
import com.cloud.nest.platform.infrastructure.security.CommonSecurityConfig;
import com.cloud.nest.platform.infrastructure.streaming.ContentRangeSelectionConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.MinioClient;
import jakarta.annotation.Nonnull;
import org.jooq.DSLContext;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

import static java.util.Objects.requireNonNull;

@Configuration
@EnableAsync
@EnableScheduling
@Import({UserAuthSessionConverter.class, CommonSecurityConfig.class, ContentRangeSelectionConverter.class})
public class FileManagementModuleConfig implements WebMvcConfigurer {

    @Override
    public void configureAsyncSupport(@Nonnull AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(taskExecutor());
    }

    @Bean
    AnonymousEndpoints anonymousEndpoints() {
        return AnonymousEndpoints.of("/public/**");
    }

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
    MinioClient minioClient(MinIOProperties properties) {
        return MinioClient.builder()
                .endpoint(properties.getUrl())
                .credentials(properties.getUsername(), properties.getPassword())
                .build();
    }

    @Bean
    AsyncTaskExecutor taskExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("umAsyncExec-");
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(50);
        return executor;
    }

    @Bean
    DataSourceConnectionProvider connectionProvider(DataSource dataSource) {
        return new DataSourceConnectionProvider(dataSource);
    }

    @Bean
    TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        final TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setTimeout(30);
        return template;
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
