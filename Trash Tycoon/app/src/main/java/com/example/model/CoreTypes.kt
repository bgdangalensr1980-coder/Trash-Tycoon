package com.example.model

enum class WasteType(
    val displayName: String,
    val colorHex: Long,
    val iconName: String,
    val basePricePerTon: Double,
    val isHazardous: Boolean = false,
    val recyclingOutput: ResourceType
) {
    MIXED_TRASH("Смешанные отходы", 0xFF8D6E63, "trash", 15.0, false, ResourceType.RDF_FUEL),
    SCRAP_METAL("Металлолом (Чермет)", 0xFF78909C, "metal", 120.0, false, ResourceType.RECYCLED_STEEL),
    PLASTIC("Пластиковые отходы (PET/HDPE)", 0xFF42A5F5, "plastic", 85.0, false, ResourceType.PLASTIC_PELLETS),
    COPPER_WIRE("Медные кабели и проводка", 0xFFFF7043, "copper", 380.0, false, ResourceType.PURE_COPPER),
    E_WASTE("Электронный лом (Печатные платы)", 0xFF26A69A, "chip", 520.0, false, ResourceType.PRECIOUS_METALS),
    ORGANIC("Органические отходы", 0xFF8BC34A, "compost", 30.0, false, ResourceType.BIO_FERTILIZER),
    HAZMAT_CHEMICAL("Токсичные химикаты", 0xFFFF1744, "hazmat", 0.0, true, ResourceType.SAFE_NEUTRALIZED)
}

enum class ResourceType(
    val displayName: String,
    val colorHex: Long,
    val unit: String,
    val baseMarketPrice: Double
) {
    RECYCLED_STEEL("Очищенная сталь", 0xFF90A4AE, "т", 240.0),
    PLASTIC_PELLETS("Гранулы полимера", 0xFF64B5F6, "т", 195.0),
    PURE_COPPER("Катодная медь (99.9%)", 0xFFFF8A65, "т", 820.0),
    PRECIOUS_METALS("Драгметаллы (Au/Ag)", 0xFFFFD54F, "кг", 4200.0),
    BIO_FERTILIZER("Био-удобрение", 0xFFAED581, "т", 75.0),
    RDF_FUEL("Топливные RDF-брикеты", 0xFFBCAAA4, "т", 90.0),
    SAFE_NEUTRALIZED("Нейтрализованный шлам", 0xFF81C784, "барр", 45.0),
    GREEN_ENERGY("Зелёная электроэнергия", 0xFF00E676, "МВт·ч", 110.0)
}

enum class VehicleType(
    val displayName: String,
    val speed: Float,
    val capacity: Float,
    val powerKw: Float,
    val colorHex: Long,
    val description: String
) {
    BULLDOZER("Бульдозер Т-140 'Титан'", 4.2f, 15f, 180f, 0xFFF59E0B, "Тяжёлый отвал для сгребания и перемещения массивных куч мусора."),
    EXCAVATOR_CLAW("Экскаватор-перегружатель 'Клешня'", 3.0f, 8f, 140f, 0xFFE65100, "Гидравлический грейферный захват для погрузки металлолома в дробилки."),
    DUMP_TRUCK("Самосвал БелАЗ-Mini 45т", 6.5f, 25f, 240f, 0xFFD97706, "Скоростная перевозка отходов от весовой рампы к приёмным бункерам."),
    FORKLIFT("Электропогрузчик 'EcoLift'", 5.0f, 6f, 50f, 0xFF00ACC1, "Манёвренная транспортировка спрессованных тюков и бочек на склад.")
}

enum class MachineNodeType(
    val displayName: String,
    val icon: String,
    val baseProcessingRate: Float, // tons/sec
    val energyCostKw: Float,
    val basePurity: Float,
    val description: String,
    val cost: Double
) {
    INTAKE_HOPPER("Приёмный бункер", "hopper", 2.0f, 15f, 0.5f, "Принимает сырой мусор и подаёт на конвейерную линию.", 3000.0),
    CONVEYOR_BELT("Конвейерная лента", "belt", 5.0f, 5f, 1.0f, "Транспортирует отходы между узлами обработки.", 500.0),
    HIGH_SPEED_BELT("Скоростной конвейер", "fast_belt", 10.0f, 12f, 1.0f, "Высокоскоростная лента для оптимизации логистических потоков.", 1200.0),
    PRIMARY_SHREDDER("Промышленный шредер", "shredder", 1.8f, 45f, 0.7f, "Измельчает крупногабаритные отходы и корпуса.", 6500.0),
    MAGNETIC_SEPARATOR("Магнитный сепаратор", "magnet", 2.2f, 30f, 0.95f, "Извлекает сталь и железо мощным электромагнитным барабаном.", 8000.0),
    OPTICAL_SORTER("Оптический NIR-лазер", "laser", 1.5f, 40f, 0.92f, "Спектрометрическое разделение полимеров и плат.", 12500.0),
    HYDRAULIC_PRESS("Гидравлический пресс", "press", 1.6f, 55f, 0.98f, "Спрессовывает отсортированное вторсырье в экспортные тюки.", 11000.0),
    BIO_DIGESTER("Био-реактор компостирования", "bio", 1.0f, 20f, 0.90f, "Анаэробное сбраживание органики в экологичное удобрение.", 9500.0),
    HAZMAT_SCRUBBER("Нейтрализатор токсинов", "scrubber", 0.8f, 50f, 0.99f, "Очищает химические отходы, предотвращая эко-штрафы.", 15000.0),
    PRECIOUS_SMELTER("Печь аффинажа драгметаллов", "furnace", 0.5f, 80f, 0.99f, "Выплавляет золото и чистую медь из микросхем.", 22000.0)
}
