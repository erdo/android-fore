package foo.bar.example.forektorkt.api

import co.early.fore.core.logging.Logger
import foo.bar.example.forektorkt.BuildConfig
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.util.*
import io.ktor.client.HttpClient

class PluginGlobalInterceptor private constructor(
    private val logger: Logger,
   // private val session: Session,
) {

    class Config {
        lateinit var logger: Logger
      //  var session: Session
    }

    companion object Plugin : HttpClientPlugin<Config, PluginGlobalInterceptor> {

        override val key = AttributeKey<PluginGlobalInterceptor>("PluginGlobalInterceptor")

        override fun prepare(block: Config.() -> Unit): PluginGlobalInterceptor {
            val config = Config().apply(block)
            return PluginGlobalInterceptor(
                logger = config.logger
             //   session = config.session
            )
        }

        override fun install(plugin: PluginGlobalInterceptor, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                context.headers.append("Content-Type", "application/json")
                // context.headers.append("X-MyApp-Auth-Token", plugin.session?.getSessionToken() ?: "expired")
                context.headers.append("User-Agent", "fore-example-user-agent-${BuildConfig.VERSION_NAME}")
            }
        }
    }
}
