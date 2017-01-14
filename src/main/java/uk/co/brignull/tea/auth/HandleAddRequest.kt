package uk.co.brignull.tea.auth

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import uk.co.brignull.tea.model.OfyService
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import uk.co.brignull.tea.model.loadSingletonInstance
import uk.co.brignull.tea.model.objects.AuthUser
import uk.co.brignull.tea.model.objects.OAuthConfiguration
import uk.co.brignull.tea.util.parseQueryString
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

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

    val accessURL = URL("https://slack.com/api/oauth.access"
            + "?client_id=" + config.clientId
            + "&client_secret=" + config.clientSecret
            + "&code=" + queryString["code"]
            + (if (queryString["redirect_uri"] != null) "&redirect_uri=" + queryString["redirect_uri"] else ""))

    val conn = accessURL.openConnection() as HttpURLConnection
    conn.requestMethod = "GET"

    val data = Gson().fromJson(InputStreamReader(conn.inputStream), AuthResponse::class.java)
    if (data == null) {
        resp.sendError(500, "Invalid response from authorization server")
        return
    }

    val newUser = AuthUser(data.teamName, data.accessToken, data.scope)
    OfyService.ofy().save().entity(newUser).now()

    resp.sendRedirect("/?success=true")
}

class AuthResponse {
    @SerializedName("access_token")
    val accessToken: String?
    val scope: String?
    @SerializedName("team_name")
    val teamName: String?
    val teamID: String?

    constructor() : this(null, null, null, null)

    constructor(accessToken: String?, scope: String?, teamName: String?, teamID: String?) {
        this.accessToken = accessToken
        this.scope = scope
        this.teamName = teamName
        this.teamID = teamID
    }
}
