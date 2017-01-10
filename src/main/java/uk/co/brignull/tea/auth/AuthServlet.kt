package uk.co.brignull.tea.auth

import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthServlet : HttpServlet() {
    @Throws(ServletException::class, IOException::class)
    public override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    }
}
