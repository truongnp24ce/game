package app

import android.content.Context
import app.DTO.Prompt
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import app.AnimalVilla.R

// The four possible week-end outcomes. Which one plays is decided by the
// player's final Energy / Money / Status (see GamePlayModel.pickEnding).
enum class EndingType { GOOD, BAD, EXHAUSTED, PENNILESS }

class GetInformation(private val context: Context) {

    private var i: Int = 0

    // Danh sách các file JSON local (theo thứ tự ngày)
    private val dayResources = listOf(
        R.raw.tutorial_prompts,  // Day 0 (Tutorial)
        R.raw.monday,            // Day 1
        R.raw.tuesday,           // Day 2
        R.raw.wednesday,         // Day 3
        R.raw.thursday,          // Day 4
        R.raw.friday,            // Day 5
        R.raw.saturday,          // Day 6
        R.raw.sunday             // Day 7
    )

    private val endingResources = mapOf(
        EndingType.GOOD to R.raw.good_ending,
        EndingType.BAD to R.raw.bad_ending,
        EndingType.EXHAUSTED to R.raw.exhausted_ending,
        EndingType.PENNILESS to R.raw.penniless_ending
    )

    // When non-null we are inside an ending sequence and getAllPrompts reads
    // from this resource instead of the per-day file.
    private var endingResource: Int? = null

    // When true we are inside the opening intro sequence and getAllPrompts
    // reads from the intro resource instead of the per-day file. The intro
    // plays once at the very start of a fresh game and then hands off to
    // the tutorial / Monday flow.
    private var introResource: Int? = R.raw.intro

    fun isInIntro(): Boolean = introResource != null

    fun finishIntro() {
        introResource = null
    }

    fun isInEnding(): Boolean = endingResource != null

    // Current day index (0 = tutorial, 1 = Monday, …). Exposed so the
    // save/load layer can snapshot exactly where the player left off.
    fun currentDayIndex(): Int = i

    // Returns which ending file is active, or null when not in an ending.
    // Used by GameSave to persist the active ending across app restarts.
    fun currentEndingType(): EndingType? {
        val active = endingResource ?: return null
        return endingResources.entries.firstOrNull { it.value == active }?.key
    }

    // Restore the gameplay flow to a previously saved state. Mirrors the
    // mutually-exclusive intro / ending / per-day modes the activity drives
    // through nextDayCounter()/startEnding()/finishIntro() during play.
    fun restoreState(dayIndex: Int, inIntro: Boolean, ending: EndingType?) {
        this.i = dayIndex.coerceIn(0, dayResources.size - 1)
        this.introResource = if (inIntro) R.raw.intro else null
        this.endingResource = ending?.let { endingResources[it] }
    }

    // True when the day pointer is on the last entry of dayResources (Sunday).
    // Used by GamePlayModel to know that the next "end of day" sentinel
    // should trigger an ending instead of an out-of-range nextDayCounter().
    fun isLastDay(): Boolean = endingResource == null && introResource == null && i == dayResources.size - 1

    fun startEnding(type: EndingType) {
        endingResource = endingResources[type]
    }

    // Counts next day
    fun nextDayCounter() {
        // Clamp to the last available day so callers that advance past
        // Sunday (e.g. an end-of-day click on the final day) don't trigger
        // an IndexOutOfBoundsException the next time getAllPrompts reads
        // dayResources[i].
        if (i < dayResources.size - 1) {
            this.i = i + 1
        }
    }

    // Collects all prompts for use (ĐỌC TỪ LOCAL FILE)
    private fun getAllPrompts(): MutableList<Prompt>? {
        return try {
            // Lấy resource ID của ngày hiện tại (hoặc của intro / ending đang diễn ra)
            val resourceId = endingResource ?: introResource ?: dayResources[i]

            // Đọc file JSON từ res/raw/
            val inputStream = context.resources.openRawResource(resourceId)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val data = reader.readText()
            reader.close()

            // Parse JSON
            val prompts = mutableListOf<Prompt>()
            val jRecord = JSONObject(data)
            var index = 1

            while (true) {
                if (!jRecord.has(index.toString()))
                    break

                val jPrompt = jRecord.get(index.toString()) as JSONObject

                // Prompt for the player
                val promptText = jPrompt.get("PromptText").toString()
                val id = jPrompt.get("id").toString().toInt()

                // Left Information
                val leftOption = jPrompt.get("LeftOption").toString()
                val leftMoney = jPrompt.get("LeftMoney").toString().toInt()
                val leftEnergy = jPrompt.get("LeftEnergy").toString().toInt()
                val leftStatus = jPrompt.get("LeftStatus").toString().toInt()
                val nextLeft = jPrompt.get("NextLeft").toString().toInt()

                // Right Information
                val rightOption = jPrompt.get("RightOption").toString()
                val rightMoney = jPrompt.get("RightMoney").toString().toInt()
                val rightEnergy = jPrompt.get("RightEnergy").toString().toInt()
                val rightStatus = jPrompt.get("RightStatus").toString().toInt()
                val nextRight = jPrompt.get("NextRight").toString().toInt()

                // Next Day Information (only tutorial_prompts.json defines this
                // field; for the per-day files we default to false so JSON
                // parsing does not throw and leave the screen blank).
                val nextDay = jPrompt.optBoolean("NextDay", false)

                // Optional illustration name (matches a drawable in res/drawable
                // without the file extension, e.g. "yescat"). When absent, the
                // game falls back to a deterministic default chosen in the UI.
                val image = jPrompt.optString("Image", "")

                prompts.add(
                    Prompt(
                        promptText,
                        leftOption,
                        leftEnergy,
                        leftMoney,
                        leftStatus,
                        rightOption,
                        rightEnergy,
                        rightMoney,
                        rightStatus,
                        nextLeft,
                        nextRight,
                        id,
                        nextDay,
                        image
                    )
                )
                index++
            }
            prompts
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Collects needed prompt for use
    fun organizeCurrentPrompt(fetch: String, array: ArrayList<String>): ArrayList<String> {
        // Assign values to prompts
        val queue = getAllPrompts()

        // Prompt ids in the JSON files are 1-based, but `queue` is 0-based,
        // so convert the requested id into the matching list index.
        val idx = fetch.toIntOrNull()?.minus(1) ?: return array

        if (queue == null || idx < 0 || idx >= queue.size) {
            // Trả về array rỗng nếu không load được prompts
            return array
        }

        // Replace previous prompt data instead of stacking it on top, otherwise
        // `array` grows on every choice and the indices used by the caller
        // (e.g. array[12] for NextDay) end up pointing at stale values.
        array.clear()

        array.add(0, queue[idx].PromptText)
        array.add(1, queue[idx].NextLeft.toString())
        array.add(2, queue[idx].NextRight.toString())
        array.add(3, queue[idx].id.toString())
        array.add(4, queue[idx].LeftOption)
        array.add(5, queue[idx].RightOption)
        array.add(6, queue[idx].LeftEnergy.toString())
        array.add(7, queue[idx].RightEnergy.toString())
        array.add(8, queue[idx].LeftMoney.toString())
        array.add(9, queue[idx].RightMoney.toString())
        array.add(10, queue[idx].LeftStatus.toString())
        array.add(11, queue[idx].RightStatus.toString())
        array.add(12, queue[idx].NextDay.toString())
        array.add(13, queue[idx].Image)

        // Return array for use
        return array
    }
}