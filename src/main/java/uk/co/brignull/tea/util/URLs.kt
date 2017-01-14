package uk.co.brignull.tea.util

fun parseQueryString(queryString: String?): Map<String, String> {
    if (queryString == null)
        return emptyMap()
    val result = mutableMapOf<String, String>()
    queryString.trim('?').split("&")
            .map { it.split("=") }
            .forEach { result.put(it[0], it[1]) }
    return result
}
