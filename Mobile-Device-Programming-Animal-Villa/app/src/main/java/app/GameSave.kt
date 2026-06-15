package app

import android.content.Context

// Lightweight save-game store backed by SharedPreferences. We persist just
// enough state to recreate the gameplay screen the player left: which day
// they are on, which prompt id is showing, whether they are inside the
// intro or one of the four endings, and their current Energy / Money /
// Status. The save is intentionally a single slot — the game itself only
// exposes "Save", "Continue" and "Discard Save", so multiple slots would
// only add complexity without changing the UX.
object GameSave {

    private const val PREFS = "animal_villa_save"

    private const val KEY_HAS_SAVE = "has_save"
    private const val KEY_DAY_INDEX = "day_index"
    private const val KEY_PROMPT_ID = "prompt_id"
    private const val KEY_IN_INTRO = "in_intro"
    private const val KEY_ENDING_TYPE = "ending_type" // empty when not in ending
    private const val KEY_ENERGY = "energy"
    private const val KEY_MONEY = "money"
    private const val KEY_STATUS = "status"

    data class Snapshot(
        val dayIndex: Int,
        val promptId: Int,
        val inIntro: Boolean,
        val endingType: EndingType?,
        val energy: Int,
        val money: Int,
        val status: Int
    )

    fun hasSave(context: Context): Boolean =
        prefs(context).getBoolean(KEY_HAS_SAVE, false)

    fun save(context: Context, snapshot: Snapshot) {
        prefs(context).edit()
            .putBoolean(KEY_HAS_SAVE, true)
            .putInt(KEY_DAY_INDEX, snapshot.dayIndex)
            .putInt(KEY_PROMPT_ID, snapshot.promptId)
            .putBoolean(KEY_IN_INTRO, snapshot.inIntro)
            .putString(KEY_ENDING_TYPE, snapshot.endingType?.name ?: "")
            .putInt(KEY_ENERGY, snapshot.energy)
            .putInt(KEY_MONEY, snapshot.money)
            .putInt(KEY_STATUS, snapshot.status)
            .apply()
    }

    fun load(context: Context): Snapshot? {
        val p = prefs(context)
        if (!p.getBoolean(KEY_HAS_SAVE, false)) return null
        val endingName = p.getString(KEY_ENDING_TYPE, "") ?: ""
        val ending = if (endingName.isEmpty()) null else
            runCatching { EndingType.valueOf(endingName) }.getOrNull()
        return Snapshot(
            dayIndex = p.getInt(KEY_DAY_INDEX, 0),
            promptId = p.getInt(KEY_PROMPT_ID, 1),
            inIntro = p.getBoolean(KEY_IN_INTRO, true),
            endingType = ending,
            energy = p.getInt(KEY_ENERGY, 50),
            money = p.getInt(KEY_MONEY, 50),
            status = p.getInt(KEY_STATUS, 50)
        )
    }

    fun clear(context: Context) {
        prefs(context).edit().clear().apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
