package app

import android.content.Context
import app.DTO.Prompt
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import app.AnimalVilla.R
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

    // Counts next day
    fun nextDayCounter() {
        this.i = i + 1
    }

    // Collects all prompts for use (ĐỌC TỪ LOCAL FILE)
    private fun getAllPrompts(): MutableList<Prompt>? {
        return try {
            // Lấy resource ID của ngày hiện tại
            val resourceId = dayResources[i]

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

                // Next Day Information
                val nextDay = jPrompt.get("NextDay").toString().toBoolean()

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
                        nextDay
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

        if (queue == null || fetch.toInt() >= queue.size) {
            // Trả về array rỗng nếu không load được prompts
            return array
        }

        array.add(0, queue[fetch.toInt()].PromptText)
        array.add(1, queue[fetch.toInt()].NextLeft.toString())
        array.add(2, queue[fetch.toInt()].NextRight.toString())
        array.add(3, queue[fetch.toInt()].id.toString())
        array.add(4, queue[fetch.toInt()].LeftOption)
        array.add(5, queue[fetch.toInt()].RightOption)
        array.add(6, queue[fetch.toInt()].LeftEnergy.toString())
        array.add(7, queue[fetch.toInt()].RightEnergy.toString())
        array.add(8, queue[fetch.toInt()].LeftMoney.toString())
        array.add(9, queue[fetch.toInt()].RightMoney.toString())
        array.add(10, queue[fetch.toInt()].LeftStatus.toString())
        array.add(11, queue[fetch.toInt()].RightStatus.toString())
        array.add(12, queue[fetch.toInt()].NextDay.toString())

        // Return array for use
        return array
    }
}