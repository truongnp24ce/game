package app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import app.AnimalVilla.R

// Title screen displayed after login (and after the player returns from
// gameplay via the exit icon or the ending "Back to Start" button). The
// whole mushroom-villa illustration is tappable: with no saved game the
// tap simply launches a fresh GamePlayModel; if a save exists we instead
// surface a two-button overlay so the player can choose to continue the
// saved run or discard it and start over.
class TitleScreenActivity : AppCompatActivity() {

    private val appMethods = AppMethods()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title_screen)
        supportActionBar?.hide()
        appMethods.hideSystemBars(actionBar, window, findViewById(R.id.titleScreenRoot))

        val root: View = findViewById(R.id.titleScreenRoot)
        val overlay: View = findViewById(R.id.saveChoiceOverlay)
        val continueButton: Button = findViewById(R.id.continueButton)
        val discardButton: Button = findViewById(R.id.discardSaveButton)

        // Tapping anywhere on the title art is the primary way to enter the
        // game. The overlay (when visible) consumes its own taps so the
        // background click doesn't bypass the choice.
        root.setOnClickListener {
            if (GameSave.hasSave(this)) {
                overlay.visibility = View.VISIBLE
            } else {
                launchGame(resume = false)
            }
        }

        continueButton.setOnClickListener {
            launchGame(resume = true)
        }

        discardButton.setOnClickListener {
            GameSave.clear(this)
            overlay.visibility = View.GONE
            launchGame(resume = false)
        }
    }

    override fun onResume() {
        super.onResume()
        // Hide the overlay each time we come back so a stale "continue?"
        // prompt doesn't sit on top of the title art.
        findViewById<View>(R.id.saveChoiceOverlay).visibility = View.GONE
    }

    private fun launchGame(resume: Boolean) {
        val intent = Intent(this, GamePlayModel::class.java)
        intent.putExtra(GamePlayModel.EXTRA_RESUME_SAVE, resume)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
