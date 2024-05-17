package tn.esprit.androidapplicationtest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
//import io.agora.agorauikit_android.AgoraConnectionData
//import io.agora.agorauikit_android.AgoraVideoViewer


class VideoCallActivity : AppCompatActivity() {
  //  val agView = AgoraVideoViewer(this, AgoraConnectionData("d79ab7f5156d4cd1a683fe6e24506a6f"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)

        // Find the FrameLayout in the layout
      //  val videoContainer = findViewById<FrameLayout>(R.id.video_container)
/*
        // Initialize AgoraVideoViewer
// Fill the MainActivity with this view
        this.addContentView(
            agView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        // Add AgoraVideoViewer to the FrameLayout
        videoContainer.addView(agView)

        // Join channel "test"
        agView.join("test")*/
    }
}
