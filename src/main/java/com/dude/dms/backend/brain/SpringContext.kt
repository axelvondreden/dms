package com.dude.dms.backend.brain

import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
class SpringContext : ApplicationContextAware {

    @Throws(BeansException::class)
    override fun setApplicationContext(context: ApplicationContext) {
        Companion.context = context
    }

    companion object {
        private var context: ApplicationContext? = null

        fun <T> getBean(beanClass: Class<T>) = if (context != null) context!!.getBean(beanClass) else null
    }
}