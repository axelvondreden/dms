package com.dude.dms.backend.data

import java.io.Serializable
import java.util.*
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class DataEntity : Serializable {

    @Id
    @GeneratedValue var id = 0L

    override fun equals(other: Any?) = if (other is DataEntity) other.id == id else false

    override fun hashCode() = Objects.hash(id)
}