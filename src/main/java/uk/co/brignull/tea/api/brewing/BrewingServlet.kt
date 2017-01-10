package uk.co.brignull.tea.api.brewing

import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class BrewingServlet : HttpServlet() {
    @Throws(ServletException::class, IOException::class)
    public override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.writer.println("Hello, World!")
    }
}
