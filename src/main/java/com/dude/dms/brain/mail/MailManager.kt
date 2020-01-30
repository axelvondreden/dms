package com.dude.dms.brain.mail

import com.dude.dms.backend.data.mails.Mail
import com.dude.dms.brain.options.Options
import org.jsoup.Jsoup
import org.springframework.stereotype.Component
import java.time.ZoneId
import java.util.*
import javax.mail.Folder
import javax.mail.Message
import javax.mail.Session
import javax.mail.Store
import javax.mail.internet.MimeMultipart


@Component
class MailManager {

    fun getRootFolders(opened: Boolean = false): List<Folder> {
        return getStore().defaultFolder.list().toList().map {
            getStore().getFolder(it.fullName)
                    .apply { if (opened && it.type and Folder.HOLDS_MESSAGES == Folder.HOLDS_MESSAGES) it.open(Folder.READ_ONLY) }
        }
    }

    fun getSubFolders(parent: Folder, opened: Boolean = false): List<Folder> {
        return getStore().getFolder(parent.fullName).list().toList().map {
            getStore().getFolder(it.fullName)
                    .apply { if (opened && it.type and Folder.HOLDS_MESSAGES == Folder.HOLDS_MESSAGES) it.open(Folder.READ_ONLY) }
        }
    }

    fun getMails(folderName: String): Set<Mail> {
        val folder = getStore().getFolder(folderName)
        folder.open(Folder.READ_ONLY)
        val set = mutableSetOf<Mail>()
        for (message in folder.messages) {
            val from = message.from[0].toString()
            val subject = message.subject
            val date = message.receivedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            val body = getTextFromMessage(message)
            set.add(Mail(from, date, subject, body))
        }
        folder.close(false)
        return set
    }

    fun testConnection() {
        getStore().defaultFolder.list()
    }

    companion object {
        private var store: Store? = null

        private fun getStore(): Store {
            if (store == null || !store!!.isConnected) {
                store = Session.getInstance(getServerProperties()).getStore("imaps").apply {
                    connect(Options.get().mail.host, Options.get().mail.login, Options.get().mail.password)
                }
            }
            store!!.defaultFolder
            return store!!
        }

        private fun getServerProperties() = Properties().apply {
            setProperty("mail.imap.host", Options.get().mail.host)
            setProperty("mail.imap.port", Options.get().mail.port.toString())
            setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            setProperty("mail.imap.socketFactory.fallback", "false")
            setProperty("mail.imap.socketFactory.port", Options.get().mail.port.toString())
        }

        private fun getTextFromMessage(message: Message) = when {
            message.isMimeType("text/plain") -> message.content.toString()
            message.isMimeType("text/html") -> Jsoup.parse(message.content as String).text()
            message.isMimeType("multipart/*") -> getTextFromMimeMultipart(message.content as MimeMultipart)
            else -> throw RuntimeException("Unknown MIME type: ${message.contentType}")
        }

        private fun getTextFromMimeMultipart(mimeMultipart: MimeMultipart): String {
            var result = ""
            for (i in 0 until mimeMultipart.count) {
                val bodyPart = mimeMultipart.getBodyPart(i)
                if (bodyPart.isMimeType("text/plain")) {
                    result = result + "\n" + bodyPart.content
                    break
                } else if (bodyPart.isMimeType("text/html")) {
                    result = result + "\n" + Jsoup.parse(bodyPart.content as String).text()
                } else if (bodyPart.content is MimeMultipart) {
                    result += getTextFromMimeMultipart(bodyPart.content as MimeMultipart)
                }
            }
            return result
        }
    }
}