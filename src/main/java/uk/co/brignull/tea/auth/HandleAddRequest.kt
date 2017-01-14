package uk.co.brignull.tea.auth

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.apache.http.client.fluent.Request
import uk.co.brignull.tea.model.OfyService
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import uk.co.brignull.tea.model.loadSingletonInstance
import uk.co.brignull.tea.model.objects.AuthUser
import uk.co.brignull.tea.model.objects.OAuthConfiguration
import uk.co.brignull.tea.util.parseQueryString

fun handleAddRequest(req: HttpServletRequest, resp: HttpServletResponse) {
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
    val data = Gson().fromJson(response, AuthResponse::class.java)
    if (data == null) {
        resp.sendError(500, "Invalid response from authorization server")
        return
    }

    val newUser = AuthUser(data.teamName, data.teamID, data.accessToken, data.scope)
    OfyService.ofy().save().entity(newUser).now()

    resp.sendRedirect("/?success=true")
}

class AuthResponse {
    @SerializedName("access_token")
    val accessToken: String?
    val scope: String?
    @SerializedName("team_name")
    val teamName: String?
    @SerializedName("team_id")
    val teamID: String?

    constructor() : this(null, null, null, null)

    constructor(accessToken: String?, scope: String?, teamName: String?, teamID: String?) {
        this.accessToken = accessToken
        this.scope = scope
        this.teamName = teamName
        this.teamID = teamID
    }
}
