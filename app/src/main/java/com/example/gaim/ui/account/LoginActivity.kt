package com.example.gaim.ui.account
import com.example.gaim.account.login.LoginManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import com.example.gaim.R
import com.example.gaim.ui.*
import com.example.gaim.ui.utility.ErrorChecker
import com.example.gaim.ui.utility.MissingText
import android.content.Context
import java.io.File
import java.io.FileOutputStream
import android.util.Log
import android.widget.Toast

fun copyDatabaseFromAssetsIfNeeded(context: Context, dbName: String) {
    val dbPath = context.getDatabasePath(dbName)
    val TAG = "LoginActivity"

    if (!dbPath.exists()) {
        try {
            // Create the databases directory if it doesn't exist
            dbPath.parentFile?.mkdirs()
            
            // Copy the database from assets
            context.assets.open(dbName).use { input ->
                FileOutputStream(dbPath).use { output ->
                    input.copyTo(output)
                }
            }
            Log.d(TAG, "Successfully copied database from assets")
        } catch (e: Exception) {
            Log.e(TAG, "Error copying database from assets", e)
            throw e
        }
    } else {
        Log.d(TAG, "Database already exists at: ${dbPath.absolutePath}")
    }
}

class LoginActivity : AbstractActivity()  {
    private val usernameID = R.id.username
    private val passwordID = R.id.password
    private val loginID = R.id.login
    private val createAccountID = R.id.create_account
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            copyDatabaseFromAssetsIfNeeded(this, "user_accounts.db")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize database", e)
            Toast.makeText(this, "Error: Unable to access database", Toast.LENGTH_LONG).show()
            finish() // Close the activity since we can't proceed without the database
            return
        }
        
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val usernameInput = findViewById<EditText>(usernameID)
        val passwordInput = findViewById<EditText>(passwordID)

        val loginButton = findViewById<Button>(loginID)
        val createAccountButton = findViewById<Button>(createAccountID)

        val loginCheckers = mutableSetOf<ErrorChecker>(
            MissingText(this, usernameInput, "Username"),
            MissingText(this, passwordInput, "Password")
        )

        loginButton.setOnClickListener {
            if(this.checkErrors(loginCheckers)){
                if(this.loginVerified()){
                    this.nextActivity(MainpageActivity.MAIN)
                }
            }
        }

        createAccountButton.setOnClickListener {
            this.nextActivity(MainpageActivity.CREATEACCOUNT)
        }
    }

    private fun loginVerified(): Boolean {
        val username = findViewById<EditText>(usernameID).text.toString()
        val password = findViewById<EditText>(passwordID).text.toString()

        val loginManager = LoginManager(this)
        return loginManager.verifyCredentials(username, password)
    }
}