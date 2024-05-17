
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import tn.esprit.androidapplicationtest.Conversation
import tn.esprit.androidapplicationtest.DeleteMessageRequest
import tn.esprit.androidapplicationtest.TranslationConfig
import tn.esprit.androidapplicationtest.Translationrequest

import tn.esprit.androidapplicationtest.Message2
import tn.esprit.androidapplicationtest.MessengerActivity
import tn.esprit.androidapplicationtest.R
val messengerActivity = MessengerActivity()
interface MessageService {
    @POST("translateconfig")
    fun translateConfig(
        @Body request: TranslationConfig
    ): Call<String>
    @POST("translatemessage")
    fun translatemessage(
        @Body request: Translationrequest
    ): Call<String>
    @POST("deletemessages")
    fun deletemessage(
        @Body request: DeleteMessageRequest
    ): Call<DeleteMessageRequest>
    @PUT("deleteglobaly")
    fun deletemessage2(
        @Body request: DeleteMessageRequest
    ): Call<DeleteMessageRequest>
}
class ChatAdapter(private val messages: List<Message2>, private val currentUserId: String,private val context: Context) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Views for the left side layout (received messages)
        val messageText: TextView = view.findViewById(R.id.textMessage)
        val timeText: TextView = view.findViewById(R.id.heure)
        val hide1:  TextView = view.findViewById(R.id.espace1)
        val hide2:  TextView = view.findViewById(R.id.espace2)
        val phote: ShapeableImageView = view.findViewById(R.id.photo)
        val cart: MaterialCardView =view.findViewById(R.id.con)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.messageitem, parent, false)
        return ViewHolder(view)
    }
    private fun showDeleteDialog(messageId: String) {
        val dialog = BottomSheetDialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.deleteoption)
        var deleteforallbutton = dialog.findViewById<TextView>(R.id.deleteforallbutton)
        var attachbutton = dialog.findViewById<TextView>(R.id.deleteonlyforyou)
        deleteforallbutton?.setOnClickListener{
            deleteMessage2(messageId)

            Toast.makeText(context, "You have delete the message for all ", Toast.LENGTH_SHORT).show()
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"  // Allow any file type
            dialog.dismiss()

        }
        attachbutton?.setOnClickListener {
            deleteMessage(messageId)

            Toast.makeText(context, "You have delete the message only for you", Toast.LENGTH_SHORT).show()
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"  // Allow any file type
            dialog.dismiss()


        }
        dialog.show()
    }
    private fun showTranslationConfigurationDialog(conversationId: String) {
        val dialog = BottomSheetDialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.translate_config)
        val saveButton = dialog.findViewById<Button>(R.id.buttonsave)
        val languageInput = dialog.findViewById<EditText>(R.id.langugesellector)

        saveButton?.setOnClickListener {
            val selectedLanguage = languageInput?.text.toString().trim()
            if (selectedLanguage.isNotEmpty()) {
                addtranslationconfiguration(conversationId,selectedLanguage)
            } else {
                Toast.makeText(context, "Please enter a language", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        holder.messageText.text = message.content
        holder.timeText.text = message.timestamp
        holder.cart.setOnLongClickListener {
            // Show a context menu for the message
            val popupMenu = PopupMenu(holder.itemView.context, holder.messageText)
            popupMenu.menuInflater.inflate(R.menu.message_context_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.Translate -> {
                        translatemessage(message.conversation,message.content)
                        //here you hundle if the translation is found or not
                        //in the case of its not found it will popup a toast and say you must configure languge
                        // in the other case i will hundle it

                        true
                    }
                    R.id.delete_message -> {
                        showDeleteDialog(message._id)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
            true // Indicate that the long click event is consumed
        }
        if (message.sender == currentUserId) {
            // Message sent by the current user
            holder.hide1.visibility=View.GONE
            holder.hide2.visibility=View.GONE
            holder.phote.visibility=View.GONE
            holder.cart.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.softred))
            if(message.content=="this message has been deleted"){
                holder.cart.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.softgray))
            }
        }else
        {
            holder.hide1.visibility=View.VISIBLE
            holder.hide2.visibility=View.VISIBLE
            holder.phote.visibility=View.VISIBLE
            holder.cart.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.msgC))
            if(message.content=="this message has been deleted"){
                holder.cart.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.softgray))
            }

        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.sender == currentUserId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }
    private fun deleteMessage2(messageId: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:9090") // Update with your server URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(MessageService::class.java)
        val request = DeleteMessageRequest(messageId, currentUserId)
        service.deletemessage2(request).enqueue(object : Callback<DeleteMessageRequest> {
            override fun onResponse(
                call: Call<DeleteMessageRequest>,
                response: Response<DeleteMessageRequest>
            ) {
                if (response.isSuccessful) {
                    // Deletion successful
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(context, "Failed to delete message", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DeleteMessageRequest>, t: Throwable) {
                // Handle network or unexpected errors
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        }) // Closing parenthesis for enqueue method
    } // Closing parenthesis for deleteMessage method
    private fun deleteMessage(messageId: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:9090") // Update with your server URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(MessageService::class.java)
        val request = DeleteMessageRequest(messageId, currentUserId)
        service.deletemessage(request).enqueue(object : Callback<DeleteMessageRequest> {
            override fun onResponse(
                call: Call<DeleteMessageRequest>,
                response: Response<DeleteMessageRequest>
            ) {
                if (response.isSuccessful) {
                    // Deletion successful
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(context, "Failed to delete message", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DeleteMessageRequest>, t: Throwable) {
                // Handle network or unexpected errors
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        }) // Closing parenthesis for enqueue method
    }
    private fun addtranslationconfiguration(conversationId: String,selectedLanguage: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:9090") // Update with your server URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val request = TranslationConfig(conversationId, currentUserId, selectedLanguage)
        val service = retrofit.create(MessageService::class.java)
        service.translateConfig(request).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if (response.isSuccessful) {
                    val configResponse = response.body()
                    Toast.makeText(context, "You have choosed the "+selectedLanguage , Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to configure translation", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(context, "You have choosed the "+selectedLanguage, Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun translatemessage(conversationId: String, message: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:9090") // Update with your server URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(MessageService::class.java)
        val request = Translationrequest(conversationId, currentUserId, message)
        service.translatemessage(request).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if (response.isSuccessful) {
                    val configResponse = response.body()
                    Toast.makeText(context, configResponse, Toast.LENGTH_SHORT).show()

                    val translatedMessage = response.body()
                    // Update UI or perform actions with the translated message
                } else {
                    // Handle unsuccessful response
                    if (response.code() == 400) {
                        val errorBody = response.errorBody()?.string()
                        // Check if the error message indicates language configuration is needed
                        if (errorBody?.contains("choose languge before") == true) {
                            showTranslationConfigurationDialog(conversationId)
                            // Show a toast or dialog indicating that language needs to be configured
                            Toast.makeText(context, "Please configure language before translating", Toast.LENGTH_SHORT).show()
                        } else {
                            showTranslationConfigurationDialog(conversationId)
                            // Handle other types of errors if needed
                            Toast.makeText(context, "Please configure language before translating", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        showTranslationConfigurationDialog(conversationId)
                        // Handle other types of errors if needed
                        Toast.makeText(context, "Please configure language before translating", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // Handle network or unexpected errors
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }



}
