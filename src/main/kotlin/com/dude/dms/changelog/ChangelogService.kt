package com.dude.dms.changelog

import com.dude.dms.brain.DmsLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpClientErrorException.Unauthorized
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Component
class ChangelogService {

    @Value("\${api.releases}")
    private lateinit var releaseApi: String

    @Value("\${build.version}")
    private lateinit var buildVersion: String

    val currentVersion get() = buildVersion

    fun findAll(): List<Changelog> {
        try {
            val releases = RestTemplate().getForObject(releaseApi, Array<Release>::class.java)
            if (releases != null) {
                return releases.map { Changelog(it.published_at, it.body, it.tag_name) }
            }
        } catch (e: Unauthorized) {
            LOGGER.warn(e.statusText)
        } catch (e: HttpClientErrorException.NotFound) {
            LOGGER.warn(e.statusText)
        } catch (e: RestClientException) {
            e.message?.let { LOGGER.error(it, e) }
        }
        return emptyList()
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(ChangelogService::class.java)
    }
}