package uk.co.brignull.tea

import com.mitchellbosecke.pebble.PebbleEngine
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RootServlet : HttpServlet() {
    @Throws(ServletException::class, IOException::class)
    public override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val engine = PebbleEngine.Builder().build()
        val compiledTemplate = engine.getTemplate("templates/root.html")

        val context = mutableMapOf<String, Any>()
        context.put("client_id", "64369940551.124605742784")

        compiledTemplate.evaluate(resp.writer, context)
    }
}
