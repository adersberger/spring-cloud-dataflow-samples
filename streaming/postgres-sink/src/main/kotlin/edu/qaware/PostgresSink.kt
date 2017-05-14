package edu.qaware

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Sink

/*
 * Copyright (C) 2016 QAware GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

@EnableBinding(Sink::class)
@EnableConfigurationProperties(PostgresSinkProperties::class)
class PostgresSink {

    @Autowired
    lateinit var props: PostgresSinkProperties

    @StreamListener(Sink.INPUT)
    fun processTweet(message: String) {
        Database.connect(props.url, user = props.user, password = props.password,
                driver = "org.postgresql.Driver")
        transaction {
            SchemaUtils.create(Messages)
            Messages.insert {
                it[Messages.message] = message
            }
        }
    }
}

object Messages : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val message = text("message")
}
