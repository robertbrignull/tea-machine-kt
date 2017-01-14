package uk.co.brignull.tea.model.objects

import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id

@Entity
class OAuthConfiguration {
    @Id var id: String?
    var clientId: String?
    var clientSecret: String?
    var verificationToken: String?

    constructor() : this(null, null, null, null)

    constructor(id: String?, clientId: String?, clientSecret: String?, verificationToken: String?) {
        this.id = id
        this.clientId = clientId
        this.clientSecret = clientSecret
        this.verificationToken = verificationToken
    }
}
