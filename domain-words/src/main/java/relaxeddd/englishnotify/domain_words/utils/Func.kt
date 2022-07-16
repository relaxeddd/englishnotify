package relaxeddd.englishnotify.domain_words.utils

import relaxeddd.englishnotify.domain_words.entity.Word

internal fun createDefaultWords() = listOf(
    Word("cause", "cause", "причина, дело, повод, вызывать", "kɔːz",
        timestamp = System.currentTimeMillis(), isCreatedByUser = false),
    Word("catch", "catch", "ловить, поймать, улов, выгода, добыча, захват", "kæʧ",
        listOf("irregular"), v2 = "caught", v3 = "caught", timestamp = System.currentTimeMillis(), isCreatedByUser = false),
    Word("dream", "dream", "мечтать, сниться, мечта, сон, фантазировать", "driːm",
        listOf("irregular"), v2 = "dreamt", v3 = "dreamt", timestamp = System.currentTimeMillis(), isCreatedByUser = false),
    Word("throw", "throw", "бросать, бросок, кидать, метать, метание", "θroʊ",
        listOf("irregular"), v2 = "threw", v3 = "thrown",
        timestamp = System.currentTimeMillis(), isCreatedByUser = false),
    Word("forget", "forget", "забывать, не помнить, забыть", "fəˈɡet",
        listOf("irregular"), v2 = "forgot", v3 = "forgotten",
        timestamp = System.currentTimeMillis(), isCreatedByUser = false),
    Word("bite", "bite", "кусать, укусить, укус, кусок, кусаться", "baɪt",
        listOf("irregular"), v2 = "bit", v3 = "bitten",
        timestamp = System.currentTimeMillis(), isCreatedByUser = false),
    Word("hide", "hide", "скрывать, прятать, прятаться", "haɪd",
        listOf("irregular"), v2 = "hid", v3 = "hidden",
        timestamp = System.currentTimeMillis(), isCreatedByUser = false),
    Word("absent", "absent", "отсутствовать, отсутствующий, отсутствует, в отсутствие", "ˈæbsənt",
        timestamp = System.currentTimeMillis(), isCreatedByUser = false),
    Word("beard", "beard", "борода", "bɪrd",
        timestamp = System.currentTimeMillis(), isCreatedByUser = false),
    Word("claim", "claim", "запрос, требование, требовать, иск, заявка, претензия", "kleɪm",
        timestamp = System.currentTimeMillis(), isCreatedByUser = false),
    Word("desire", "desire", "желание, желать, страсть", "dɪˈzaɪər",
        timestamp = System.currentTimeMillis(), isCreatedByUser = false),
    Word("elk", "elk", "лось, сохатый", "elk",
        timestamp = System.currentTimeMillis(), isCreatedByUser = false)
)
