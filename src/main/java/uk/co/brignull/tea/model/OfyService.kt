package uk.co.brignull.tea.model

import com.googlecode.objectify.Objectify
import com.googlecode.objectify.ObjectifyService
import uk.co.brignull.tea.model.objects.AuthUser
import uk.co.brignull.tea.model.objects.OAuthConfiguration

class OfyService {
    companion object {
        init {
            ObjectifyService.register(AuthUser::class.java)
            ObjectifyService.register(OAuthConfiguration::class.java)
        }

        fun ofy() : Objectify {
            return ObjectifyService.ofy()
        }
    }
}