import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.health.kmhealthcare.Models.User
import com.health.kmhealthcare.R
import android.widget.Filter
import android.widget.Filterable
import androidx.fragment.app.FragmentActivity
import com.health.kmhealthcare.ViewDoctorsAvailableFragment
import java.util.*
import androidx.fragment.app.FragmentManager
import android.content.ContextWrapper
import android.os.Bundle

class UserAdapter(private val originalUserList: MutableList<User>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>(), Filterable {

    private var userList = originalUserList.toMutableList()

    var onAvailabilityButtonClick: ((String) -> Unit)? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val docName: TextView = itemView.findViewById(R.id.docName)
        val gender: TextView = itemView.findViewById(R.id.gender)
        val docEmail: TextView = itemView.findViewById(R.id.docEmail)
        val docPhone: TextView = itemView.findViewById(R.id.docPhone)
        val docSpecial: TextView = itemView.findViewById(R.id.docSpecial)
        val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
        val availabilityButton: Button = itemView.findViewById(R.id.availabilityButton) // Add this line
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.docName.text = "Doctor Name: ${user.docName}"
        holder.gender.text = "Doctor Gender: ${user.gender}"
        holder.docEmail.text = "Doctor Email: ${user.docEmail}"
        holder.docPhone.text = "Doctor Phone: ${user.docPhone}"
        holder.docSpecial.text = "Specialty: ${user.docSpecial}"
        Log.i("h","docUniId: ${user.DocUniId}")

        Glide.with(holder.itemView.context)
            .load(user.profileImage)
            .placeholder(R.drawable.ic_launcher_circle_background)
            .error(R.drawable.ic_launcher_circle_background)
            .into(holder.profileImage)

        holder.availabilityButton.setOnClickListener {

            val context = it.context
            val fragmentManager: FragmentManager

            onAvailabilityButtonClick?.invoke(user.DocUniId)
            Log.d("UserAdapter", "Doctor Name: ${user.DocUniId}")


            fragmentManager = when {
                context is FragmentActivity -> context.supportFragmentManager
                context is ContextWrapper && context.baseContext is FragmentActivity ->
                    (context.baseContext as FragmentActivity).supportFragmentManager
                else -> {
                    Log.e("UserAdapter", "Error: Context is not an instance of FragmentActivity")
                    return@setOnClickListener
                }
            }

            val bundle = Bundle()
            bundle.putString("docId", user.DocUniId)

            val viewDoctorsFragment = ViewDoctorsAvailableFragment()
            viewDoctorsFragment.arguments = bundle



            val transaction = fragmentManager.beginTransaction()
//            transaction.replace(R.id.fragment_container, ViewDoctorsAvailableFragment())
            transaction.replace(R.id.fragment_container, viewDoctorsFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateUserList(updatedUserList: List<User>) {
        userList.clear()
        userList.addAll(updatedUserList)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<User>()

                if (constraint.isNullOrBlank()) {
                    filteredList.addAll(originalUserList)
                } else {
                    val filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim()

                    for (user in originalUserList) {
                        if (user.docSpecial.toLowerCase(Locale.ROOT).contains(filterPattern)) {
                            filteredList.add(user)
                        }
                    }
                }

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                userList.clear()
                userList.addAll(results?.values as List<User>)
                notifyDataSetChanged()
            }


        }
    }
}