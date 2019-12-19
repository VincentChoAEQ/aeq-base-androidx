/**
 * Created by Vincent Cho on 12/19/19.
 * Copyright (c) 2019 aequilibrium LLC. All rights reserved.
 */
package ca.aequilibrium.base.ui

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.app_bar_main.*
import androidx.core.view.GravityCompat
import androidx.navigation.ui.*
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.android.synthetic.main.activity_main.*
import ca.aequilibrium.base.R
import ca.aequilibrium.base.auth.Authenticator
import ca.aequilibrium.base.auth.CredentialKeeper
import ca.aequilibrium.base.util.Preferences
import com.crashlytics.android.Crashlytics
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity(){

    private lateinit var appBarConfiguration: AppBarConfiguration
    val authenticator : Authenticator by inject { parametersOf(this) }
    val credentialKeeper : CredentialKeeper by inject { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        //changing theme, must be put before super.onCreate()
        setTheme(Preferences.theme)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        //if not set subtitle == null, Calligraphy will make the title flicker.
        supportActionBar?.setSubtitle(null);

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_camera,
                R.id.nav_map,
                R.id.nav_pay,
                R.id.nav_notification
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)


//        val cameraItem = navigationView.menu.findItem(R.id.nav_camera)
//        cameraItem.setOnMenuItemClickListener {
//            onCameraItemClicked()
//            true
//        }

        //handling logout
        val logoutItem = navigationView.menu.findItem(R.id.nav_logout)
        logoutItem.setOnMenuItemClickListener {
            onSignOutItemClicked()
            true
        }

        //navigation to transformer app
        val transItem = navigationView.menu.findItem(R.id.nav_trans)
        transItem.setOnMenuItemClickListener {
            onTransformerItemClicked()
            true
        }
    }

//    private fun onCameraItemClicked(){
//        CameraActivity.startActivity(this)
//        drawer_layout.closeDrawer(GravityCompat.START)
//    }

    private fun onTransformerItemClicked(){
        TransformerActivity.startActivity(this)
        drawer_layout.closeDrawer(GravityCompat.START)
    }

    private fun onSignOutItemClicked(){
        signOutAndRevokeCredential()
        drawer_layout.closeDrawer(GravityCompat.START)
        SignInActivity.startActivity(this)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                findNavController(R.id.nav_host_fragment).navigate((Uri.parse("baseapp://navgraph/settings")))
                true
            }
            R.id.action_crash -> {
                Crashlytics.getInstance().crash()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun hideBarAndFloatButton(){
        toolbar.visibility = View.GONE
        fab.hide()
    }

    fun showBarAndFloatButton(){
        toolbar.visibility = View.VISIBLE
        fab.show()
    }

    fun signOutAndRevokeCredential(){
        //todo check if we need to force user input password for google signin.
        authenticator.signOut()
        credentialKeeper.revokeCurrentCredential()
    }

    companion object {

        fun startActivity(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }
}
