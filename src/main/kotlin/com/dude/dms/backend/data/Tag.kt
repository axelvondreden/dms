package com.dude.dms.backend.data

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.history.TagHistory
import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.data.rules.RegexRule
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Size
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

@JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
@Entity
class Tag(
        @Size(max = 50) var name: String,
        var color: String,
        @ManyToMany(mappedBy = "tags") var docs: Set<Doc> = HashSet(),
        @ManyToMany(fetch = FetchType.EAGER) var attributes: Set<Attribute> = HashSet(),
        @ManyToMany(mappedBy = "tags") var plainTextRules: Set<PlainTextRule> = HashSet(),
        @ManyToMany(mappedBy = "tags") var regexRules: Set<RegexRule> = HashSet(),
        @OneToMany(mappedBy = "tag") @OrderBy("timestamp") override var history: List<TagHistory> = ArrayList()
) : DataEntity(), Diffable<Tag>, Historical<TagHistory> {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        if (!super.equals(other)) return false
        return name == (other as Tag).name
    }

    override fun hashCode() = Objects.hash(super.hashCode(), name)
}