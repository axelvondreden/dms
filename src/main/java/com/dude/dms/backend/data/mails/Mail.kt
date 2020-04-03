package com.dude.dms.backend.data.mails

import com.dude.dms.backend.data.DataEntity
import com.dude.dms.backend.data.Diffable
import com.dude.dms.backend.data.LogsEvents
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.brain.t
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.Size

@JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
@Entity
class Mail(
        var sender: String,

        var received: LocalDateTime,

        var subject: String? = null,

        @Column(columnDefinition = "LONGVARCHAR")
        @Size(max = 99999)
        var body: String? = null,

        @ManyToMany
        var tags: MutableSet<Tag> = HashSet(),

        @OneToMany
        var docs: MutableSet<Doc> = HashSet()
) : DataEntity(), Diffable<Mail>, LogsEvents {

    override fun toString() = t("mail")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Mail

        if (sender != other.sender) return false
        if (received != other.received) return false
        if (subject != other.subject) return false
        if (body != other.body) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sender.hashCode()
        result = 31 * result + received.hashCode()
        result = 31 * result + (subject?.hashCode() ?: 0)
        result = 31 * result + (body?.hashCode() ?: 0)
        return result
    }
}