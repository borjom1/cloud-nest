package com.cloud.nest.um.config;

import com.cloud.nest.auth.AuthApiConfig;
import com.cloud.nest.platform.infrastructure.auth.AuthSessionConverter;
import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.core.convert.converter.Converter;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;

import static java.util.Objects.requireNonNull;

@Configuration
@Import(AuthApiConfig.class)
public class UserManagementModuleConfig {

    @Bean
    ObjectMapper objectMapper() {
        final var objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        return objectMapper;
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
