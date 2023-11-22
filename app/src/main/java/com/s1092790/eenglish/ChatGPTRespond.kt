package com.s1092790.eenglish

class ChatGPTRespond {
    var id: String? = null
    var obj: String? = null
    var created = 0
    var model: String? = "text-davinci-002"
    var choices: ArrayList<Choice?>? = null
    var usage: Usage? = null

    inner class Choice {
        var text: String? = null
        var index = 0
        var logprobs: Any? = null
        var finishReason: String? = null
    }

    inner class Usage {
        var promptTokens = 0
        var completionTokens = 0
        var totalTokens = 0
    }

    val choicesList: ArrayList<Choice?>?
        get() = choices
}