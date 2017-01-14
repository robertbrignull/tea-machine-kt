package uk.co.brignull.tea.api.brewing

import com.google.appengine.api.taskqueue.DeferredTask
import com.google.appengine.api.taskqueue.QueueFactory
import com.google.appengine.api.taskqueue.TaskOptions
import org.apache.http.client.fluent.Request
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import uk.co.brignull.tea.model.loadSingletonInstance
import uk.co.brignull.tea.model.objects.OAuthConfiguration
import uk.co.brignull.tea.util.parseQueryString
import java.io.IOException
import java.net.URLDecoder
import java.util.logging.Logger
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class BrewingServlet : HttpServlet() {
    val log = Logger.getLogger(BrewingServlet::class.qualifiedName)!!

    @Throws(ServletException::class, IOException::class)
    public override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val data = parseQueryString(req.reader.readText())

        val config = loadSingletonInstance(OAuthConfiguration::class)

        if (data["token"] != config.verificationToken) {
            resp.sendError(400, "verification token incorrect")
            return
        }

        val brewingMinutes = 3L

        scheduleTask(URLDecoder.decode(data["response_url"]!!, Charsets.UTF_8.name()), brewingMinutes)

        resp.writer.println("Will remind you in $brewingMinutes minutes")
    }

    private fun scheduleTask(responseURL : String, brewingMinutes: Long) {
        log.info(responseURL)
        val queue = QueueFactory.getDefaultQueue()
        queue.add(TaskOptions.Builder
                .withPayload(ReminderTask(responseURL))
                .countdownMillis(brewingMinutes * 60 * 1000))
    }
}

private class ReminderTask(val responseURL: String) : DeferredTask {
    override fun run() {
        val log = Logger.getLogger(ReminderTask::class.qualifiedName)!!
        val message = "{ \"text\": \"Your tea is ready!\" }"
        log.info("making post request to $responseURL with content $message")
        val response = Request.Post(responseURL)
                .body(StringEntity(message, ContentType.APPLICATION_JSON))
                .execute().returnContent().asString()
        log.info("response = " + response)
    }
}
