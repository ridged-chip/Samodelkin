package android.bignerdranch.com

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Contacts
import android.util.Log
import kotlinx.android.synthetic.main.activity_new_character.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.android.Main
import kotlinx.coroutines.android.UI
import kotlinx.coroutines.launch

private const val CHARACTER_DATA_KEY = "CHARACTER_DATA_KEY"

private var Bundle.characterData
get() = getSerializable(CHARACTER_DATA_KEY) as CharacterGenerator.CharacterData
set(value) = putSerializable(CHARACTER_DATA_KEY, value)

class NewCharacterActivity : AppCompatActivity() {
    private var characterData = CharacterGenerator.generate()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.characterData = characterData
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_character)


        savedInstanceState?.let {
            characterData = it.characterData
        } ?: fetchCharacterFromWWW()

        generateButton.setOnClickListener {
            launch (UI) {
                fetchCharacterFromWWW()
            }
        }

        displayCharacterData()
    }

    private fun displayCharacterData() {
        characterData.run {
            nameTextView.text = name
            raceTextView.text = race
            dexterityTextView.text = dex
            wisdomTextView.text = wis
            strengthTextView.text = str
        }
    }

    private fun fetchCharacterFromWWW() {
        GlobalScope.launch (Dispatchers.Main) {
            characterData = fetchCharacterData().await()
            if (characterData.str.toInt() < 10) {
                fetchCharacterFromWWW()
            } else {
                displayCharacterData()
            }
        }
    }
}
