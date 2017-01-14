package uk.co.brignull.tea.model.objects

import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id

@Entity
class AuthUser {
    @Id var teamName: String?
    var accessToken: String?
    var scope: String?

    constructor() : this(null, null, null)

    constructor(teamName: String?, accessToken: String?, scope: String?) {
        this.teamName = teamName
        this.accessToken = accessToken
        this.scope = scope
    }
}
