package uk.co.brignull.tea.servlet

import com.mitchellbosecke.pebble.PebbleEngine
import uk.co.brignull.tea.model.loadSingletonInstance
import uk.co.brignull.tea.model.objects.OAuthConfiguration
import uk.co.brignull.tea.util.parseQueryString
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RootPage : RequestHandler {
    override fun handleRequest(req: HttpServletRequest, resp: HttpServletResponse) {
        val engine = PebbleEngine.Builder().build()
        val compiledTemplate = engine.getTemplate("templates/root.html")

        val oauthConfig = loadSingletonInstance(OAuthConfiguration::class)
        val queryString = parseQueryString(req.queryString)

        val context = mapOf(
                Pair("client_id", oauthConfig.clientId),
                Pair("redirect_uri", "https://tea-machine.appspot.com/auth/add"),
                Pair("success", queryString["success"] == "true")
        )

        compiledTemplate.evaluate(resp.writer, context)
    }
}
