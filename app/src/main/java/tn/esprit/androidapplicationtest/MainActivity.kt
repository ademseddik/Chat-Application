package tn.esprit.androidapplicationtest

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class Conversation(
    val _id: String,
    val participants: List<String>,
    val messages: List<Message>
)

data class Message(
    val _id: String,
    val sender: Sender,
    val content: String
)
data class User(
    val id: Int,
    val name: String,
    val imageResourceId: Int // Resource ID for the user's image
)

val sam = User(5, "Sam", R.drawable.sam)
val steven = User(7, "Steven", R.drawable.steven)
val olivia = User(4, "Olivia", R.drawable.olivia)
val john = User(3, "John", R.drawable.john)
val greg = User(1, "Greg", R.drawable.greg)


val users: List<User> = listOf(sam, steven, olivia, john, greg)

data class Sender(
    val name: String
)
const val currentUser= "participant2"
interface ConversationService {
    @GET("conversation/$currentUser")
    fun getConversations(): Call<List<Conversation>>
}

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewUser: RecyclerView
    private lateinit var adapter: ContactAdapter
    private lateinit var adapterUser: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerViewUser = findViewById(R.id.userIconRecyclerView)
        displayUsers(users)

        // Initialize Retrofit and make network call
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:9090/") // Ensure the URL ends with "/"
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ConversationService::class.java)
        service.getConversations().enqueue(object : Callback<List<Conversation>> {

            override fun onResponse(
                call: Call<List<Conversation>>,
                response: Response<List<Conversation>>
            ) {
                if (response.isSuccessful) {
                    val conversations = response.body()
                    conversations?.let {
                        Log.d("MainActivity", "Number of conversations received: ${conversations.size}")
                        displayConversations(it)
                        Log.d("MainActivity", "Number of users received: ${users.size}")

                    }
                    Log.d("MainActivity", "Response: $response")
                } else {
                    Log.e("MainActivity", "Failed to fetch data: ${response.code()}")
                    Toast.makeText(this@MainActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Conversation>>, t: Throwable) {
                Log.e("MainActivity", "Network Error: ${t.message}")
                Toast.makeText(this@MainActivity, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayConversations(conversations: List<Conversation>) {
        adapter = ContactAdapter(this, conversations )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

    }
    private fun displayUsers(users: List<User>) {
        adapterUser = UserAdapter(this, users)
        recyclerViewUser.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewUser.adapter = adapterUser
    }

}
