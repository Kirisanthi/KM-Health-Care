package com.health.kmhealthcare

import FeedbackAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.health.kmhealthcare.Models.UserData2
import com.health.kmhealthcare.R
import com.google.firebase.database.*

class PaHomeFragment : Fragment() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var feedbackAdapter: FeedbackAdapter
    private val userList = mutableListOf<UserData2>()
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pa_home, container, false)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize the adapter here
        feedbackAdapter = FeedbackAdapter(userList)
        recyclerView.adapter = feedbackAdapter

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("RecievedFeedback")

        // Retrieve data
        retrieveUserData()

        // Initialize SearchView
//        searchView = view.findViewById(R.id.searchView)
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return false
//            }
//
////            override fun onQueryTextChange(newText: String?): Boolean {
////                feedbackAdapter.filter.filter(newText)
////                return false
////            }
//        })

        return view
    }

    private fun retrieveUserData() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("HomeFragment", "Data changed. Snapshot: ${snapshot.value}")
                if (snapshot.exists()) {
                    userList.clear()

                    for (userSnapshot in snapshot.children) {
                        val userData = userSnapshot.getValue(UserData2::class.java)
                        userData?.let {
                            userList.add(it)
                        }
                    }

                    Log.d("HomeFragment", "User list size: ${userList.size}")

                    // Update the adapter with the updated userList
                    feedbackAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Database error: $error")
                // Handle errors
            }
        })
    }
}
