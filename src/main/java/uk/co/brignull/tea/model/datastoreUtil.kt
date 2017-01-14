package uk.co.brignull.tea.model

import kotlin.reflect.KClass

private val singletonInstanceName = "instance"

fun <T : Any> loadSingletonInstance(type: KClass<T>): T {
    return OfyService.ofy().load().type(type.java).id(singletonInstanceName).safe()
}
