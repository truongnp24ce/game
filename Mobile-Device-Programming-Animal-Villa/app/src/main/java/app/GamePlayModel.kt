package app

import android.os.Bundle
import android.view.View
import android.widget.Button
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
        leftButtonTextView: Button,
        rightButtonTextView: Button,
        nextDayButtonTextView: Button
    ) {
        getInformation.organizeCurrentPrompt(nextPromptId, array)
        if (array.size >= 6) {
            textView.text = array[0]
            leftButtonTextView.text = array[4]
            rightButtonTextView.text = array[5]
            nextDayButtonTextView.text = "Go To Next Day..."
        }
    }
}
