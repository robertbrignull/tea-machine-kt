package uk.co.brignull.tea.servlet.api

import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ApiServlet : HttpServlet() {
    val requestHandlers = mapOf<String, (HttpServletRequest, HttpServletResponse) -> Unit>(
            Pair("/brewing", ::handleBrewingRequest)
    )

    @Throws(ServletException::class, IOException::class)
    public override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        for (handler in requestHandlers.entries) {
            if (req.pathInfo == handler.key) {
                handler.value(req, resp)
                return
            }
        }

        resp.sendError(404, "Path " + req.pathInfo + " not recognised. Expected one of ["
                + requestHandlers.map { it -> it.key }.joinToString() + "].")
    }
}
