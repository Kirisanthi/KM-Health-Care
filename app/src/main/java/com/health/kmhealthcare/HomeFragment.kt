package com.health.kmhealthcare

import UserAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.health.kmhealthcare.Models.User
import com.health.kmhealthcare.R
import com.google.firebase.database.*


class HomeFragment : Fragment() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private val userList = mutableListOf<User>()
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize the adapter here
        userAdapter = UserAdapter(userList)
        recyclerView.adapter = userAdapter

        // Initialize Firebase
        databaseReference =
            FirebaseDatabase.getInstance().getReference("doctor/doctorsProfileDetails")

        // Retrieve data
        retrieveUserData()


        // Initialize SearchView
        searchView = view.findViewById(R.id.searchView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                userAdapter.filter.filter(newText)
                return false
            }
        })

        return view
    }


    private fun retrieveUserData() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("HomeFragment", "Data changed. Snapshot: ${snapshot.value}")
                if (snapshot.exists()) {
                    userList.clear()

                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        user?.let {
                            userList.add(user)
                        }
                    }

                    Log.d("HomeFragment", "User list size: ${userList.size}")

                    // Update the adapter with the updated userList
                    userAdapter.updateUserList(userList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Database error: $error")
                // Handle errors
            }
        })
    }


}