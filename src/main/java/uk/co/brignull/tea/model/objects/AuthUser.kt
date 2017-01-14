package uk.co.brignull.tea.model.objects

import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id

@Entity
data class AuthUser(
    @Id var teamName: String? = null,
    var teamID: String? = null,
    var accessToken: String? = null,
    var scope: String? = null
)
