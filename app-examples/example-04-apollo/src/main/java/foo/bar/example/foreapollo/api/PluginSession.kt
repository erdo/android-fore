package foo.bar.example.foreapollo.api

import foo.bar.example.foreapollo.feature.authentication.Authenticator
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.api.createClientPlugin

fun HttpClientConfig<*>.addSessionToken(authenticator: Authenticator? = null) {
    install(createClientPlugin("PluginSessionToken") {
        onRequest { request, _ ->
            if (authenticator?.hasSessionToken() == true) {
                request.headers.append("Authorization", authenticator.sessionToken)
            } else {
                request.headers.append("Authorization", "expired")
            }
        }
    })
}
