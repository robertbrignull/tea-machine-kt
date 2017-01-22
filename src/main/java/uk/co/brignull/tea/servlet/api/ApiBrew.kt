package uk.co.brignull.tea.servlet.api

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.appengine.api.taskqueue.DeferredTask
import com.google.appengine.api.taskqueue.QueueFactory
import com.google.appengine.api.taskqueue.TaskOptions
import com.google.appengine.repackaged.com.google.api.client.http.HttpResponseException
import org.apache.http.client.fluent.Request
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import uk.co.brignull.tea.model.loadSingletonInstance
import uk.co.brignull.tea.model.objects.OAuthConfiguration
import uk.co.brignull.tea.servlet.RequestHandler
import uk.co.brignull.tea.util.parseQueryString
import java.net.URLDecoder
import java.util.logging.Logger
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ApiBrew : RequestHandler {
    val log = Logger.getLogger(ApiBrew::class.qualifiedName)!!

    private val defaultBrewingMinutes = 3

    override fun handleRequest(req: HttpServletRequest, resp: HttpServletResponse) {
        val data = parseQueryString(req.reader.readText())

        val config = loadSingletonInstance(OAuthConfiguration::class)

        if (data["token"] != config.verificationToken) {
            resp.sendError(400, "verification token incorrect")
            return
        }

        val brewingMinutes = decodeMinutes(data["text"])

        scheduleTask(URLDecoder.decode(data["response_url"]!!, Charsets.UTF_8.name()), brewingMinutes)

        resp.writer.println("Will remind you in $brewingMinutes minutes")
    }

    private fun decodeMinutes(text: String?): Int {
        if (text == null)
            return defaultBrewingMinutes
        try {
            return text.toInt()
        } catch (e: NumberFormatException) {
            return defaultBrewingMinutes
        }
    }

    private fun scheduleTask(responseURL: String, brewingMinutes: Int) {
        log.info(responseURL)
        val queue = QueueFactory.getDefaultQueue()
        queue.add(TaskOptions.Builder
                .withPayload(ReminderTask(responseURL))
                .countdownMillis(brewingMinutes * 60 * 1000L))
    }

    private class ReminderTask(val responseURL: String) : DeferredTask {
        override fun run() {
            val log = Logger.getLogger(ReminderTask::class.qualifiedName)!!

            val message = ":tea: Your tea is ready!"
            val json = jacksonObjectMapper().writeValueAsString(Message(message))

            log.info("making post request to $responseURL with content $json")
            try {
                Request.Post(responseURL)
                        .body(StringEntity(json, ContentType.APPLICATION_JSON))
                        .execute().returnContent().asString()
            } catch (e: HttpResponseException) {
                if (e.statusCode == 404)
                    log.warning("Posting failed with 404")
                else throw e
            }
        }
    }

    private data class Message(
            @JsonProperty("text") val text: String
    )
}
