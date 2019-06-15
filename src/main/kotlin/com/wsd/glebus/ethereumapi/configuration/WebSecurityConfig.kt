package com.wsd.glebus.ethereumapi.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig : WebSecurityConfigurerAdapter() {


    @Bean
    fun myBasicAuthenticationEntryPoint(): MyBasicAuthenticationEntryPoint {
        return MyBasicAuthenticationEntryPoint()
    }

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests().anyRequest().authenticated()
                .and().httpBasic().realmName("ETH").authenticationEntryPoint(myBasicAuthenticationEntryPoint())
    }


    class MyBasicAuthenticationEntryPoint : BasicAuthenticationEntryPoint() {

        override fun commence(request: HttpServletRequest?,
                              response: HttpServletResponse,
                              authEx: AuthenticationException) {
            response.addHeader("WWW-Authenticate", "Basic realm=$realmName")
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            val writer = response.writer
            writer.println("HTTP Status 401 - " + authEx.message)
        }

        override fun afterPropertiesSet() {
            realmName = "ETH"
            super.afterPropertiesSet()
        }

    }

}