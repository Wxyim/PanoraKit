package com.github.yumelira.yumebox.service.runtime.session

internal data class InstalledAppUidEntry(
    val packageName: String,
    val uid: Int,
    val sharedUserId: String? = null,
)

internal object InstalledAppUidMappings {
    fun fromEntries(entries: List<InstalledAppUidEntry>): List<Pair<Int, String>> {
        if (entries.isEmpty()) return emptyList()

        return entries
            .asSequence()
            .filter { it.packageName.isNotBlank() && it.uid > 0 }
            .groupBy { entry ->
                entry.sharedUserId?.takeIf(String::isNotBlank) ?: entry.packageName
            }
            .map { (_, groupedEntries) ->
                val primary = groupedEntries.first()
                val sharedIdentity = primary.sharedUserId?.takeIf(String::isNotBlank)
                val identity =
                    if (groupedEntries.size == 1) {
                        primary.packageName
                    } else {
                        sharedIdentity ?: primary.packageName
                    }
                primary.uid to identity
            }
            .sortedBy { it.first }
    }

    fun fromRootPackageUidMap(packageUidMap: Map<String, Int>): List<Pair<Int, String>> {
        if (packageUidMap.isEmpty()) return emptyList()

        return packageUidMap.entries
            .asSequence()
            .filter { it.key.isNotBlank() && it.value > 0 }
            .groupBy { it.value }
            .map { (uid, entries) -> uid to entries.map { it.key }.sorted().first() }
            .sortedBy { it.first }
    }
}
