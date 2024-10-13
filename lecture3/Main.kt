package org.example

data class Response(
    val code: Int,
    val body: String?,
)

class Client {
    fun perform(code: Int, body: String?) = ResponseActions(code, body)
}

fun main() {
    val mockClient = Client()

    val response = mockClient.perform(200, "OK") // ResponseActions.andExpect()
        .andDo { response ->
            println(response)
        }
        .andExpect{
            body {
                isNotNull()
            }
            status {
                isOk()
            }
        }.response
    println(response)
}

class ResponseActions(
    private val code: Int,
    private val body: String?
) {
    fun andExpect(func: ResponseMatchers.() -> Unit): ResponseActions {
        ResponseMatchers(code, body).func()
        return this
    }

    val response = Response(code, body)

    fun andDo(func: (Response) -> Unit ): ResponseActions {
        func(response)
        return this
    }

    inner class ResponseMatchers(
        private val code: Int,
        private val body: String?
    ) {
        fun status(func: StatusResponseMatchers.() -> Unit) {
            StatusResponseMatchers(code).func()
        }

        fun body(func: BodyResponseMatchers.() -> Unit) {
            BodyResponseMatchers(body).func()
        }
    }

    inner class StatusResponseMatchers(
        private val code: Int
    ) {
        fun isOk() {
            if (code != 200) {
                throw StatusResponseMatchersException("Code is not 200, expected 200")
            }
        } // если статус не 200, то выбросить исключение
        fun isBadRequest() {
            if (code != 400) {
                throw StatusResponseMatchersException("Code is not 400, expected 400")
            }
        } // если статус не 400, то выбросить исключение
        fun isInternalServerError() {
            if (code != 500) {
                throw StatusResponseMatchersException("Code is not 500, expected 500")
            }
        } // если статус не 500, то выбросить исключение
    }

    inner class BodyResponseMatchers(
        private val body: String?
    ) {
        fun isNull() {
            if (body != null) {
                throw BodyResponseMatchersException("Body is not null, expected null")
            }
        } // если тело не пустое, то выбросить исключение
        fun isNotNull() {
            if (body == null) {
                throw BodyResponseMatchersException("Body is null, expected not null")
            }
        } // если тело пустое, то выбросить исключение
    }
}

sealed class ResponseMatchersException(message: String) : Exception(message)

class StatusResponseMatchersException(message: String) : ResponseMatchersException(message)

class BodyResponseMatchersException(message: String) : ResponseMatchersException(message)
