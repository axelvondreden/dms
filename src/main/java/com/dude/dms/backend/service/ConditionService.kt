package com.dude.dms.backend.service

import com.dude.dms.backend.data.rules.Condition
import com.dude.dms.backend.repositories.ConditionRepository
import org.springframework.stereotype.Service

@Service
class ConditionService(repository: ConditionRepository) : CrudService<Condition>(repository) {

    override fun save(entity: Condition): Condition {
        return super.save(entity).also { entity.children.forEach { save(it) } }
    }

    override fun delete(entity: Condition) {
        entity.children.forEach(this::delete)
        super.delete(entity)
    }
}