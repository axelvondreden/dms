package com.dude.dms.backend.data.mails

import com.dude.dms.backend.data.DataEntity
import com.dude.dms.backend.data.Diffable
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Doc
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

        @ManyToMany(fetch = FetchType.EAGER)
        var tags: MutableSet<Tag> = HashSet(),

        @OneToMany(fetch = FetchType.EAGER)
        var docs: MutableSet<Doc> = HashSet()
) : DataEntity(), Diffable<Mail> {

    override fun toString() = "Mail(sender='$sender', received=$received, subject=$subject)"
}