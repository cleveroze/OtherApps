package io.github.cleveroze.otherapps.data

import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Bundled catalogue: descriptions and icons live in the APK, releases are
 * refreshed from GitHub. Everything is in Russian, matching the app.
 */
object Catalog {
    const val USER = "cleveroze"
    const val PROFILE_URL = "https://github.com/cleveroze"
    const val TELEGRAM_URL = "https://t.me/OtherGramWorks"

    /** Desktop-only projects never appear in an Android app store. */
    val blockedRepos = setOf("potable", "stride", "what", "graam")

    private fun ts(year: Int, month: Int, day: Int): Long =
        LocalDate.of(year, month, day).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    val bundled: List<StoreApp> = listOf(
        StoreApp(
            repo = "OtherGram",
            name = "OtherGram",
            tagline = "Telegram с плагинами и Material You",
            description = "Полноценный Telegram-клиент для Android: чаты, звонки, медиа и папки — плюс плагины, " +
                "собственные темы и аккуратная интеграция Material You. Внешний вид легко подогнать под себя, " +
                "не теряя привычных функций мессенджера.",
            category = "Мессенджеры",
            features = listOf(
                "Плагины .plugin и собственные темы",
                "Аккуратный Material You и живые анимации",
                "Звонки, медиа, папки и всё остальное из Telegram",
            ),
            iconKey = "asset:icons/othergram.png",
            accent = 0xFF7FA6BC,
            featured = true,
            release = Release(
                tag = "v2.0.0",
                version = "2.0.0",
                name = "OtherGram 2.0.0",
                publishedAt = ts(2026, 11, 2),
                apkUrl = "https://github.com/cleveroze/OtherGram/releases/download/v2.0.0/OtherGram-2.0.0.apk",
                apkName = "OtherGram-2.0.0.apk",
                sizeBytes = 84_780_000,
                downloads = 66,
                notes = "Новая база Telegram, стабильнее звонки и медиа. Поддержка плагинов, " +
                    "свежие иконки и исправления интерфейса.",
            ),
            stars = 0,
            language = "Java",
        ),
        StoreApp(
            repo = "MizuGram",
            name = "MizuGram",
            tagline = "Тихий Telegram цвета аметиста",
            description = "Telegram-клиент с характером воды: мягкие волны анимаций, аметистовая палитра " +
                "и закрытые чаты с PIN-кодом. Внутри — шаблоны сообщений, статистика переписки " +
                "и режим призрака, который не выдаёт вашу активность.",
            category = "Мессенджеры",
            features = listOf(
                "Закрытые чаты с PIN-кодом",
                "Шаблоны сообщений и статистика чатов",
                "Ghost mode и сохранение удалённых сообщений",
            ),
            iconKey = "asset:icons/mizugram.png",
            accent = 0xFF8E7BA8,
            comingSoon = true,
        ),
        StoreApp(
            repo = "DyeTube",
            name = "DyeTube",
            tagline = "YouTube без рекламы, который можно перекрасить",
            description = "Клиент YouTube на базе NewPipe: без рекламы и сервисов Google, с фоновым " +
                "воспроизведением и скачиванием видео. Восемь цветов интерфейса, брызги анимаций " +
                "при запуске — и всё это можно выключить в настройках.",
            category = "Видео",
            features = listOf(
                "Без рекламы и аккаунта Google",
                "Фоновое воспроизведение и загрузки",
                "8 цветов, градиенты и живой запуск",
            ),
            iconKey = "asset:icons/dyetube.png",
            accent = 0xFFC97B63,
            comingSoon = true,
        ),
        StoreApp(
            repo = "OtherRedactor",
            name = "OtherRedactor",
            tagline = "HTML, CSS и JS редактор в кармане",
            description = "Редактор кода для Android с подсветкой HTML, CSS и JavaScript, вкладками как " +
                "в браузере и предпросмотром на разных экранах — от телефона до Full HD. Проекты " +
                "хранятся на устройстве, а поиск, автодополнение тегов и JS-консоль всегда под рукой.",
            category = "Разработка",
            features = listOf(
                "Подсветка, автозакрытие тегов и поиск",
                "Предпросмотр под 7 устройств",
                "JS-консоль и работа с файлами",
            ),
            iconKey = "asset:icons/otherredactor.png",
            accent = 0xFFA9835E,
            release = Release(
                tag = "v1.0.0",
                version = "1.0.0",
                name = "OtherRedactor 1.0.0",
                publishedAt = ts(2026, 10, 28),
                apkUrl = "https://github.com/cleveroze/OtherRedactor/releases/download/v1.0.0/OtherRedactor.apk",
                apkName = "OtherRedactor.apk",
                sizeBytes = 1_720_000,
                downloads = 1,
                notes = "Первый публичный выпуск: редактор, предпросмотр на разных экранах, " +
                    "JS-консоль и ванильная палитра.",
            ),
            language = "Kotlin",
        ),
        StoreApp(
            repo = "NotesYou",
            name = "NotesYou",
            tagline = "Заметки, которые приятно трогать",
            description = "Заметки и чек-листы в стиле Material 3 Expressive: одиннадцать тональных цветов, " +
                "шестнадцать фигур и напоминания. Отмеченные пункты уезжают вниз, карточки появляются " +
                "по очереди, а редактор раскрывается прямо из карточки.",
            category = "Продуктивность",
            features = listOf(
                "Заметки и чек-листы с прогрессом",
                "Напоминания, архив и корзина",
                "Поиск, ярлыки и экспорт в JSON",
            ),
            iconKey = "res:notesyou",
            accent = 0xFFBE8A5A,
            release = Release(
                tag = "v1.0.2",
                version = "1.0.2",
                name = "NotesYou 1.0.2",
                publishedAt = ts(2026, 10, 25),
                apkUrl = "https://github.com/cleveroze/NotesYou/releases/download/v1.0.2/NotesYou-1.0.2.apk",
                apkName = "NotesYou-1.0.2.apk",
                sizeBytes = 1_960_000,
                downloads = 0,
                notes = "Исправления редактора и напоминаний. Стабильная синхронизация автосохранений.",
            ),
            language = "Kotlin",
        ),
        StoreApp(
            repo = "WeatherYou",
            name = "WeatherYou",
            tagline = "Минималистичная погода с живым небом",
            description = "Погода на Open-Meteo без ключей и рекламы: огромная температура, почасовой " +
                "прогноз и неделя с диапазонами. Фон подстраивается под погоду — солнце с лучами, " +
                "плывущие облака, дождь, снег или гроза.",
            category = "Погода",
            features = listOf(
                "Живая сцена погоды и анимации",
                "Open-Meteo без API-ключа",
                "Поиск городов и офлайн-кэш",
            ),
            iconKey = "res:weatheryou",
            accent = 0xFF5C8578,
            release = Release(
                tag = "v2.0.4",
                version = "2.0.4",
                name = "Погода 2.0.4",
                publishedAt = ts(2026, 10, 25),
                apkUrl = "https://github.com/cleveroze/WeatherYou/releases/download/v2.0.4/WeatherYou-2.0.4.apk",
                apkName = "WeatherYou-2.0.4.apk",
                sizeBytes = 1_500_000,
                downloads = 0,
                notes = "Пружинная температура, улучшенный поиск городов и аккуратные детали прогноза.",
            ),
            language = "Kotlin",
        ),
        StoreApp(
            repo = "CalcYou",
            name = "CalcYou",
            tagline = "Калькулятор, который умеет почти всё",
            description = "Калькулятор с точными дробями, пошаговыми решениями и стрелочной нотацией Кнута. " +
                "Живые курсы валют, история, питомец Calcy и клавиши, которые меняют форму при нажатии.",
            category = "Инструменты",
            features = listOf(
                "Дроби и решения по шагам",
                "Валюты и офлайн-курсы",
                "Стрелки Кнута и суффиксы до Tg",
            ),
            iconKey = "asset:icons/calcyou.png",
            accent = 0xFFC2A15C,
            release = Release(
                tag = "v1.0.1",
                version = "1.0.1",
                name = "CalcYou 1.0.1",
                publishedAt = ts(2026, 10, 25),
                apkUrl = "https://github.com/cleveroze/CalcYou/releases/download/v1.0.1/CalcYou-1.0.1.apk",
                apkName = "CalcYou-1.0.1.apk",
                sizeBytes = 1_150_000,
                downloads = 0,
                notes = "Курсы валют, точные дроби и маленькие исправления клавиатуры.",
            ),
            language = "JavaScript",
        ),
        StoreApp(
            repo = "KeyboardYou",
            name = "KeyboardYou",
            tagline = "Клавиатура с характером и стеклом",
            description = "Клавиатура в стиле Material You с пятью стилями, включая Liquid Glass, и палитрой " +
                "из обоев. Офлайн-переводчик на 20 языков, история буфера и формы, в которые клавиши " +
                "морфятся при нажатии.",
            category = "Клавиатуры",
            features = listOf(
                "5 стилей и формы Material 3",
                "Офлайн-переводчик на 20 языков",
                "Буфер обмена и умная автозамена",
            ),
            iconKey = "res:keyboardyou",
            accent = 0xFF5FA189,
            release = Release(
                tag = "latest",
                version = "latest",
                name = "Keyboard You — свежая сборка",
                publishedAt = ts(2026, 10, 25),
                apkUrl = "https://github.com/cleveroze/KeyboardYou/releases/download/latest/KeyboardYou.apk",
                apkName = "KeyboardYou.apk",
                sizeBytes = 71_100_000,
                downloads = 3,
                notes = "Свежая сборка с Liquid Glass, переводчиком и обновлёнными словарями.",
            ),
            language = "Kotlin",
        ),
        StoreApp(
            repo = "ClaudeLand",
            name = "ClaudeLand",
            tagline = "Тамагочи целой страны Клодов",
            description = "Симулятор жизни Клодов: они рождаются разными моделями, ходят в школу, работают, " +
                "женятся и стареют. Вместо возраста — остаток контекста: чем ближе к нулю, тем больше " +
                "старичок забывает о себе.",
            category = "Игры",
            features = listOf(
                "46 улучшений в 8 ветках",
                "Семейное древо, религии и города",
                "Офлайн-доход и сохранение на телефоне",
            ),
            iconKey = "res:claudeland",
            accent = 0xFFD48163,
            release = Release(
                tag = "v0.1.0",
                version = "0.1.0",
                name = "ClaudeLand 0.1.0",
                publishedAt = ts(2026, 11, 3),
                apkUrl = "https://github.com/cleveroze/ClaudeLand/releases/download/v0.1.0/ClaudeLand.apk",
                apkName = "ClaudeLand.apk",
                sizeBytes = 930_000,
                downloads = 2,
                notes = "Первый выпуск: жизнь, работа, религии, семейное древо и автопилот судеб.",
            ),
            language = "JavaScript",
        ),
    )

    val byRepo: Map<String, StoreApp> = bundled.associateBy { it.repo.lowercase() }

    val categories: List<String> = bundled.map { it.category }.distinct()

    fun sort(apps: List<StoreApp>): List<StoreApp> = apps.sortedWith(
        compareByDescending<StoreApp> { it.featured }
            .thenByDescending { it.release != null }
            .thenByDescending { it.release?.publishedAt ?: 0L }
            .thenBy { it.name.lowercase() }
    )
}
