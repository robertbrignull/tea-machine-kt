package uk.co.brignull.tea.model.objects

import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id

@Entity
data class OAuthConfiguration(
    @Id var id: String? = null,
    var clientId: String? = null,
    var clientSecret: String? = null,
    var verificationToken: String? = null
)
