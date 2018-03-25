package scripts.implementation.http

import org.http4k.client.ApacheClient
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters

fun newHttpClient() = ApacheClient()

fun HttpHandler.withDebug() = DebuggingFilters.PrintRequestAndResponse().then(this)
