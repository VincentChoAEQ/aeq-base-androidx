package ca.aequilibrium.base.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ca.aequilibrium.base.App
import ca.aequilibrium.base.R
import ca.aequilibrium.base.auth.Authenticator
import ca.aequilibrium.base.auth.CredentialKeeper
import ca.aequilibrium.base.ui.login.EmailLoginFragment
import ca.aequilibrium.base.ui.login.LoginSelectionFragment
import com.orhanobut.logger.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class SignInActivity : AppCompatActivity()
{
    val authenticator : Authenticator by inject {parametersOf(this)}
    val credentialKeeper : CredentialKeeper by inject {parametersOf(this)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_signin)

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) as FrameLayout != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            val firstFragment = LoginSelectionFragment()

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.arguments = intent.extras

            // Add the fragment to the 'fragment_container' FrameLayout
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, firstFragment).commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == App.RC_GOOGLE_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val task = authenticator.getGoogleSignedInAccountFromIntent(data)
                task.result?.let {
                    credentialKeeper.saveCredential(it)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                Logger.d(getString(R.string.notify_succeed_to_save))
                            },
                            {
                                Toast.makeText(this@SignInActivity , getString(R.string.err_fail_to_save), Toast.LENGTH_SHORT).show()
                            })
                }
                showMainActivity()
            }
            else {
                //should not reach here
                assert(false)
            }
        }
    }

    fun showEmailLoginFragment() {
        val newFragment = EmailLoginFragment()

        val transaction = supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, newFragment)
            addToBackStack(null)
        }
        transaction.commit();
    }

    fun showMainActivity(){
        MainActivity.startActivity(this)
        finish()
    }

    companion object {

        fun startActivity(context: Context) {
            val intent = Intent(context, SignInActivity::class.java)
            context.startActivity(intent)
        }
    }

}