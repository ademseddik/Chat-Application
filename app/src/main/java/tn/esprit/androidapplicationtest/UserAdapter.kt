package tn.esprit.androidapplicationtest

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import tn.esprit.androidapplicationtest.databinding.UserIconItemBinding
import java.io.IOException

class UserAdapter(private val context: Context, private val users: List<User>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: UserIconItemBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(user: User) {
            binding.userName.text = user.name
            Glide.with(context)
                .load(user.imageResourceId) // Load image from resource ID
                .into(binding.userIcon)
            // Set click listener
            binding.root.setOnClickListener {
                // Call function to create or get conversation
                createOrGetConversation(user.id.toString(), user.name, user.imageResourceId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = UserIconItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }
    private fun createOrGetConversation(clickedUserId: String, senderName: String, senderImageUrl: Int) {
        val currentUserId = "participant2" // Your current user ID
        val url = "http://10.0.2.2:9090/conversation/$currentUserId/$senderName"

        val request = Request.Builder()
            .url(url)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body()?.string()
                    val jsonObject = JSONObject(responseData)
                    val conversationId = jsonObject.getString("_id")

                    // Navigate to ChatScreen and pass necessary data
                    val intent = Intent(context, MessengerActivity::class.java).apply {
                        putExtra("currentUserId", currentUserId)
                        putExtra("clickedUserId", clickedUserId)
                        putExtra("conversationId", conversationId)
                        putExtra("senderName", senderName)
                        putExtra("senderImageUrl", senderImageUrl)
                    }
                    context.startActivity(intent)
                } else {
                    // Handle unsuccessful response
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // Handle failure
            }
        })
    }
}
