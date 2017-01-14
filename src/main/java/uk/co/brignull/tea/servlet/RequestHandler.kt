package uk.co.brignull.tea.servlet

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

interface RequestHandler {
    fun handleRequest(req: HttpServletRequest, resp: HttpServletResponse): Unit
}
