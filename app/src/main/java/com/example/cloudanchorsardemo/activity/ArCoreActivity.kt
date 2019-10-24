package com.example.cloudanchorsardemo.activity

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.cloudanchorsardemo.R
import com.example.cloudanchorsardemo.database.FirebaseDatabaseManager
import com.example.cloudanchorsardemo.dialog.ResolveDialog
import com.example.cloudanchorsardemo.fragment.ArCoreFragment
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.google.firebase.database.DataSnapshot
import kotlinx.android.synthetic.main.activity_arcore.*
import org.json.JSONObject
import java.util.concurrent.CompletableFuture


class ArCoreActivity : AppCompatActivity() {

    // Declare a CloudAnchor and an AppAnchorState
    private var cloudAnchor: Anchor? = null
    private var appAnchorState = AppAnchorState.NONE

    private var arCoreFragment: ArCoreFragment? = null
    private var firebaseDatabaseManager: FirebaseDatabaseManager? = null
    private var manualShortCode = 99;

    private enum class AppAnchorState {
        NONE,
        HOSTING,
        HOSTED,
        RESOLVING,
        RESOLVED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arcore)

        arCoreFragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArCoreFragment?
        arCoreFragment?.planeDiscoveryController?.hide()
        arCoreFragment?.arSceneView?.scene?.addOnUpdateListener {
            Scene.OnUpdateListener { p0 -> updateAnchorIfNecessary() }
        }
        arCoreFragment?.arSceneView?.scene?.addOnUpdateListener { p0 -> updateAnchorIfNecessary() }
        firebaseDatabaseManager = FirebaseDatabaseManager(this)

        initListeners()
    }

    private fun initListeners() {
        save_button.setOnClickListener {
            saveAnchors();
        }

        clear_button.setOnClickListener {
            setCloudAnchor(null)
        }

        resolve_button.setOnClickListener(View.OnClickListener {
            ResolveDialog(
                this,
                object : ResolveDialog.PositiveButtonListener {
                    override fun onPositiveButtonClicked(dialogValue: String) {
                        resolveAnchor(dialogValue)
                    }
                },
                getString(R.string.resolve),
                View.VISIBLE,
                View.VISIBLE
            ).show()
        })


        arCoreFragment?.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, _: MotionEvent ->
            var userInputView = layoutInflater.inflate(R.layout.user_input, null) as EditText;
            AlertDialog.Builder(this)
                .setView(userInputView)
                .setPositiveButton("Add", DialogInterface.OnClickListener { dialog, which ->
                    manualShortCode = Integer.parseInt(userInputView.text.toString());
                    val newAnchor = arCoreFragment?.arSceneView?.session?.hostCloudAnchor(hitResult.createAnchor())
                    setCloudAnchor(newAnchor)
                    appAnchorState = AppAnchorState.HOSTING
                    Toast.makeText(this, "Now hosting anchor...", Toast.LENGTH_LONG).show()
                    arCoreFragment?.let { placeObject(it, newAnchor) }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
            if (plane.type != Plane.Type.HORIZONTAL_UPWARD_FACING || appAnchorState != AppAnchorState.NONE) {}
        }
    }

    fun saveAnchors() {

    }
// I have to reimplement this so that it fetches all the id.
    fun resolveAnchor(dialogValue: String) {

        val shortCode = Integer.parseInt(dialogValue)

        firebaseDatabaseManager?.getCloudAnchorID(shortCode, object :
            FirebaseDatabaseManager.CloudAnchorIdListener {
            override fun onCloudAnchorIdAvailable(cloudAnchors: Iterable<DataSnapshot>?) {
                cloudAnchors?.forEach {
                    val resolvedAnchor = arCoreFragment?.arSceneView?.session?.resolveCloudAnchor(it.getValue() as String?)
                    setCloudAnchor(resolvedAnchor)
                    showMessage("Now Resolving Anchor...")

                    arCoreFragment?.let { placeObject(it, cloudAnchor) }
                    appAnchorState = AppAnchorState.RESOLVING
                }
            }

        })

    }

    fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun setCloudAnchor(newAnchor: Anchor?) {
// Because of some synchronicity, the below codebase fails: java.util.ConcurrentModificationException
// And when I am trying to remove a node with without the for loop, the ScenView gets destroyed (I think), leaving the screen entirely black.
//        if(newAnchor == null) {
//            var parent = arCoreFragment?.arSceneView?.scene
//            // There must be a cleaner way to write those two below ...
//            var children = parent?.children
//            if (parent != null && children != null) {
//                // Detaches all the renderables.
//                for(node in children) {
//                    node.isEnabled = false
//                    parent.removeChild(node)
//                }
//            }
//        }
        cloudAnchor = newAnchor
        appAnchorState = AppAnchorState.NONE
    }

    @Synchronized
    private fun updateAnchorIfNecessary() {
        if (appAnchorState != AppAnchorState.HOSTING && appAnchorState != AppAnchorState.RESOLVING) {
            return
        }
        val cloudState = cloudAnchor?.cloudAnchorState
        cloudState?.let { it ->
            if (appAnchorState == AppAnchorState.HOSTING) {
                if (it.isError) {
                    Toast.makeText(this, "Error hosting anchor.. $it", Toast.LENGTH_LONG).show()

                    appAnchorState = AppAnchorState.NONE
                } else if (it == Anchor.CloudAnchorState.SUCCESS) {
//                    firebaseDatabaseManager?.nextShortCode(object :
//                        FirebaseDatabaseManager.ShortCodeListener {
//                        override fun onShortCodeAvailable(shortCode: Int?) {
//                            if (shortCode == null) {
//                                showMessage("Could not get shortCode")
//                                return
//                            }
//                            // This is where the short code gets stored in Firebase.
//                            cloudAnchor?.let {
//                                firebaseDatabaseManager?.storeUsingShortCode(
//                                    manualShortCode,
//                                    it.cloudAnchorId
//                                )
//                            }
//                            showMessageWitAnchorId("Anchor hosted with: " + manualShortCode)
//                        }
//
//                    })
                    cloudAnchor?.let {
                        firebaseDatabaseManager?.storeUsingShortCode(
                            manualShortCode,
                            it.cloudAnchorId
                        )
                    }
                    appAnchorState = AppAnchorState.HOSTED
                }
            } else if (appAnchorState == AppAnchorState.RESOLVING) {
                if (it.isError) {
                    Toast.makeText(this, "Error hosting anchor.. $it", Toast.LENGTH_LONG).show()

                    appAnchorState = AppAnchorState.NONE
                } else if (it == Anchor.CloudAnchorState.SUCCESS) {
                    Toast.makeText(this, "Anchor resolved successfully", Toast.LENGTH_LONG).show()

                    appAnchorState = AppAnchorState.RESOLVED
                }
            }
        }

    }

    private fun showMessageWitAnchorId(s: String) {
        ResolveDialog(
            this,
            object : ResolveDialog.PositiveButtonListener {
                override fun onPositiveButtonClicked(dialogValue: String) {
                    resolveAnchor(dialogValue)
                }
            },
            s,
            View.GONE,
            View.GONE
        ).show()
    }

    private fun placeObject(fragment: ArFragment, anchor: Anchor?) {
        var view = layoutInflater.inflate(R.layout.bottle_station, null);
        ViewRenderable.builder()
            .setView(fragment.context, view)
            .build()
            .thenAccept { renderable -> addNodeToScene(fragment, anchor, renderable) }
            .exceptionally { throwable ->
                val builder = android.app.AlertDialog.Builder(this)
                builder.setMessage(throwable.message)
                    .setTitle("Error!")
                val dialog = builder.create()
                dialog.show()
                null
            }
    }

    private fun addNodeToScene(fragment: ArFragment, anchor: Anchor?, renderable: Renderable) {
        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(fragment.transformationSystem)
        node.setParent(anchorNode)
        node.renderable = renderable;
        // Long as the node is not being detached, I can keep adding childs to the ARFragment.
        fragment.arSceneView.scene.addChild(anchorNode);
        node.select()
    }
}
