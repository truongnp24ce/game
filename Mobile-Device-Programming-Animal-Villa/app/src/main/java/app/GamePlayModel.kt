package app

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
            val leftButtonTextView = findViewById<Button>(R.id.leftButton)
            val rightButtonTextView = findViewById<Button>(R.id.rightButton)
            val nextDayButtonTextView = findViewById<Button>(R.id.nextDayButton)
            leftButtonTextView.text = array[4]
            rightButtonTextView.text = array[5]
            nextDayButtonTextView.text = "Go to next Day"

            //Do this when left button is pressed
            leftButton.setOnClickListener {
                //left button follows the NextLeft id (array[1])
                changeButtonsAndText(
                    array[1],
                    array,
                    textView,
                    promptImage,
                    leftButtonTextView,
                    rightButtonTextView,
                    nextDayButtonTextView
                )
                checkDay(array[12].toBoolean(), leftButton, rightButton, nextDayButton)
            }

            //Do this when right button is pressed
            rightButton.setOnClickListener {
                //right button follows the NextRight id (array[2])
                changeButtonsAndText(
                    array[2],
                    array,
                    textView,
                    promptImage,
                    leftButtonTextView,
                    rightButtonTextView,
                    nextDayButtonTextView
                )
                checkDay(array[12].toBoolean(), leftButton, rightButton, nextDayButton)
            }

            //Do this when next day button is pressed
            nextDayButton.setOnClickListener{
                //next day button follows the NextRight id (array[2]) like before
                changeButtonsAndText(
                    array[2],
                    array,
                    textView,
                    promptImage,
                    leftButtonTextView,
                    rightButtonTextView,
                    nextDayButtonTextView
                )
                checkDay(array[12].toBoolean(), leftButton, rightButton, nextDayButton)

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

    //Get json for correct day
    private fun checkDay(NextDay: Boolean, leftButton: Button, rightButton: Button, nextDayButton: Button) {
        if(NextDay){
            hideButtons(leftButton, rightButton, nextDayButton)
            getInformation.nextDayCounter()
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
        val resolvedId = if (nextPromptId == "0") {
            getInformation.nextDayCounter()
            "1"
        } else {
            nextPromptId
        }
        getInformation.organizeCurrentPrompt(resolvedId, array)
        if (array.size >= 6) {
            textView.text = array[0]
            leftButtonTextView.text = array[4]
            rightButtonTextView.text = array[5]
            nextDayButtonTextView.text = "Go To Next Day..."
            updatePromptImage(promptImage, array)
        }
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
