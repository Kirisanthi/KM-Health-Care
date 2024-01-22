// FeedbackAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.health.kmhealthcare.Models.UserData2
import com.health.kmhealthcare.R

class FeedbackAdapter(private val feedbackList: List<UserData2>) :
    RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>() {

    // ViewHolder class for the feedback items
    class FeedbackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textFieldDataTextView: TextView = itemView.findViewById(R.id.textFieldDataTextView)
        val ratingTextView: TextView = itemView.findViewById(R.id.ratingTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        // Inflate the item layout and return a new instance of FeedbackViewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_feedback, parent, false)
        return FeedbackViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        // Bind data to views based on the position in the list
        val feedback = feedbackList[position]

        // Set data to views
        holder.textFieldDataTextView.text = "Feedback: ${feedback.etpaOpinion}"
        holder.ratingTextView.text = "Rating: ${feedback.rating}"
        holder.dateTextView.text = "Date: ${feedback.defaultDate}"
    }

    override fun getItemCount(): Int {
        // Return the total number of items in the list
        return feedbackList.size
    }
}
