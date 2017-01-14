package uk.co.brignull.tea.servlet.auth

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.http.client.fluent.Request
import uk.co.brignull.tea.model.OfyService
import uk.co.brignull.tea.model.loadSingletonInstance
import uk.co.brignull.tea.model.objects.AuthUser
import uk.co.brignull.tea.model.objects.OAuthConfiguration
import uk.co.brignull.tea.servlet.RequestHandler
import uk.co.brignull.tea.util.parseQueryString
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthAdd : RequestHandler {
    override fun handleRequest(req: HttpServletRequest, resp: HttpServletResponse) {
        val config = loadSingletonInstance(OAuthConfiguration::class)

        val queryString = parseQueryString(req.queryString)

        if (queryString["code"] == null) {
            resp.sendError(400, "Invalid query string. Must provide \"code\"")
            return
        }

        if (queryString["error"] != null) {
            resp.sendError(500, "Request denied by user: " + queryString["error"])
            return
        }

        val accessURL = "https://slack.com/api/oauth.access" +
                "?client_id=" + config.clientId +
                "&client_secret=" + config.clientSecret +
                "&code=" + queryString["code"] +
                (if (queryString["redirect_uri"] != null) "&redirect_uri=" + queryString["redirect_uri"] else "")

        val response = Request.Get(accessURL).execute().returnContent().asString()
        val data = jacksonObjectMapper().readValue(response, AuthResponse::class.java)
        if (data == null) {
            resp.sendError(500, "Invalid response from authorization server")
            return
        }

        val newUser = AuthUser(data.teamName, data.teamID, data.accessToken, data.scope)
        OfyService.ofy().save().entity(newUser).now()

        resp.sendRedirect("/?success=true")
    }

    private data class AuthResponse(
            @JsonProperty("access_token") val accessToken: String?,
            @JsonProperty("scope") val scope: String?,
            @JsonProperty("team_name") val teamName: String?,
            @JsonProperty("team_id") val teamID: String?
    )
}
