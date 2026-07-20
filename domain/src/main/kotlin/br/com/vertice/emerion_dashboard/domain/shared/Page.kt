package br.com.vertice.emerion_dashboard.domain.shared

/**
 * Framework-agnostic pagination result. Used as the return type of domain
 * ports and use cases so the domain/application layers never depend on
 * Spring Data's `Page`/`Pageable` types.
 */
data class Page<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
) {
    val totalPages: Int
        get() = if (size == 0) 0 else ((totalElements + size - 1) / size).toInt()
}

/**
 * Framework-agnostic pagination request.
 */
data class PageRequest(
    val page: Int,
    val size: Int,
)
