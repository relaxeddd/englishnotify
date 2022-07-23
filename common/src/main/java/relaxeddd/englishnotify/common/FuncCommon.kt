package relaxeddd.englishnotify.common

fun isCorrectAnswer(userAnswer: String, trueAnswer: String) : Boolean {
    val answerWords = trueAnswer.split(",")

    for (answerWord in answerWords) {
        if (getDefaultWord(answerWord) == getDefaultWord(userAnswer)) {
            return true
        }
    }

    return false
}

private fun getDefaultWord(word: String) = word
    .lowercase()
    .replace(".", "")
    .replace("-", "")
    .replace("?", "")
    .replace("!", "")
    .replace(",", "")
    .replace("`", "")
    .replace("'", "")
    .replace("\"", "")
    .replace("’", "")
    .replace("«", "")
    .replace("»", "")
    .replace("“", "")
    .replace("”", "")
    .replace(" ", "")
    .replace("ь", "")
    .replace("ъ", "")
    .replace("сс", "с")
    .replace("пп", "п")
    .replace("и", "е")
    .replace("й", "е")
    .replace("э", "е")
    .replace("а", "о")
    .replace("ю", "у")
    .replace("ё", "е")
    .replace("д", "т")
    .replace("г", "к")
    .replace("б", "п")
    .replace("з", "с")
    .trim()
