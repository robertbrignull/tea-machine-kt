package uk.co.brignull.tea.servlet

import uk.co.brignull.tea.servlet.api.ApiBrew
import uk.co.brignull.tea.servlet.auth.AuthAdd
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RootServlet : HttpServlet() {
    private val getHandlers = mapOf<String, RequestHandler>(
            Pair("/", RootPage()),
            Pair("/auth/add", AuthAdd())
    )

    private val postHandlers = mapOf<String, RequestHandler>(
            Pair("/api/brewing", ApiBrew())
    )

    @Throws(ServletException::class, IOException::class)
    public override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        handleRequest(req, resp, getHandlers)
    }

    @Throws(ServletException::class, IOException::class)
    public override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        handleRequest(req, resp, postHandlers)
    }

    private fun handleRequest(req: HttpServletRequest, resp: HttpServletResponse, handlers: Map<String, RequestHandler>) {
        val path = req.requestURI.substring(req.contextPath.length)
        for ((key, value) in handlers) {
            if (key == path) {
                value.handleRequest(req, resp)
                return
            }
        }

        resp.sendError(404, "Path " + req.pathInfo + " not recognised. Expected one of ["
                + handlers.keys.joinToString() + "].")
    }
}
