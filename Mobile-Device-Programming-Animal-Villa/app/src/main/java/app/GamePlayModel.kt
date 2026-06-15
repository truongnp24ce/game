package app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import app.AnimalVilla.R

class GamePlayModel: AppCompatActivity() {

    //Variables
    private lateinit var getInformation: GetInformation
    private var array:ArrayList<String> = arrayListOf() //Holds a list of array items for variables above. Will be used to add values to variables above

    // Player stats displayed on the three bottom tiles (heart / fire / dollar).
    // They mirror the starting values used by RegistrationActivity for new
    // players and get adjusted by each chosen option's Energy / Money /
    // Status delta.
    private var energy = 50
    private var money = 50
    private var status = 50

    private lateinit var statEnergyView: TextView
    private lateinit var statMoneyView: TextView
    private lateinit var statStatusView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        //Lets the game run. super.onCreate() and setContentView() MUST run
        //before we touch any views or the window insets controller, otherwise
        //findViewById() returns null and the rest of onCreate() aborts,
        //leaving the prompt box and choice buttons blank.
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_play)

        //Hides Action and Status bars (safe to call now that the layout
        //is inflated and the activity is fully initialised).
        supportActionBar?.hide()
        hideSystemBars()

        // Initialize GetInformation with context
        getInformation = GetInformation(this)

        val textView: TextView = findViewById(R.id.promptBox)
        val promptImage: ImageView = findViewById(R.id.promptImage)
        val nextDayButton: Button = findViewById(R.id.nextDayButton)
        val rightButton: Button = findViewById(R.id.rightButton)
        val leftButton: Button = findViewById(R.id.leftButton)

        statEnergyView = findViewById(R.id.statEnergy)
        statMoneyView = findViewById(R.id.statMoney)
        statStatusView = findViewById(R.id.statStatus)
        refreshStats()

        // Clear array before populating
        array.clear()

        //Gets the first prompt (prompt ids are 1-based)
        getInformation.organizeCurrentPrompt("1", array)

        // Check if array has enough elements before accessing
        if (array.size >= 13) {
            //Checks what day the game is on
            checkDay(array[12].toBoolean(), leftButton, rightButton, nextDayButton)

            //Displays first prompt
            textView.text = array[0]
            updatePromptImage(promptImage, array)

            //Label the buttons
            leftButton.text = array[4]
            rightButton.text = array[5]
            nextDayButton.text = "Go to next Day"

            //Do this when left button is pressed
            leftButton.setOnClickListener {
                //left button follows the NextLeft id (array[1])
                applyChoiceDeltas(isLeft = true)
                changeButtonsAndText(
                    array[1],
                    array,
                    textView,
                    promptImage,
                    leftButton,
                    rightButton,
                    nextDayButton
                )
                checkDay(array[12].toBoolean(), leftButton, rightButton, nextDayButton)
            }

            //Do this when right button is pressed
            rightButton.setOnClickListener {
                //right button follows the NextRight id (array[2])
                applyChoiceDeltas(isLeft = false)
                changeButtonsAndText(
                    array[2],
                    array,
                    textView,
                    promptImage,
                    leftButton,
                    rightButton,
                    nextDayButton
                )
                checkDay(array[12].toBoolean(), leftButton, rightButton, nextDayButton)
            }

            //Do this when next day button is pressed.
            //
            //Previously this re-used `array[2]` (NextRight) as the prompt id
            //which meant clicking "Next Day" on the tutorial wrap-up loaded
            //prompt 11 of Monday instead of Monday's prompt 1, and the user
            //would visually appear stuck on a confusing scene. Always advance
            //to the first prompt of the next day instead.
            nextDayButton.setOnClickListener {
                getInformation.nextDayCounter()
                getInformation.organizeCurrentPrompt("1", array)
                if (array.size >= 13) {
                    textView.text = array[0]
                    leftButton.text = array[4]
                    rightButton.text = array[5]
                    updatePromptImage(promptImage, array)
                    checkDay(array[12].toBoolean(), leftButton, rightButton, nextDayButton)
                }
            }
        } else {
            // Fallback so the screen is never visually empty if the prompt
            // data could not be loaded for any reason.
            textView.text = getString(R.string.gameplay_load_error)
            promptImage.visibility = View.GONE
            leftButton.visibility = View.GONE
            rightButton.visibility = View.GONE
            nextDayButton.visibility = View.GONE
        }

    }

    //Hides the system bars when app is running
    private fun hideSystemBars() {
        val windowInsetsController =
            WindowCompat.getInsetsController(window, findViewById(R.id.GamePlay))
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }


    fun showButtons(leftButton: Button, rightButton: Button, nextDayButton: Button) {
        leftButton.visibility = View.VISIBLE
        rightButton.visibility = View.VISIBLE
        nextDayButton.visibility = View.GONE
    }

    fun hideButtons(leftButton: Button, rightButton: Button, nextDayButton: Button) {
        leftButton.visibility = View.GONE
        rightButton.visibility = View.GONE
        nextDayButton.visibility = View.VISIBLE
    }

    //Get json for correct day. When the prompt advertises NextDay=true (only
    //the tutorial does this today) we surface the "Next Day" button; the
    //actual day counter is only incremented by the nextDayButton click
    //handler so we don't double-advance and skip a day's content.
    private fun checkDay(NextDay: Boolean, leftButton: Button, rightButton: Button, nextDayButton: Button) {
        if(NextDay){
            hideButtons(leftButton, rightButton, nextDayButton)
        }
        else{
            showButtons(leftButton, rightButton, nextDayButton)
        }
    }

    private fun changeButtonsAndText(
        nextPromptId: String,
        array: ArrayList<String>,
        textView: TextView,
        promptImage: ImageView,
        leftButtonTextView: Button,
        rightButtonTextView: Button,
        nextDayButtonTextView: Button
    ) {
        // The per-day JSON files use a NextLeft / NextRight value of 0 as an
        // "end of day" sentinel (e.g. the wrap-up prompts on Monday whose
        // buttons both read "Continue to the next day."). Without special
        // handling those clicks resolve to prompt id -1 in organizeCurrentPrompt
        // and the screen never updates, leaving the buttons visibly clickable
        // but unresponsive. Detect that sentinel here and advance to the first
        // prompt of the following day instead.
        //
        // Two extra cases are layered on top of that:
        //   * If we're already inside an ending, the "0" sentinel means the
        //     player just pressed "Back to Start" on the final achieved-screen,
        //     so finish the activity and return to the title.
        //   * If we're on Sunday (the last day) the "0" sentinel marks the end
        //     of the week and should hand off to the appropriate ending file
        //     instead of looping back to Sunday's first prompt.
        val resolvedId: String = if (nextPromptId == "0") {
            when {
                getInformation.isInEnding() -> {
                    returnToTitle()
                    return
                }
                getInformation.isInIntro() -> {
                    // Intro just finished. Drop the intro flag so the very next
                    // load reads the tutorial / day-1 file, then start the
                    // tutorial from its first prompt.
                    getInformation.finishIntro()
                    "1"
                }
                getInformation.isLastDay() -> {
                    getInformation.startEnding(pickEnding())
                    "1"
                }
                else -> {
                    getInformation.nextDayCounter()
                    "1"
                }
            }
        } else {
            nextPromptId
        }

        // Remember the current prompt so we can detect when the requested id
        // doesn't exist (e.g. Tuesday's prompt 3 points at id 4/5 which were
        // never authored). Without this guard the button click silently
        // no-ops because organizeCurrentPrompt returns the array unchanged
        // and the player appears stuck.
        val previousId = array.getOrNull(3)
        getInformation.organizeCurrentPrompt(resolvedId, array)
        if (array.getOrNull(3) == previousId && resolvedId != previousId) {
            // Requested prompt was missing from the day file; treat it like an
            // end-of-day sentinel so the story keeps moving forward.
            when {
                getInformation.isInIntro() -> getInformation.finishIntro()
                getInformation.isInEnding() -> { /* stay in ending */ }
                getInformation.isLastDay() -> getInformation.startEnding(pickEnding())
                else -> getInformation.nextDayCounter()
            }
            getInformation.organizeCurrentPrompt("1", array)
        }

        if (array.size >= 6) {
            textView.text = array[0]
            leftButtonTextView.text = array[4]
            rightButtonTextView.text = array[5]
            nextDayButtonTextView.text = "Go To Next Day..."
            updatePromptImage(promptImage, array)
        }
    }

    // Chooses which ending file plays based on the player's final stats.
    // BAD has highest priority (a tarnished reputation overrides the other
    // outcomes), then EXHAUSTED, then PENNILESS; otherwise the player gets
    // the GOOD ending. Thresholds are intentionally generous so that each
    // ending is reachable through the choices available across the week.
    private fun pickEnding(): EndingType {
        return when {
            status < 30 -> EndingType.BAD
            energy < 30 -> EndingType.EXHAUSTED
            money < 40 -> EndingType.PENNILESS
            else -> EndingType.GOOD
        }
    }

    // Closes the gameplay activity and returns the player to the title
    // screen. Called when "Back to Start" is pressed on an ending screen.
    private fun returnToTitle() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    // Applies the Energy / Money / Status deltas attached to the choice the
    // player just clicked and refreshes the three stat tiles in the bottom
    // bar. Stats are clamped to 0..100 to mirror typical UI bounds.
    private fun applyChoiceDeltas(isLeft: Boolean) {
        if (array.size < 12) return
        val energyDelta = (if (isLeft) array[6] else array[7]).toIntOrNull() ?: 0
        val moneyDelta = (if (isLeft) array[8] else array[9]).toIntOrNull() ?: 0
        val statusDelta = (if (isLeft) array[10] else array[11]).toIntOrNull() ?: 0

        energy = (energy + energyDelta).coerceIn(0, 100)
        money = (money + moneyDelta).coerceIn(0, 100)
        status = (status + statusDelta).coerceIn(0, 100)
        refreshStats()
    }

    private fun refreshStats() {
        if (::statEnergyView.isInitialized) statEnergyView.text = energy.toString()
        if (::statMoneyView.isInitialized) statMoneyView.text = money.toString()
        if (::statStatusView.isInitialized) statStatusView.text = status.toString()
    }

    // Pool of bundled character drawables used as a deterministic fallback
    // when a prompt does not specify its own "Image" name.
    private val fallbackImages = listOf(
        "kidrabbit", "yescat", "nocat",
        "yesraccoon", "noraccoon", "sonraccoon", "nodadraccoon",
        "yesmomrabbit", "nomomrabbit", "yesdadrabbit",
        "yesmomcow", "sadmomcow"
    )

    // Resolves the illustration for the current prompt. Prefers the JSON
    // "Image" value (array[13]); otherwise picks a deterministic image from
    // the bundled drawables based on the prompt id so each prompt has a
    // consistent visual instead of an empty box.
    private fun updatePromptImage(promptImage: ImageView, array: ArrayList<String>) {
        val explicitName = if (array.size > 13) array[13] else ""
        val promptId = array.getOrNull(3)?.toIntOrNull() ?: 0

        val resName = if (explicitName.isNotBlank()) {
            explicitName
        } else {
            fallbackImages[promptId.mod(fallbackImages.size)]
        }

        val resId = resources.getIdentifier(resName, "drawable", packageName)
        if (resId != 0) {
            promptImage.setImageResource(resId)
            promptImage.visibility = View.VISIBLE
        } else {
            promptImage.setImageDrawable(null)
            promptImage.visibility = View.GONE
        }
    }
}
