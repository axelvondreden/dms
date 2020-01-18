package com.dude.dms.brain.mail

import com.dude.dms.brain.OptionKey
import org.springframework.stereotype.Component
import java.util.*
import javax.mail.*


@Component
class EmailManager {

    fun getAllFolders() {
        val store = getStore()
        val folders = store.defaultFolder.list()
        folders.forEach { println(it) }
    }

    fun downloadEmails(host: String, port: String, userName: String, password: String) {
        /*val session = Session.getDefaultInstance(getServerProperties(host, port))
        try {
            val store = session.getStore("imap")
            store.connect(userName, password)
            // opens the inbox folder
            val folderInbox = store.getFolder("INBOX")
            folderInbox.open(Folder.READ_ONLY)
            // fetches new messages from server
            val messages = folderInbox.messages
            for (i in messages.indices) {
                val msg = messages[i]
                val fromAddress = msg.from
                val from = fromAddress[0].toString()
                val subject = msg.subject
                val toList = parseAddresses(msg
                        .getRecipients(Message.RecipientType.TO))
                val ccList = parseAddresses(msg
                        .getRecipients(Message.RecipientType.CC))
                val sentDate = msg.sentDate.toString()
                val contentType = msg.contentType
                var messageContent = ""
                if (contentType.contains("text/plain")
                        || contentType.contains("text/html")) {
                    try {
                        val content = msg.content
                        if (content != null) {
                            messageContent = content.toString()
                        }
                    } catch (ex: Exception) {
                        messageContent = "[Error downloading content]"
                        ex.printStackTrace()
                    }
                }
                // print out details of each message
                println("Message #" + (i + 1) + ":")
                println("\t From: $from")
                println("\t To: $toList")
                println("\t CC: $ccList")
                println("\t Subject: $subject")
                println("\t Sent Date: $sentDate")
                println("\t Message: $messageContent")
            }
            // disconnect
            folderInbox.close(false)
            store.close()
        } catch (ex: NoSuchProviderException) {
            println("No provider for protocol: $protocol")
            ex.printStackTrace()
        } catch (ex: MessagingException) {
            println("Could not connect to the message store")
            ex.printStackTrace()
        }*/
    }

    fun testConnection() {
        getStore().defaultFolder.list()
    }

    private fun getStore() = Session.getDefaultInstance(getServerProperties()).getStore("imaps").apply {
        connect(OptionKey.IMAP_HOST.string, OptionKey.IMAP_LOGIN.string, OptionKey.IMAP_PASSWORD.string)
    }

    private fun getServerProperties() = Properties().apply {
        setProperty("mail.imap.host", OptionKey.IMAP_HOST.string)
        setProperty("mail.imap.port", OptionKey.IMAP_PORT.string)
        setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
        setProperty("mail.imap.socketFactory.fallback", "false")
        setProperty("mail.imap.socketFactory.port", OptionKey.IMAP_PORT.string)
    }
}