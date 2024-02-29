package com.example.news.models

data class CountriesList(val name: String, val code: String) {
    object CountryList {
        val countries = listOf(
            CountriesList("United Arab Emirates", "ae"),
            CountriesList("Argentina", "ar"),
            CountriesList("Austria", "at"),
            CountriesList("Australia", "au"),
            CountriesList("Belgium", "be"),
            CountriesList("Bulgaria", "bg"),
            CountriesList("Brazil", "br"),
            CountriesList("Canada", "ca"),
            CountriesList("Switzerland", "ch"),
            CountriesList("China", "cn"),
            CountriesList("Colombia", "co"),
            CountriesList("Cuba", "cu"),
            CountriesList("Czech Republic", "cz"),
            CountriesList("Germany", "de"),
            CountriesList("Egypt", "eg"),
            CountriesList("France", "fr"),
            CountriesList("United Kingdom", "gb"),
            CountriesList("Greece", "gr"),
            CountriesList("Hong Kong", "hk"),
            CountriesList("Hungary", "hu"),
            CountriesList("Indonesia", "id"),
            CountriesList("Ireland", "ie"),
            CountriesList("Israel", "il"),
            CountriesList("India", "in"),
            CountriesList("Italy", "it"),
            CountriesList("Japan", "jp"),
            CountriesList("Republic Of Korea", "kr"),
            CountriesList("Lithuania", "lt"),
            CountriesList("Latvia", "lv"),
            CountriesList("Morocco", "ma"),
            CountriesList("Mexico", "mx"),
            CountriesList("Malaysia", "my"),
            CountriesList("Nigeria", "ng"),
            CountriesList("Kingdom Of NetherLands", "nl"),
            CountriesList("Norway", "no"),
            CountriesList("New Zealand", "nz"),
            CountriesList("Philippines", "ph"),
            CountriesList("Poland", "pl"),
            CountriesList("Portugal", "pt"),
            CountriesList("Romania", "ro"),
            CountriesList("Serbia", "rs"),
            CountriesList("Russian Federation", "ru"),
            CountriesList("Saudi Arabia", "sa"),
            CountriesList("Sweden", "se"),
            CountriesList("Singapore", "sg"),
            CountriesList("Slovenia", "si"),
            CountriesList("Slovakia", "sk"),
            CountriesList("Thailand", "th"),
            CountriesList("Turkey", "tr"),
            CountriesList("Taiwan", "tw"),
            CountriesList("Ukraine", "ua"),
            CountriesList("United States Of America", "us"),
            CountriesList("Venezuela", "ve"),
            CountriesList("South Africa", "za")

        )
        const val DEFAULT_COUNTRY_CODE = "us"
    }

    companion object {
        fun getCountryCode(countryName: String): String? {
            return CountryList.countries.find { it.name == countryName }?.code
        }
    }
}