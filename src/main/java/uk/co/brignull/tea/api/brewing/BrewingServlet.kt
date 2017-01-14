package uk.co.brignull.tea.api.brewing

import com.google.appengine.api.taskqueue.DeferredTask
import com.google.appengine.api.taskqueue.QueueFactory
import com.google.appengine.api.taskqueue.TaskOptions
import uk.co.brignull.tea.model.loadSingletonInstance
import uk.co.brignull.tea.model.objects.OAuthConfiguration
import uk.co.brignull.tea.util.parseQueryString
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class BrewingServlet : HttpServlet() {
    @Throws(ServletException::class, IOException::class)
    public override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val data = parseQueryString(req.reader.readText())

        val config = loadSingletonInstance(OAuthConfiguration::class)

        if (data["token"] != config.verificationToken) {
            resp.sendError(400, "verification token incorrect")
            return
        }

        val brewingMinutes = 1L

        scheduleTask(URLDecoder.decode(data["response_url"]!!, Charsets.UTF_8.name()), brewingMinutes)

        resp.writer.println("Will remind you in $brewingMinutes minutes")
    }

    private fun scheduleTask(responseURL : String, brewingMinutes: Long) {
        val queue = QueueFactory.getDefaultQueue()
        queue.add(TaskOptions.Builder
                .withPayload(ReminderTask(responseURL))
                .etaMillis(brewingMinutes * 60 * 1000))
    }
}

private class ReminderTask(val responseURL: String) : DeferredTask {
    override fun run() {
        val message = "{ \"text\": \"Tea is ready\" }".toByteArray(Charsets.UTF_8)
        val conn = URL(responseURL).openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.addRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Content-Length", Integer.toString(message.size))
        conn.doOutput = true
        conn.outputStream.write(message)
    }
}
