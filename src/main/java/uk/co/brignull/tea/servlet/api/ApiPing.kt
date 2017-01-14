package uk.co.brignull.tea.servlet.api

import uk.co.brignull.tea.servlet.RequestHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ApiPing : RequestHandler {
    override fun handleRequest(req: HttpServletRequest, resp: HttpServletResponse) {}
}