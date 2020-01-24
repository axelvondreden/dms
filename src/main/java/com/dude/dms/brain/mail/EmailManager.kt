package com.dude.dms.brain.mail

import com.dude.dms.brain.MailReceiveEvent
import com.dude.dms.brain.options.Options
import org.springframework.stereotype.Component
import java.time.ZoneId
import java.util.*
import javax.mail.Folder
import javax.mail.Session
import javax.mail.Store


@Component
class EmailManager {

    private val eventListeners = HashMap<String, MailReceiveEvent>()

    fun addEventListener(key: String, mailReceiveEvent: MailReceiveEvent) {
        eventListeners[key] = mailReceiveEvent
    }

    fun poll() {
        eventListeners.values.forEach { it.invoke() }
    }

    fun getRootFolders(opened: Boolean = false): List<Folder> {
        return getStore().defaultFolder.list().toList().apply {
            if (opened) {
                forEach { if (it.type and Folder.HOLDS_MESSAGES == Folder.HOLDS_MESSAGES) it.open(Folder.READ_ONLY) }
            }
        }
    }

    fun getSubFolders(parent: Folder, opened: Boolean = false): List<Folder> {
        return parent.list().toList().apply {
            if (opened) {
                forEach { if (it.type and Folder.HOLDS_MESSAGES == Folder.HOLDS_MESSAGES) it.open(Folder.READ_ONLY) }
            }
        }
    }

    fun getMails(folder: Folder) {
        folder.open(Folder.READ_ONLY)
        for (message in folder.messages) {
            val from = message.from[0].toString()
            val subject = message.subject
            val date = message.receivedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            val body = message.content.toString()
        }
        folder.close(false)
    }

    fun testConnection() {
        getStore().defaultFolder.list()
    }

    companion object {
        private var store: Store? = null

        private fun getStore(): Store {
            if (store == null) {
                store = Session.getDefaultInstance(getServerProperties()).getStore("imaps").apply {
                    connect(Options.get().mail.host, Options.get().mail.login, Options.get().mail.password)
                }
            }
            return store!!
        }

        private fun getServerProperties() = Properties().apply {
            setProperty("mail.imap.host", Options.get().mail.host)
            setProperty("mail.imap.port", Options.get().mail.port.toString())
            setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            setProperty("mail.imap.socketFactory.fallback", "false")
            setProperty("mail.imap.socketFactory.port", Options.get().mail.port.toString())
        }
    }
}